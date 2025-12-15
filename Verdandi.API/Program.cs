using System.ComponentModel.DataAnnotations;
using Microsoft.EntityFrameworkCore;
using Verdandi.API;
using Verdandi.API.Context;
using Verdandi.API.DTO;
using Verdandi.API.Entities;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddOpenApi();
builder.Services.AddDbContext<ApplicationDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DevelopmentConnection")));

builder.Services.AddControllers();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
    
    using var scope = app.Services.CreateScope();
    var dbContext = scope.ServiceProvider.GetRequiredService<ApplicationDbContext>();
    await dbContext.Database.MigrateAsync();
}

app.UseHttpsRedirection();
app.MapControllers();

// Simple status endpoint
app.MapGet("/api/status", () =>
    {
        return Results.Ok(new { 
            status = "ok", 
            message = "API is online and running",
            timestamp = DateTime.UtcNow
        });
    })
    .WithName("GetApiStatus");


await app.RunAsync();