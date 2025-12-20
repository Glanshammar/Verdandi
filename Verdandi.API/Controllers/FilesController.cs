using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.ComponentModel.DataAnnotations;
using Verdandi.API.Context;
using Verdandi.API.DTO;
using Verdandi.API.Entities;

namespace Verdandi.API.Controllers;

[ApiController]
[Route("api/[controller]")]
public class FilesController : ControllerBase
{
    private readonly ApplicationDbContext _context;
    private readonly ILogger<FilesController> _logger;

    public FilesController(ApplicationDbContext context, ILogger<FilesController> logger)
    {
        _context = context;
        _logger = logger;
    }

    // Get all/selective files
    [HttpGet]
    [ProducesResponseType(typeof(IEnumerable<Files>), StatusCodes.Status200OK)]
    public async Task<ActionResult<IEnumerable<Files>>> GetFiles(
        [FromQuery] string? search = null,
        [FromQuery] string? fileType = null,
        [FromQuery] DateTime? minCreated = null)
    {
        try
        {
            var query = _context.Files.AsNoTracking();
            
            if (!string.IsNullOrEmpty(search))
                query = query.Where(d => EF.Functions.Like(d.Name, $"%{search}%") || 
                                         EF.Functions.Like(d.FilePath, $"%{search}%"));
            
            if (!string.IsNullOrEmpty(fileType))
            {
                var categoryMap = new Dictionary<string, string[]>
                {
                    ["audio"] = [".mp3", ".wav", ".flac", ".aac"],
                    ["image"] = [".jpg", ".png", ".gif", ".webp"],
                    ["video"] = [".mp4", ".avi", ".mkv", ".webm"],
                    ["document"] = [".pdf", ".docx", ".txt", ".md"]
                };

                // Split and clean file types (e.g. "audio,image,txt")
                var fileTypeTokens = fileType
                    .Split(',', StringSplitOptions.RemoveEmptyEntries | StringSplitOptions.TrimEntries)
                    .Select(t => t.ToLower())
                    .ToList();

                // Collect all extensions for the provided types
                var allowedExtensions = new HashSet<string>(
                    fileTypeTokens
                        .SelectMany(t => categoryMap.TryGetValue(t, out var exts) ? exts : [t])
                );

                query = query.Where(f => allowedExtensions.Contains(f.FileType.ToLower()));
            }
                
            if (minCreated.HasValue)
            {
                var minCreatedUtc = DateTime.SpecifyKind(minCreated.Value.Date, DateTimeKind.Utc);
                query = query.Where(d => d.TimeCreated >= minCreatedUtc);
            }

            var filesList = await query.ToListAsync();
            return Ok(filesList);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting files.");
            return StatusCode(500, new { error = "An error occurred while retrieving files." });
        }
    }

