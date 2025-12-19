using System.ComponentModel.DataAnnotations;

namespace Verdandi.API.DTO;

public static class FilePaths
{
    public static string GetFullFilePath(string? fileName = null, string? fileType = null, string? filePath = null)
    {
        if (!string.IsNullOrWhiteSpace(filePath))
        {
            return filePath.Replace('\\', '/');
        }
            
        return Path.Combine(
            Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments), 
            "Verdandi", 
            $"{fileName}{fileType}"
        ).Replace('\\', '/');
    }
}

public class DocumentDto
{
    [Required]
    [MaxLength(50)]
    [MinLength(1)]
    public string Name { get; set; } = string.Empty;

    [Required]
    public string FileType { get; set; } = ".txt";
    
    [MaxLength(500)]
    public string FilePath { get; set; } = string.Empty;
}

public class UpdateDocumentDto
{
    [MaxLength(50)]
    public string? Name { get; set; }
    
    public string? FileType { get; set; }
    
    [MaxLength(500)]
    public string? FilePath { get; set; }
}