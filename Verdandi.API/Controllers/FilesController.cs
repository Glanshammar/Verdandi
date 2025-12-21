using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.ComponentModel.DataAnnotations;
using System.IO.Compression;
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
                return NotFound(new { error = $"File with ID {id} not found." });
            
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
                return Conflict(new { error = "A file with this name already exists." });
            
            var validationResults = new List<ValidationResult>();
            var validationContext = new ValidationContext(fileDto);
            bool isValid = Validator.TryValidateObject(fileDto, validationContext, validationResults, true);
            
            if (!isValid)
                return BadRequest(new { errors = validationResults.Select(v => v.ErrorMessage) });
            
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
                return NotFound(new { error = $"Document with ID {id} not found" });

            if (!string.IsNullOrEmpty(fileDto.Name) && file.Name != fileDto.Name)
            {
                var nameExists = await _context.Files.AnyAsync(d => d.Name == fileDto.Name && d.Id != id);
                if (nameExists)
                    return Conflict(new { error = "A document with this name already exists." });
            }
            
            var validationResults = new List<ValidationResult>();
            var validationContext = new ValidationContext(fileDto);
            bool isValid = Validator.TryValidateObject(fileDto, validationContext, validationResults, true);
            
            if (!isValid)
                return BadRequest(new { errors = validationResults.Select(v => v.ErrorMessage) });

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
                    Directory.CreateDirectory(directoryPath);
            
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

    // Delete a file by ID
    [HttpDelete("{id}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult> DeleteFile(int id)
    {
        try
        {
            var file = await _context.Files.FindAsync(id);
            
            if (file == null)
                return NotFound(new { error = $"Document with ID {id} not found" });
            
            if (System.IO.File.Exists(file.FilePath))
                System.IO.File.Delete(file.FilePath);
            
            _context.Files.Remove(file);
            await _context.SaveChangesAsync();
            
            return NoContent();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error deleting document with ID {DocumentId}", id);
            return StatusCode(500, new { error = "An error occurred while deleting the document" });
        }
    }
    
    // Download one or more documents by ID
    [HttpPost("download")]
    [ProducesResponseType(typeof(FileResult), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> DownloadDocuments([FromBody] DownloadRequestDto downloadRequest)
    {
        try
        {
            if (downloadRequest.Ids is null || downloadRequest.Ids.Count == 0)
                return BadRequest(new { error = "No document IDs provided" });

            var files = await _context.Files
                .Where(d => downloadRequest.Ids.Contains(d.Id))
                .ToListAsync();

            if (files.Count == 0)
                return NotFound(new { error = "No documents found with the provided IDs" });

            var foundIds = files.Select(d => d.Id).ToList();
            var missingIds = downloadRequest.Ids.Except(foundIds).ToList();
            
            if (missingIds.Count > 0)
            {
                return NotFound(new { 
                    error = $"Some documents not found", 
                    missingIds = missingIds,
                    foundCount = files.Count,
                    requestedCount = downloadRequest.Ids.Count
                });
            }

            var missingFiles = new List<FileInfoDto>();
            var validFiles = new List<Files>();
            
            foreach (var file in files)
            {
                if (System.IO.File.Exists(file.FilePath))
                    validFiles.Add(file);
                else
                {
                    missingFiles.Add(new FileInfoDto
                    {
                        Id = file.Id,
                        Name = file.Name,
                        FilePath = file.FilePath
                    });
                }
            }

            if (missingFiles.Count > 0)
            {
                return NotFound(new { 
                    error = "Some files were not found", 
                    missingFiles,
                    availableFiles = validFiles.Select(d => new { d.Id, d.Name })
                });
            }

            if (validFiles.Count == 1)
            {
                var file = validFiles[0];
                var fileName = Path.GetFileName(file.FilePath) ?? $"{file.Name}{file.FileType}";
                var fileStream = new FileStream(file.FilePath, FileMode.Open, FileAccess.Read, FileShare.Read);
                var contentType = GetContentType(file.FileType);
                return File(fileStream, contentType, fileName);
            }

            var zipFileName = $"files_{DateTime.UtcNow:yyyyMMdd_HHmmss}.zip";
            
            try
            {
                var memoryStream = new MemoryStream();
    
                await using (var zipArchive = new ZipArchive(memoryStream, ZipArchiveMode.Create, true))
                {
                    var usedEntryNames = new HashSet<string>();
        
                    foreach (var file in validFiles)
                    {
                        var entryName = $"{file.Name}{file.FileType}";
            
                        if (usedEntryNames.Contains(entryName))
                            entryName = $"{file.Id}_{entryName}";
            
                        usedEntryNames.Add(entryName);
            
                        var entry = zipArchive.CreateEntry(entryName, CompressionLevel.Optimal);
                        await using var entryStream = await entry.OpenAsync();
                        await using var fileStream = new FileStream(file.FilePath, FileMode.Open, FileAccess.Read, FileShare.Read);
                        await fileStream.CopyToAsync(entryStream);
                    }
                }
    
                memoryStream.Position = 0;
                return File(memoryStream, "application/zip", zipFileName);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error creating ZIP archive");
                return StatusCode(500, new { error = "An error occurred while creating the ZIP archive" });
            }
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error downloading files");
            return StatusCode(500, new { error = "An error occurred while downloading files" });
        }
    }

    // Download a single file by ID
    [HttpGet("{id}/download")]
    [ProducesResponseType(typeof(FileResult), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> DownloadDocument(int id)
    {
        try
        {
            var file = await _context.Files.FindAsync(id);
            
            if (file == null)
                return NotFound(new { error = $"File with ID {id} not found" });
            
            if (!System.IO.File.Exists(file.FilePath))
            {
                return NotFound(new { 
                    error = $"File not found on disk", 
                    file = new { file.Id, file.Name, file.FilePath }
                });
            }
            
            var fileName = Path.GetFileName(file.FilePath) ?? $"{file.Name}{file.FileType}";
            var contentType = GetContentType(file.FileType);
            var fileStream = new FileStream(file.FilePath, FileMode.Open, FileAccess.Read, FileShare.Read);
            
            return File(fileStream, contentType, fileName);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error downloading document with ID {DocumentId}", id);
            return StatusCode(500, new { error = "An error occurred while downloading the document" });
        }
    }

    private static string GetContentType(string fileType)
    {
        return fileType.ToLower() switch
        {
            ".txt" => "text/plain",
            ".pdf" => "application/pdf",
            ".doc" => "application/msword",
            ".docx" => "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            ".xls" => "application/vnd.ms-excel",
            ".xlsx" => "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            ".jpg" or ".jpeg" => "image/jpeg",
            ".png" => "image/png",
            ".gif" => "image/gif",
            ".mp3" => "audio/mpeg",
            ".mp4" => "video/mp4",
            ".zip" => "application/zip",
            _ => "application/octet-stream"
        };
    }
}