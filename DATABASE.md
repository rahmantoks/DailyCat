# DailyCat PostgreSQL Setup

## Quick Start

1. **Start PostgreSQL container:**
   ```bash
   docker-compose up -d
   ```

2. **Set your Cat API key:**
   ```bash
   export CAT_API_KEY="your-api-key-here"
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Sync cats from The Cat API:**
   ```bash
   curl -X POST "http://localhost:8080/api/admin/sync?count=10"
   ```

5. **View a random cat:**
   ```
   http://localhost:8080
   ```

## How It Works

- **Database**: PostgreSQL runs in Docker container on port 5432
- **Scheduled Sync**: Every day at midnight, 10 random cats are fetched from The Cat API and saved to the database
- **Manual Sync**: Use `/api/admin/sync?count=N` to fetch cats on demand
- **UI Request**: Frontend fetches random cat from local database (not directly from Cat API)

## Database Connection

- **Host**: localhost:5432
- **Database**: 
- **Username**: 
- **Password**: 

## Useful Commands

```bash
# Stop PostgreSQL
docker-compose down

# Stop and remove data
docker-compose down -v

# View logs
docker-compose logs -f postgres

# Connect to database
docker exec -it dailycat-db psql -U dailycat -d dailycat

# Check tables
docker exec -it dailycat-db psql -U dailycat -d dailycat -c "\dt"
```
