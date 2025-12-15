using System.ComponentModel.DataAnnotations;

namespace Verdandi.API.DTO;

public class DocumentDto
{
    [Required]
    [MaxLength(50)]
    [MinLength(1)]
    public string Name { get; set; } = string.Empty;

    [Required]
    public string FileType { get; set; } = ".txt";
    
    [MinLength(1)]
    [MaxLength(500)]
    public string FilePath { get; set; } = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments), "Verdandi");
}