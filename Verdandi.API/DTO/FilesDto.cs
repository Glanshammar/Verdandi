using System.ComponentModel.DataAnnotations;

namespace Verdandi.API.DTO;

public static class FilePaths
{
    public static string GetFullFilePath(string? fileName = null, string? fileType = null, string? filePath = null)
    {
        if (!string.IsNullOrWhiteSpace(filePath))
        {
            return filePath;
        }
            
        return Path.Combine(
            Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments), 
            "Verdandi", 
            $"{fileName}{fileType}"
        );
    }
}

public class FileDto
{
    [Required]
    [MinLength(1), MaxLength(50)]
    public string Name { get; set; } = string.Empty;

    [Required]
    [MinLength(2), MaxLength(50)]
    public string FileType { get; set; } = ".file";
    
    [MaxLength(500)]
    public string FilePath { get; set; } = string.Empty;
}

public class UpdateFileDto
{
    [MaxLength(50)]
    public string? Name { get; set; }
    
    public string? FileType { get; set; }
    
    [MaxLength(500)]
    public string? FilePath { get; set; }
}