    // Get a file by ID
    [HttpGet("{id}")]
    [ProducesResponseType(typeof(Files), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<Files>> GetFile(int id)
    {
        try
        {
            var file = await _context.Files.FindAsync(id);
            
            if (file == null)
            {
                return NotFound(new { error = $"File with ID {id} not found." });
            }
            
            return Ok(file);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting file with ID {DocumentId}.", id);
            return StatusCode(500, new { error = "An error occurred while retrieving the file." });
        }
    }

    // Create file
    [HttpPost]
    [ProducesResponseType(typeof(object), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<ActionResult> CreateFile([FromBody] FileDto fileDto)
    {
        try
        {
            var existingFile = await _context.Files.FirstOrDefaultAsync(file => file.Name == fileDto.Name);
            if (existingFile != null)
            {
                return Conflict(new { error = "A file with this name already exists." });
            }
            
            var validationResults = new List<ValidationResult>();
            var validationContext = new ValidationContext(fileDto);
            bool isValid = Validator.TryValidateObject(fileDto, validationContext, validationResults, true);
            
            if (!isValid)
            {
                return BadRequest(new { errors = validationResults.Select(v => v.ErrorMessage) });
            }
            
            var file = new Files
            {
                Name = fileDto.Name,
                FileType = fileDto.FileType,
                FilePath = Path.Combine(FilePaths.GetFullFilePath(), fileDto.Name + fileDto.FileType).Replace('\\', '/')
            };
            
            _context.Files.Add(file);
            await _context.SaveChangesAsync();
            
            Directory.CreateDirectory(FilePaths.GetFullFilePath());
            var fileStream = System.IO.File.Create(file.FilePath);
            await fileStream.DisposeAsync();
            
            return CreatedAtAction(nameof(GetFile), new { id = file.Id }, new
            {
                file.Id,
                file.Name,
                file.FileType,
                file.FilePath,
                file.TimeCreated
            });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error creating document");
            return StatusCode(500, new { error = "An error occurred while creating the document" });
        }
    }

    // Update a file by ID
    [HttpPut("{id}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult> UpdateDocument(int id, [FromBody] UpdateFileDto fileDto)
    {
        try
        {
            var file = await _context.Files.FindAsync(id);
            if (file == null)
            {
                return NotFound(new { error = $"Document with ID {id} not found" });
            }

            if (!string.IsNullOrEmpty(fileDto.Name) && file.Name != fileDto.Name)
            {
                var nameExists = await _context.Files.AnyAsync(d => d.Name == fileDto.Name && d.Id != id);
                if (nameExists)
                {
                    return Conflict(new { error = "A document with this name already exists." });
                }
            }
            
            var validationResults = new List<ValidationResult>();
            var validationContext = new ValidationContext(fileDto);
            bool isValid = Validator.TryValidateObject(fileDto, validationContext, validationResults, true);
            
            if (!isValid)
            {
                return BadRequest(new { errors = validationResults.Select(v => v.ErrorMessage) });
            }

            if (!string.IsNullOrEmpty(fileDto.FilePath))
            {
                string newFullFilePath = fileDto.FilePath;
                bool isDirectory = newFullFilePath.EndsWith(Path.DirectorySeparatorChar) || 
                                   newFullFilePath.EndsWith('/');
            
                if (isDirectory)
                {
                    string fileName = fileDto.Name ?? file.Name;
                    string fileType = fileDto.FileType ?? file.FileType;
                    newFullFilePath = Path.Combine(newFullFilePath, fileName + fileType).Replace('\\', '/');
                }
                else
                {
                    newFullFilePath = newFullFilePath.Replace('\\', '/');
                }
            
                string? directoryPath = Path.GetDirectoryName(newFullFilePath);
                if (!string.IsNullOrEmpty(directoryPath))
                {
                    Directory.CreateDirectory(directoryPath);
                }
            
                if (file.FilePath != newFullFilePath)
                {
                    if (System.IO.File.Exists(file.FilePath))
                    {
                        System.IO.File.Move(file.FilePath, newFullFilePath);
                    }
                    else
                    {
                        await using var fileStream = System.IO.File.Create(newFullFilePath);
                    }
                    file.FilePath = newFullFilePath;
                }
            }
        
            if(fileDto.Name != null && file.Name != fileDto.Name)
                file.Name = fileDto.Name;
        
            if(fileDto.FileType != null)
                file.FileType = fileDto.FileType;
        
            file.TimeModified = DateTime.UtcNow;
        
            await _context.SaveChangesAsync();
            return NoContent();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error updating file with ID {FileID}.", id);
            return StatusCode(500, new { error = "An error occurred while updating the file." });
        }
    }

    // Delete a document by ID
    [HttpDelete("{id}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult> DeleteFile(int id)
    {
        try
        {
            var document = await _context.Files.FindAsync(id);
            if (document == null)
            {
                return NotFound(new { error = $"Document with ID {id} not found" });
            }
            
            if (System.IO.File.Exists(document.FilePath))
            {
                System.IO.File.Delete(document.FilePath);
            }
            
            _context.Files.Remove(document);
            await _context.SaveChangesAsync();
            
            return NoContent();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error deleting document with ID {DocumentId}", id);
            return StatusCode(500, new { error = "An error occurred while deleting the document" });
        }
    }
}