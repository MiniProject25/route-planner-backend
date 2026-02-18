package com.project.routeplanner.services;

import com.project.routeplanner.dtos.GraphNodeInput;
import com.project.routeplanner.dtos.GraphNodeResponse;
import com.project.routeplanner.graph.GraphBuilder;
import com.project.routeplanner.graph.model.Graph;
import com.project.routeplanner.model.Coordinates;
import com.project.routeplanner.parser.OsmHandler;
import com.project.routeplanner.parser.SaxParser;
import com.project.routeplanner.routing.AStarRouter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutingService {

    OsmHandler osmHandler;
    GraphBuilder graphBuilder;
    AStarRouter aStarRouter;
    GeocodingService geocodingService;

    RoutingService (OsmHandler osmHandler, GraphBuilder graphBuilder, AStarRouter aStarRouter, GeocodingService geocodingService) {
        this.osmHandler = osmHandler;
        this.graphBuilder = graphBuilder;
        this.aStarRouter = aStarRouter;
        this.geocodingService = geocodingService;
    }

    public String loadOsmData(String filePath) {
        try {
            SaxParser.parseOSM(filePath, osmHandler);

            int nodeCount = osmHandler.getNodes().size();
            int wayCount = osmHandler.getWays().size();

            return "OSM Loaded Successfully - Nodes: " + nodeCount + " Ways: " + wayCount;
        } catch (Exception e) {
            return "Error loading OSM: " + e.getMessage();
        }
    }

    public String createGraph(String filePath) {
        try {
            SaxParser.parseOSM(filePath, osmHandler);
            Graph cityNetwork = graphBuilder.BuildGraph(osmHandler.getNodes(), osmHandler.getWays());

            return cityNetwork.getNodes().toString();
        } catch(Exception e) {
            return "Error loading OSM: " + e.getMessage();
        }
    }

    public List<GraphNodeResponse> getPathUsingAStar(String filePath, String from, String to) {
        try {
            SaxParser.parseOSM(filePath, osmHandler);
            Graph cityNetwork = graphBuilder.BuildGraph(osmHandler.getNodes(), osmHandler.getWays());

            Coordinates fromCoords = geocodingService.geocoding(from);
            Coordinates toCoords = geocodingService.geocoding(to);

            long fromId = aStarRouter.nearestNode(fromCoords.getLat(), fromCoords.getLon(), cityNetwork);
            long toId = aStarRouter.nearestNode(toCoords.getLat(), toCoords.getLon(), cityNetwork);

            return aStarRouter.AStar(cityNetwork, fromId, toId).stream().map(GraphNodeResponse::new).toList();
        }
        catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
    }

    public List<GraphNodeResponse> getPathUsingAStarUsingLatLng(String filePath, double lat1, double lng1, double lat2, double lng2) {
        try {
            SaxParser.parseOSM(filePath, osmHandler);
            Graph cityNetwork = graphBuilder.BuildGraph(osmHandler.getNodes(), osmHandler.getWays());

            long fromId = aStarRouter.nearestNode(lat1, lng1, cityNetwork);
            long toId = aStarRouter.nearestNode(lat2, lng2, cityNetwork);

            return aStarRouter.AStar(cityNetwork, fromId, toId).stream().map(GraphNodeResponse::new).toList();
        }
        catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
    }
}
