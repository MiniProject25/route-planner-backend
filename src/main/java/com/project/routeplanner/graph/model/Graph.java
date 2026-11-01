package com.project.routeplanner.graph.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class Graph {
    Map<Long, GraphNode> nodes;

    public Graph(Map<Long, GraphNode> nodes) {
        this.nodes = nodes;
    }

    public Map<String, List<Long>> grid = new HashMap<>();
    private final Long GRID_SIZE = 50L;

    public String getGrid(double lat, double lon) {
        int gridX = (int) (lat * GRID_SIZE);
        int gridY = (int) (lon * GRID_SIZE);

        return gridX + "," + gridY;
    }

    public void BuildGridIndex() {
        if (!grid.isEmpty()) {
            return;
        }
        for (GraphNode node : nodes.values()) {
            int gridX = (int) (node.getLat() * GRID_SIZE);
            int gridY = (int) (node.getLon() * GRID_SIZE);

            String key = gridX + "," + gridY;
            grid.computeIfAbsent(key, k -> new ArrayList<>()).add(node.getId());
        }
    }
}
