using Microsoft.EntityFrameworkCore;
using Verdandi.API;
using Verdandi.API.Context;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddOpenApi();
builder.Services.AddDbContext<ApplicationDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DevelopmentConnection")));

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

// Get users
app.MapGet("/api/users", async (ApplicationDbContext db) =>
    {
        var users = await db.Users.ToListAsync();
        return Results.Ok(users);
    })
    .WithName("GetUsers");

// Create user
app.MapPost("/api/users", () =>
    {
        return Results.Created();
    })
    .WithName("CreateUser");

await app.RunAsync();