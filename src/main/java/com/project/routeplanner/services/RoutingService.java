package com.project.routeplanner.services;

import com.project.routeplanner.dtos.GraphNodeResponse;
import com.project.routeplanner.graph.GraphBuilder;
import com.project.routeplanner.graph.model.Graph;
import com.project.routeplanner.model.Coordinates;
import com.project.routeplanner.parser.OsmHandler;
import com.project.routeplanner.parser.SaxParser;
import com.project.routeplanner.routing.AStarRouter;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Service
public class RoutingService {

    private final OsmHandler osmHandler;
    private final GraphBuilder graphBuilder;
    private final AStarRouter aStarRouter;
    private final GeocodingService geocodingService;

    @Value("${route.osm.file-path:data/mangalore.osm}")
    private String defaultOsmPath;

    private Graph cityNetwork;

    public RoutingService(OsmHandler osmHandler, GraphBuilder graphBuilder, AStarRouter aStarRouter, GeocodingService geocodingService) {
        this.osmHandler = osmHandler;
        this.graphBuilder = graphBuilder;
        this.aStarRouter = aStarRouter;
        this.geocodingService = geocodingService;
    }

    @PostConstruct
    public void init() {
        File defaultFile = new File(defaultOsmPath);
        if (defaultFile.exists()) {
            System.out.println("Initializing route planner graph from: " + defaultOsmPath);
            loadOsmData(defaultOsmPath);
            System.out.println("Graph initialized with " + (cityNetwork != null ? cityNetwork.getNodes().size() : 0) + " nodes.");
        }
    }

    public synchronized String loadOsmData(String filePath) {
        String path = (filePath != null && !filePath.isBlank()) ? filePath : defaultOsmPath;
        try {
            osmHandler.reset();
            SaxParser.parseOSM(path, osmHandler);
            cityNetwork = graphBuilder.BuildGraph(osmHandler.getNodes(), osmHandler.getWays());
            cityNetwork.BuildGridIndex();

            int nodeCount = cityNetwork.getNodes().size();
            int wayCount = osmHandler.getWays().size();

            return "OSM Loaded Successfully - Graph Nodes: " + nodeCount + " Ways: " + wayCount;
        } catch (Exception e) {
            return "Error loading OSM: " + e.getMessage();
        }
    }

    public synchronized Graph getOrLoadGraph(String filePath) {
        String path = (filePath != null && !filePath.isBlank()) ? filePath : defaultOsmPath;
        if (cityNetwork == null) {
            loadOsmData(path);
        }
        return cityNetwork;
    }

    public String createGraph(String filePath) {
        try {
            Graph graph = getOrLoadGraph(filePath);
            if (graph == null) {
                return "Error: Graph could not be initialized.";
            }
            return "Graph contains " + graph.getNodes().size() + " nodes and grid index with " + graph.getGrid().size() + " cells.";
        } catch (Exception e) {
            return "Error loading OSM: " + e.getMessage();
        }
    }

    public List<GraphNodeResponse> getPathUsingAStar(String filePath, String from, String to) {
        try {
            Graph graph = getOrLoadGraph(filePath);
            if (graph == null || graph.getNodes().isEmpty()) {
                return Collections.emptyList();
            }

            Coordinates fromCoords = geocodingService.geocoding(from);
            Coordinates toCoords = geocodingService.geocoding(to);

            if (fromCoords == null || toCoords == null) {
                return Collections.emptyList();
            }

            long fromId = aStarRouter.nearestNode(fromCoords.getLat(), fromCoords.getLon(), graph);
            long toId = aStarRouter.nearestNode(toCoords.getLat(), toCoords.getLon(), graph);

            if (fromId == -1 || toId == -1) {
                return Collections.emptyList();
            }

            return aStarRouter.AStar(graph, fromId, toId).stream()
                    .map(GraphNodeResponse::new)
                    .toList();
        } catch (Exception e) {
            System.err.println("Error calculating path: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<GraphNodeResponse> getPathUsingAStarUsingLatLng(String filePath, double lat1, double lng1, double lat2, double lng2) {
        try {
            Graph graph = getOrLoadGraph(filePath);
            if (graph == null || graph.getNodes().isEmpty()) {
                return Collections.emptyList();
            }

            long fromId = aStarRouter.nearestNode(lat1, lng1, graph);
            long toId = aStarRouter.nearestNode(lat2, lng2, graph);

            if (fromId == -1 || toId == -1) {
                return Collections.emptyList();
            }

            return aStarRouter.AStar(graph, fromId, toId).stream()
                    .map(GraphNodeResponse::new)
                    .toList();
        } catch (Exception e) {
            System.err.println("Error calculating path by latlng: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
