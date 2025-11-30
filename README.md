# Daily Cat Webapp

A minimal full-stack app with a Spring Boot backend and a static frontend showing the Cat of the Day.

## Backend
- Endpoint: `/api/cat-of-the-day` returns JSON with `name`, `imageUrl`, `description`.
- Technology: Spring Boot 3 (Java 21), Maven build.

## Frontend
- Served from `src/main/resources/static/index.html`.
- Fetches the cat JSON and renders the landing page.

## Run with Docker (no local Maven needed)
```bash
# Build image
docker build -t dailycat:local .

# Run container
docker run --rm -p 8080:8080 dailycat:local
```
Then open `http://localhost:8080`.

## Run locally (with Maven)
```bash
# Build
mvn -q -DskipTests package

# Run
mvn spring-boot:run
```
Open `http://localhost:8080`.
 
## Project Structure
- `src/main/java/com/dailycat/DailyCatApplication.java`: Spring Boot app entry
- `src/main/java/com/dailycat/controller/CatController.java`: REST API
- `src/main/java/com/dailycat/model/Cat.java`: DTO model
- `src/main/resources/static/index.html`: Landing page
- `pom.xml`: Maven build config
- `Dockerfile`: Containerized build/run

## Notes
- Currently returns a hardcoded sample cat.
- CORS not required since frontend is served by the same server.

## Configuration
- `src/main/resources/application.yml` reads The Cat API settings:
	- `catapi.base-url` defaults to `https://api.thecatapi.com/v1`.
	- `catapi.api-key` is read from the environment variable `CAT_API_KEY` when set.
- Without `CAT_API_KEY`, the service falls back to a local, hardcoded cat response.

### Set API key (optional)
```bash
export CAT_API_KEY="<your-key>"
mvn spring-boot:run
```