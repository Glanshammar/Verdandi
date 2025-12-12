
## Startup
1. Install
	* PostgreSQL
	* Npgsql.EntityFrameworkCore.PostgreSQL (NuGet)
	* Microsoft.EntityFrameworkCore.Design (NuGet)
	* Microsoft.EntityFrameworkCore.Tools (NuGet)
2. Add connection string
	``` JSON
	"ConnectionStrings": {
    "DefaultConnection": "Host=localhost;Port=5432;Database=VerdandiDB;Username=postgres;Password=yourpassword"
  }
  ```
3. Scaffold