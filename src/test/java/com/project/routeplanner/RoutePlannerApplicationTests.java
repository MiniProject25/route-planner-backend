package com.project.routeplanner;

import com.project.routeplanner.controller.RoutingController;
import com.project.routeplanner.dtos.GraphNodeResponse;
import com.project.routeplanner.graph.GraphBuilder;
import com.project.routeplanner.graph.model.Graph;
import com.project.routeplanner.parser.OsmHandler;
import com.project.routeplanner.parser.SaxParser;
import com.project.routeplanner.routing.AStarRouter;
import com.project.routeplanner.services.GeocodingService;
import com.project.routeplanner.services.RoutingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoutePlannerApplicationTests {

    @Autowired
    private OsmHandler osmHandler;

    @Autowired
    private GraphBuilder graphBuilder;

    @Autowired
    private AStarRouter aStarRouter;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private RoutingService routingService;

    @Autowired
    private RoutingController routingController;

    @Test
    void contextLoads() {
        assertNotNull(routingService);
        assertNotNull(routingController);
    }

    @Test
    void testOsmParsingAndAStar() throws Exception {
        File file = new File("data/mangalore.osm");
        assertTrue(file.exists(), "mangalore.osm should exist in data/");

        osmHandler.reset();
        SaxParser.parseOSM(file.getPath(), osmHandler);
        assertFalse(osmHandler.getNodes().isEmpty(), "Nodes should not be empty");
        assertFalse(osmHandler.getWays().isEmpty(), "Ways should not be empty");

        Graph graph = graphBuilder.BuildGraph(osmHandler.getNodes(), osmHandler.getWays());
        assertFalse(graph.getNodes().isEmpty(), "Graph nodes should not be empty");

        long startNode = aStarRouter.nearestNode(12.87, 74.88, graph);
        long endNode = aStarRouter.nearestNode(12.86, 74.85, graph);

        assertTrue(startNode != -1, "Start node should be found");
        assertTrue(endNode != -1, "End node should be found");

        var path = aStarRouter.AStar(graph, startNode, endNode);
        assertFalse(path.isEmpty(), "Path should not be empty");
        System.out.println("Found path with size: " + path.size());

        // Test nearestNode with coordinate far away - should not crash with NPE
        long outsideNode = aStarRouter.nearestNode(12.80, 74.70, graph);
        assertTrue(outsideNode != -1, "Fallback should return a valid node even for far coordinates");
    }

    @Test
    void testGeocoding() {
        var coords = geocodingService.geocoding("Mangalore");
        assertNotNull(coords, "Geocoding for 'Mangalore' should return coordinates");
        assertTrue(coords.getLat() > 0);
        assertTrue(coords.getLon() > 0);

        var coordsWithSpace = geocodingService.geocoding("Kadri Mangalore");
        assertNotNull(coordsWithSpace, "Geocoding for 'Kadri Mangalore' should return coordinates");
    }

    @Test
    void testRoutingControllerPathEndpoints() {
        List<GraphNodeResponse> pathByLatLng = routingController.pathByLatlng(12.87, 74.88, 12.86, 74.85, null);
        assertNotNull(pathByLatLng);
        assertFalse(pathByLatLng.isEmpty(), "Path by latlng should return coordinates");

        List<GraphNodeResponse> pathByName = routingController.path("Kadri Mangalore", "Hampankatta Mangalore", null);
        assertNotNull(pathByName);
        assertFalse(pathByName.isEmpty(), "Path by name should return coordinates");
    }
}
