using System.ComponentModel.DataAnnotations;

namespace Verdandi.API.DTO;

public class GoalDto
{
    [Required]
    [MaxLength(100)]
    public string Name { get; set; } = string.Empty;
    
    [MaxLength(500)]
    public string Description { get; set; } = string.Empty;
    
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