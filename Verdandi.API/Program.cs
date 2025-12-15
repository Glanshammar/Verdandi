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
app.MapPost("/api/users", async (UserDto userDto, ApplicationDbContext db) =>
    {
        var existingUser = await db.Users.FirstOrDefaultAsync(u => u.Email == userDto.Email);
        if (existingUser != null)
        {
            return Results.Conflict(new { error = "A user with this email already exists." });
        }
        
        var validationResults = new List<ValidationResult>();
        var validationContext = new ValidationContext(userDto);
        bool isValid = Validator.TryValidateObject(userDto, validationContext, validationResults, true);
        
        if (!isValid)
        {
            return Results.BadRequest(new { errors = validationResults.Select(v => v.ErrorMessage) });
        }
        
        var user = new User
        {
            Name = userDto.Name,
            Email = userDto.Email,
            CreatedAt = DateTime.UtcNow
        };
        
        user.SetPassword(userDto.Password);
        db.Users.Add(user);
        await db.SaveChangesAsync();
        
        return Results.Created($"/api/users/{user.Id}", new
        {
            user.Id,
            user.Name,
            user.Email,
            user.CreatedAt
        });
    })
    .WithName("CreateUser")
    .Produces<object>(StatusCodes.Status201Created)
    .Produces<object>(StatusCodes.Status400BadRequest)
    .Produces<object>(StatusCodes.Status409Conflict);

//Get documents
app.MapGet("/api/documents/{id}", async (int id, ApplicationDbContext db) =>
    {
        var document = await db.Documents.FindAsync(id);
        if (document == null)
        {
            return Results.NotFound();
        }
        return Results.Ok(document);
    })
    .WithName("GetDocumentById")
    .Produces<Document>(StatusCodes.Status200OK)
    .Produces(StatusCodes.Status404NotFound);


//Create document
app.MapPost("/api/documents", async (DocumentDto docDto, ApplicationDbContext db) =>
    {
        var existingDocument = await db.Documents.FirstOrDefaultAsync(doc => doc.Name == docDto.Name);
        if (existingDocument != null)
        {
            return Results.Conflict(new { error = "A document with this name already exists." });
        }
        
        var validationResults = new List<ValidationResult>();
        var validationContext = new ValidationContext(docDto);
        bool isValid = Validator.TryValidateObject(docDto, validationContext, validationResults, true);
        
        if (!isValid)
        {
            return Results.BadRequest(new { errors = validationResults.Select(v => v.ErrorMessage) });
        }
        
        var document = new Document
        {
            Name = docDto.Name,
            FileType = docDto.FileType,
            FilePath = docDto.FilePath
        };
        
        db.Documents.Add(document);
        await db.SaveChangesAsync();
        
        return Results.Created($"/api/documents/{document.Id}", new
        {
            document.Id,
            document.Name,
            document.FileType,
            document.FilePath,
            document.TimeCreated
        });
    })
    .WithName("CreateDocument")
    .Produces<object>(StatusCodes.Status201Created)
    .Produces<object>(StatusCodes.Status400BadRequest)
    .Produces<object>(StatusCodes.Status409Conflict);


await app.RunAsync();