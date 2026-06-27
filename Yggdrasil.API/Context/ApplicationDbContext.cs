using System;
using Microsoft.EntityFrameworkCore;
using DotNetEnv;
using Yggdrasil.API.Entities;
using Tasks = Yggdrasil.API.Entities.Task;

namespace Yggdrasil.API.Context;

public partial class ApplicationDbContext : DbContext
{
    public ApplicationDbContext()
    {
    }

    public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options)
        : base(options)
    {
    }

    public DbSet<User> Users => Set<User>();
    public DbSet<Files> Files => Set<Files>();
    public DbSet<Goal> Goals => Set<Goal>();
    public DbSet<Tasks> Tasks => Set<Tasks>();
    public DbSet<GoalTask> GoalTasks => Set<GoalTask>();
    public DbSet<TaskStep> TaskSteps => Set<TaskStep>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        // Configure the relationship between Goal and GoalTask
        modelBuilder.Entity<GoalTask>()
            .HasOne(gt => gt.Goal)               // GoalTask has one Goal
            .WithMany(g => g.GoalTasks)     // Goal has many GoalTasks
            .HasForeignKey(gt => gt.GoalId)      // Foreign key is GoalId
            .OnDelete(DeleteBehavior.Cascade);   // Optional: If Goal is deleted, delete its links

        // Configure the relationship between Task and GoalTask
        modelBuilder.Entity<GoalTask>()
            .HasOne(gt => gt.Task)               // GoalTask has one Task
            .WithMany(t => t.GoalTasks)     // Task has many GoalTasks
            .HasForeignKey(gt => gt.TaskId)      // Foreign key is TaskId
            .OnDelete(DeleteBehavior.Cascade);   // Optional: If Task is deleted, delete its links
        
        modelBuilder.Entity<GoalTask>().ToTable("GoalTasks");
    }
}