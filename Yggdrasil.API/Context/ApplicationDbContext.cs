using System;
using Microsoft.EntityFrameworkCore;
using DotNetEnv;
using Yggdrasil.API.Entities;
using Entities_Task = Yggdrasil.API.Entities.Task;
using Task = Yggdrasil.API.Entities.Task;

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
    public DbSet<Entities_Task> Tasks => Set<Entities_Task>();
    public DbSet<GoalTask> GoalTasks => Set<GoalTask>();

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        Env.Load();

        var host = Environment.GetEnvironmentVariable("DB_HOST");
        var port = Environment.GetEnvironmentVariable("DB_PORT");
        var db   = Environment.GetEnvironmentVariable("DB_NAME");
        var user = Environment.GetEnvironmentVariable("DB_USER");
        var pass = Environment.GetEnvironmentVariable("DB_PASS");

        var connString =
            $"Host={host};Port={port};Database={db};Username={user};Password={pass}";

        optionsBuilder.UseNpgsql(connString);
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Goal>()
            .HasMany(g => g.Tasks)
            .WithMany(t => t.Goals)
            .UsingEntity<GoalTask>(
                j => j
                    .HasOne(gt => gt.Task)
                    .WithMany()
                    .HasForeignKey(gt => gt.TaskId),
                j => j
                    .HasOne(gt => gt.Goal)
                    .WithMany()
                    .HasForeignKey(gt => gt.GoalId),
                j =>
                {
                    j.HasKey(gt => gt.Id);
                    j.ToTable("GoalTasks");
                });
    }
}