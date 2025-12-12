using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Verdandi.API.Entities;

public class Task
{
    [Key]
    public int Id { get; set; }
    
    [Required]
    [MaxLength(50)]
    public string TaskName { get; set; } = string.Empty;
    
    [Required]
    [MaxLength(500)]
    public string TaskDescription { get; set; } = string.Empty;
    
    public virtual ICollection<Goal> Goals { get; set; } = new List<Goal>();
}

public class Goal
{
    [Key]
    public int Id { get; set; }
    
    [Required]
    [MaxLength(100)]
    public string Name { get; set; } = string.Empty;
    
    [MaxLength(500)]
    public string Description { get; set; } = string.Empty;
    
    [Required]
    public bool IsCompleted { get; set; } = false;
    
    public virtual ICollection<Task> Tasks { get; set; } = new List<Task>();
}

public class GoalTask
{
    [Key]
    public int Id { get; set; }
    
    [Required]
    [ForeignKey(nameof(Goal))]
    public int GoalId { get; set; }
    
    [Required]
    [ForeignKey(nameof(Task))]
    public int TaskId { get; set; }
    
    public virtual Goal Goal { get; set; } = null!;
    public virtual Task Task { get; set; } = null!;
    
    // You can add additional properties here if needed
    // For example:
    // public DateTime? CompletedDate { get; set; }
    // public bool IsPrimary { get; set; }
}