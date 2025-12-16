using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.ComponentModel.DataAnnotations;
using Verdandi.API.Context;
using Verdandi.API.DTO;
using Verdandi.API.Entities;

namespace Verdandi.API.Controllers;

[ApiController]
[Route("api/[controller]")]
public class DocumentsController : ControllerBase
{
    private readonly ApplicationDbContext _context;
    private readonly ILogger<DocumentsController> _logger;

    public DocumentsController(ApplicationDbContext context, ILogger<DocumentsController> logger)
    {
        _context = context;
        _logger = logger;
    }

    // Get all documents
    [HttpGet]
    [ProducesResponseType(typeof(IEnumerable<Document>), StatusCodes.Status200OK)]
    public async Task<ActionResult<IEnumerable<Document>>> GetDocuments()
    {
        try
        {
            var documents = await _context.Documents.ToListAsync();
            return Ok(documents);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting documents");
            return StatusCode(500, new { error = "An error occurred while retrieving documents" });
        }
    }

    // Get a document by ID
    [HttpGet("{id}")]
    [ProducesResponseType(typeof(Document), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<Document>> GetDocument(int id)
    {
        try
        {
            var document = await _context.Documents.FindAsync(id);
            
            if (document == null)
            {
                return NotFound(new { error = $"Document with ID {id} not found" });
            }
            
            return Ok(document);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error getting document with ID {DocumentId}", id);
            return StatusCode(500, new { error = "An error occurred while retrieving the document" });
        }
    }

    // Create document
    [HttpPost]
    [ProducesResponseType(typeof(object), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status409Conflict)]
    public async Task<ActionResult> CreateDocument([FromBody] DocumentDto docDto)
    {
        try
        {
            var existingDocument = await _context.Documents.FirstOrDefaultAsync(doc => doc.Name == docDto.Name);
            if (existingDocument != null)
            {
                return Conflict(new { error = "A document with this name already exists." });
            }
            
            var validationResults = new List<ValidationResult>();
            var validationContext = new ValidationContext(docDto);
            bool isValid = Validator.TryValidateObject(docDto, validationContext, validationResults, true);
            
            if (!isValid)
            {
                return BadRequest(new { errors = validationResults.Select(v => v.ErrorMessage) });
            }
            
            var document = new Document
            {
                Name = docDto.Name,
                FileType = docDto.FileType,
                FilePath = docDto.GetFullFilePath()
            };
            
            _context.Documents.Add(document);
            await _context.SaveChangesAsync();
            
            string fullFilePath = Path.Combine(docDto.FilePath, docDto.Name + docDto.FileType);
            Directory.CreateDirectory(docDto.GetFullFilePath());
            var fileStream = System.IO.File.Create(fullFilePath);
            await fileStream.DisposeAsync();
            document.FilePath = fullFilePath;
            
            return CreatedAtAction(nameof(GetDocument), new { id = document.Id }, new
            {
                document.Id,
                document.Name,
                document.FileType,
                document.FilePath,
                document.TimeCreated
            });
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error creating document");
            return StatusCode(500, new { error = "An error occurred while creating the document" });
        }
    }

    // Update a document by ID
    [HttpPut("{id}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult> UpdateDocument(int id, [FromBody] DocumentDto docDto)
    {
        try
        {
            var document = await _context.Documents.FindAsync(id);
            if (document == null)
            {
                return NotFound(new { error = $"Document with ID {id} not found" });
            }

            if (document.Name != docDto.Name)
            {
                var nameExists = await _context.Documents.AnyAsync(d => d.Name == docDto.Name);
                if (nameExists)
                {
                    return Conflict(new { error = "A document with this name already exists." });
                }
            }
            
            var validationResults = new List<ValidationResult>();
            var validationContext = new ValidationContext(docDto);
            bool isValid = Validator.TryValidateObject(docDto, validationContext, validationResults, true);
            
            if (!isValid)
            {
                return BadRequest(new { errors = validationResults.Select(v => v.ErrorMessage) });
            }
            
            document.Name = docDto.Name;
            document.FileType = docDto.FileType;
            document.FilePath = docDto.FilePath;
            document.TimeModified = DateTime.UtcNow;
            
            await _context.SaveChangesAsync();
            
            return NoContent();
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Error updating document with ID {DocumentId}", id);
            return StatusCode(500, new { error = "An error occurred while updating the document" });
        }
    }

    // Delete a document by ID
    [HttpDelete("{id}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult> DeleteDocument(int id)
    {
        try
        {
            var document = await _context.Documents.FindAsync(id);
            if (document == null)
            {
                return NotFound(new { error = $"Document with ID {id} not found" });
            }
            
            if (System.IO.File.Exists(document.FilePath))
            {
                System.IO.File.Delete(document.FilePath);
            }
            
            _context.Documents.Remove(document);
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