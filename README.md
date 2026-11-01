# Route Planner - Backend

Spring Boot backend for the route planner project. Built as a learning project to implement and understand graph-based routing algorithms (A* search), spatial grid indexing, and OpenStreetMap (OSM) data parsing.

## Features

- A* shortest path algorithm using the Haversine formula for heuristic distance calculation
- SAX streaming parser to extract road networks (`highway` tags) from OpenStreetMap XML files (`.osm`)
- Spatial grid index to map geographic coordinates to the nearest road network node
- Integration with OpenStreetMap Nominatim for address geocoding
- Graph loaded into memory at startup to serve path requests

## Tech Stack

- Spring Boot 4
- Java 21
- Apache Maven (via `./mvnw` wrapper)
- Lombok

## Directory Structure

```text
route-planner-backend/
├── data/
│   └── mangalore.osm                       # Road network XML dataset
├── src/
│   ├── main/
│   │   ├── java/com/project/routeplanner/
│   │   │   ├── config/CorsConfig.java      # CORS configuration (allows localhost:3000)
│   │   │   ├── controller/RoutingController.java # REST API endpoints
│   │   │   ├── graph/GraphBuilder.java     # Constructs graph adjacency list and edge weights
│   │   │   ├── graph/model/                # Graph, GraphNode, GraphEdge, AStarNode
│   │   │   ├── parser/SaxParser.java       # SAX parser runner
│   │   │   ├── parser/OsmHandler.java      # SAX handler for OSM nodes and highway ways
│   │   │   ├── routing/AStarRouter.java    # A* algorithm and nearest node lookup
│   │   │   ├── services/RoutingService.java # Graph management and path calculation
│   │   │   └── services/GeocodingService.java # Client for Nominatim geocoding
│   │   └── resources/application.properties # Configuration file
│   └── test/java/com/project/routeplanner/ # Unit and integration tests
├── mvnw / mvnw.cmd                         # Maven wrapper
└── pom.xml
```

## Getting Started

### Prerequisites

- Java JDK 21 or later
- Maven wrapper is included (`./mvnw`)

### 1. Run the Application

```bash
# On Linux / macOS
./mvnw spring-boot:run

# On Windows
mvnw.cmd spring-boot:run
```

The server starts on port `8080`. On startup, it loads the road network from `data/mangalore.osm` into memory.

## REST API Reference

Base URL: `http://localhost:8080/route`

### 1. Path by Coordinates

Calculates the shortest route between two coordinate points.

- **Method**: `GET`
- **Path**: `/route/pathByLatlng`
- **Parameters**:
  - `lat1` (Double): Start latitude
  - `lng1` (Double): Start longitude
  - `lat2` (Double): End latitude
  - `lng2` (Double): End longitude
- **Example**:
  ```bash
  curl "http://localhost:8080/route/pathByLatlng?lat1=12.87&lng1=74.88&lat2=12.86&lng2=74.85"
  ```
- **Response**:
  ```json
  [
    {"lat": 12.8700748, "lon": 74.8800886},
    {"lat": 12.8700809, "lon": 74.8799577}
  ]
  ```

### 2. Path by Address Names

Geocodes both addresses using Nominatim, maps them to the nearest road nodes, and runs A*.

- **Method**: `GET`
- **Path**: `/route/path`
- **Parameters**:
  - `from` (String): Starting location name
  - `to` (String): Destination location name
- **Example**:
  ```bash
  curl "http://localhost:8080/route/path?from=Kadri+Mangalore&to=Hampankatta+Mangalore"
  ```

### 3. Utility Endpoints

- `GET /route/load`: Reloads the OSM file and returns node and way counts.
- `GET /route/graph`: Returns graph node and grid cell counts.

## Configuration

In `src/main/resources/application.properties`:

```properties
spring.application.name=route-planner
server.port=8080

# Path to the OSM XML file
route.osm.file-path=data/mangalore.osm

# User-Agent for Nominatim geocoding requests
route.geocoding.user-agent=RoutePlanner-App/1.0 (routeplanner-support@project.org)
```

## How the Routing Works

1. **Parsing**: `SaxParser` reads `mangalore.osm` and passes events to `OsmHandler`. It stores nodes (ID, lat, lon) and only ways marked with `highway=*`.
2. **Graph Construction**: `GraphBuilder` creates bidirectional edges between consecutive nodes in highway ways. Edge weights are distances calculated using the Haversine formula.
3. **Nearest Node Lookup**: `Graph` groups nodes into a grid index. When a coordinate is clicked or geocoded, `AStarRouter.nearestNode` checks the matching grid cell and adjacent cells to find the closest road node.
4. **Pathfinding**: `AStarRouter.AStar` uses a priority queue ordered by $f(n) = g(n) + h(n)$, where $g(n)$ is actual distance from the start node and $h(n)$ is Haversine distance to the goal.

## Running Tests

```bash
./mvnw test
```
