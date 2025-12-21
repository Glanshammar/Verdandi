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
    
    public static string GetDownloadsPath()
    {
        string userProfile = Environment.GetFolderPath(Environment.SpecialFolder.UserProfile);
        return Path.Combine(userProfile, "Downloads");
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

public class DownloadRequestDto
{
    [Required]
    [MinLength(1, ErrorMessage = "At least one file ID is required")]
    public List<int>? Ids { get; set; } = new List<int>();
}

public class FileInfoDto
{
    public int Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string FilePath { get; set; } = string.Empty;
}