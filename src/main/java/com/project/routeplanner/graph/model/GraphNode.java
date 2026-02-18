package com.project.routeplanner.graph.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GraphNode {
    private long id;
    private double lat;
    private double lon;
    List<GraphEdge> edges;

    public GraphNode(long node, double lat, double lon) {
        this.id = node;
        this.lat = lat;
        this.lon = lon;
        this.edges = new ArrayList<>();
    }
}
