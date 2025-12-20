using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Verdandi.API.Entities;

public class Files
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
    public DateTime TimeCreated { get; set; } = DateTime.SpecifyKind(DateTime.UtcNow, DateTimeKind.Utc);

    [Column("time_modified")]
    public DateTime TimeModified { get; set; } = DateTime.SpecifyKind(DateTime.UtcNow, DateTimeKind.Utc);
    
    [Required]
    [MinLength(2)]
    [MaxLength(500)]
    [Column("file_path")]
    public string FilePath { get; set; } = string.Empty;

    [MinLength(2)]
    [MaxLength(20)]
    [Column("file_type")]
    public string FileType { get; set; } = ".file";
}