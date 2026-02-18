package com.project.routeplanner.dtos;

import com.project.routeplanner.graph.model.GraphNode;

public record GraphNodeResponse(Double lat, Double lon) {
    public GraphNodeResponse(GraphNode graphNode) {
        this(
                graphNode.getLat(),
                graphNode.getLon()
        );
    }
}
