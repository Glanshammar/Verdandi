using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Verdandi.API.Entities;

public class Document
{
    [Key]
    [Column("id")]
    public int Id { get; set; }
    
    [Required]
    [MaxLength(50)]
    public string Name { get; set; } = string.Empty;
    
    [MaxLength(250)]
    public string Description { get; set; } = string.Empty;
    
    [Required]
    public DateTime DateCreated { get; set; } =  DateTime.Now;
    
    [Required]
    public DateTime DateModified { get; set; } =  DateTime.Now;
    
    [Required]
    [MinLength(1)]
    [MaxLength(250)]
    public string FilePath { get; set; } = string.Empty;
}