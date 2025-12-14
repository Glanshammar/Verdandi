using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Verdandi.API.Entities;

public class Document
{
    [Key]
    [Column("id")]
    public int Id { get; set; }
    
    [Required]
    [MinLength(1)]
    [MaxLength(50)]
    [Column("name")]
    public string Name { get; set; } = string.Empty;
    
    [Column("time_created")]
    public DateTime TimeCreated { get; set; } =  DateTime.UtcNow;
    
    [Column("time_modified")]
    public DateTime TimeModified { get; set; } =  DateTime.UtcNow;
    
    [Required]
    [MinLength(2)]
    [MaxLength(500)]
    [Column("file_path")]
    public string FilePath { get; set; } = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments), "Verdandi");

    [Required]
    [MinLength(2)]
    [MaxLength(20)]
    [Column("file_type")]
    public string FileType { get; set; } = ".txt";
}