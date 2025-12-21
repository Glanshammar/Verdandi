using System.ComponentModel.DataAnnotations;

namespace Verdandi.API.DTO;

public class FileDto
{
    [Required]
    [MinLength(1), MaxLength(500)]
    public string FilePath { get; set; } = string.Empty;
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