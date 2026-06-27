using System.ComponentModel.DataAnnotations;
using Yggdrasil.API.Entities;

namespace Yggdrasil.API.DTO;

public class GoalDto
{
    [Required]
    [MaxLength(100)]
    public string Name { get; set; } = string.Empty;
    
    [Required]
    [MaxLength(500)]
    public string Description { get; set; } = string.Empty;
    
    public Priority Priority { get; set; } = Priority.MediumImportant;
    
    public bool IsCompleted { get; set; } = false;
}

public class TaskDto
{
    [Required]
    [MaxLength(50)]
    public string TaskName { get; set; } = string.Empty;
    
    [Required]
    [MaxLength(500)]
    public string TaskDescription { get; set; } = string.Empty;
}