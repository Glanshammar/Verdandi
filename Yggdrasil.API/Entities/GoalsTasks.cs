using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace Yggdrasil.API.Entities;

public enum Priority
{
    LittleImportant = 0,
    MediumImportant = 1,
    VeryImportant = 2,
    CriticalImportant = 3,
}

public class TaskStep
{
    [Key]
    public int Id { get; set; }
    
    [Required]
    public int TaskId { get; set; }
    
    [Required]
    [MinLength(1)]
    [MaxLength(50)]
    public string StepTitle { get; set; } = string.Empty;
    
    [Required]
    public bool IsCompleted { get; set; } = false;
}

public class Task
{
    [Key]
    public int Id { get; set; }
    
    [Required]
    [MaxLength(50)]
    public string TaskName { get; set; } = string.Empty;
    
    [Required]
    public Priority Priority { get; set; } = Priority.MediumImportant;
    
    [Required]
    [MaxLength(500)]
    public string TaskDescription { get; set; } = string.Empty;
    
    [Required]
    public bool IsCompleted { get; set; } = false;
    
    public virtual ICollection<GoalTask> GoalTasks { get; set; } = new List<GoalTask>();
    public virtual ICollection<TaskStep> TaskSteps { get; set; } = new List<TaskStep>();
}

public class Goal
{
    [Key]
    public int Id { get; set; }
    
    [Required]
    [MaxLength(100)]
    public string Name { get; set; } = string.Empty;
    
    [Required]
    public Priority Priority { get; set; } = Priority.MediumImportant;
    
    [MaxLength(500)]
    public string Description { get; set; } = string.Empty;
    
    [Required]
    public bool IsCompleted { get; set; } = false;
    
    public virtual ICollection<GoalTask> GoalTasks { get; set; } = new List<GoalTask>();
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
    
    public DateTime? CompletedDate { get; set; }
}