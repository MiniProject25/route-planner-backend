# Route Planner - Backend 🚀

High-performance graph routing backend built with **Spring Boot** and **Java 21**, implementing the **A\* (A-Star) search algorithm** over OpenStreetMap (OSM) road networks.

---

## ✨ Features

- **A\* Shortest Path Algorithm**: Fast pathfinding using Haversine distance heuristic for realistic road distances.
- **Pre-warmed In-Memory Graph**: Road nodes and edges are indexed at application startup, enabling sub-millisecond query execution.
- **Spatial Grid Indexing**: $O(1)$ nearest-node spatial lookup with automatic multi-ring neighborhood expansion and global fallback.
- **Streaming SAX XML Parser**: Efficiently parses large OpenStreetMap XML files (`.osm`) without high memory consumption.
- **Automated Geocoding**: Integrates with OpenStreetMap Nominatim for address-to-coordinate resolution.
- **Cross-Platform**: Includes a bundled road network dataset (`data/mangalore.osm`) that works out of the box on Linux, macOS, and Windows.

---

## 🛠️ Tech Stack

- **Framework**: Spring Boot 4
- **Language**: Java 21
- **Build Tool**: Apache Maven (via included `./mvnw` wrapper)
- **Utilities**: Project Lombok, SAX XML Parser, RestClient

---

## 📁 Directory Structure

```text
route-planner-backend/
├── data/
│   └── mangalore.osm                       # Road network XML dataset
├── src/
│   ├── main/
│   │   ├── java/com/project/routeplanner/
│   │   │   ├── config/CorsConfig.java      # Allowed origins (localhost:3000)
│   │   │   ├── controller/RoutingController.java # REST API endpoints
│   │   │   ├── graph/GraphBuilder.java     # Builds adjacency list & edge weights
│   │   │   ├── graph/model/                # Graph, GraphNode, GraphEdge, AStarNode
│   │   │   ├── parser/SaxParser.java       # SAX parser runner
│   │   │   ├── parser/OsmHandler.java      # Highway element extraction
│   │   │   ├── routing/AStarRouter.java    # A* algorithm & spatial grid lookup
│   │   │   ├── services/RoutingService.java # Graph caching & route orchestration
│   │   │   └── services/GeocodingService.java # Nominatim geocoding client
│   │   └── resources/application.properties # Server port & OSM path configuration
│   └── test/java/com/project/routeplanner/ # Unit and integration tests
├── mvnw / mvnw.cmd                         # Maven wrapper executable
└── pom.xml
```

---

## 🚀 Getting Started

### Prerequisites

- **Java JDK 21** or later:
  ```bash
  java -version
  ```

### 1. Run the Backend

Using the included Maven wrapper:

```bash
# On Linux / macOS
./mvnw spring-boot:run

# On Windows
mvnw.cmd spring-boot:run
```

The application will start on port `8080`:

```text
Initializing route planner graph from: data/mangalore.osm
Graph initialized with 18574 nodes.
Tomcat started on port 8080 (http)
```

---

## 📡 REST API Reference

All endpoints are hosted under `/route`:

### 1. Calculate Route by Coordinates
- **Endpoint**: `GET /route/pathByLatlng`
- **Query Parameters**:
  - `lat1` (Double, required): Start latitude
  - `lng1` (Double, required): Start longitude
  - `lat2` (Double, required): End latitude
  - `lng2` (Double, required): End longitude
  - `filePath` (String, optional): Custom OSM dataset path
- **Example**:
  ```bash
  curl "http://localhost:8080/route/pathByLatlng?lat1=12.87&lng1=74.88&lat2=12.86&lng2=74.85"
  ```
- **Response**:
  ```json
  [
    {"lat": 12.8700748, "lon": 74.8800886},
    {"lat": 12.8700809, "lon": 74.8799577},
    {"lat": 12.8701141, "lon": 74.8797455}
  ]
  ```

### 2. Calculate Route by Address
- **Endpoint**: `GET /route/path`
- **Query Parameters**:
  - `from` (String, required): Start location name
  - `to` (String, required): Destination location name
- **Example**:
  ```bash
  curl "http://localhost:8080/route/path?from=Kadri+Mangalore&to=Hampankatta+Mangalore"
  ```

### 3. Graph Utilities
- `GET /route/load` - Reloads the OSM file and prints total nodes and ways.
- `GET /route/graph` - Inspects graph node and grid partition metrics.

---

## ⚙️ Configuration (`application.properties`)

```properties
spring.application.name=route-planner
server.port=8080

# Path to the default OSM road network file
route.osm.file-path=data/mangalore.osm

# User-Agent for Nominatim geocoding requests
route.geocoding.user-agent=RoutePlanner-App/1.0 (routeplanner-support@project.org)
```

---

## 🧪 Running Tests

Run the automated test suite covering SAX parsing, A* path resolution, geocoding, and controller endpoints:

```bash
./mvnw test
```
