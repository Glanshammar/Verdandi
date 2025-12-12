using System.ComponentModel.DataAnnotations;

namespace Verdandi.API.DTO;

public class CreateUser
{
    [Required]
    [MaxLength(100)]
    [MinLength(1)]
    public string Name { get; set; } = string.Empty;
    
    [Required]
    [MaxLength(100)]
    [MinLength(1)]
    [EmailAddress]
    public string Email { get; set; } = string.Empty;
    
    [Required]
    [MaxLength(128)]
    [MinLength(5)]
    public string Password { get; set; } = string.Empty;
}