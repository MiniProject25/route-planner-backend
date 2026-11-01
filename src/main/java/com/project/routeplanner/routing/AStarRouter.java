package com.project.routeplanner.routing;

import com.project.routeplanner.graph.model.AStarNode;
import com.project.routeplanner.graph.model.Graph;
import com.project.routeplanner.graph.model.GraphEdge;
import com.project.routeplanner.graph.model.GraphNode;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.project.routeplanner.graph.GraphBuilder.calculateHaversine;

@Data
@Component
public class AStarRouter {

    public List<GraphNode> AStar(Graph graph, Long from, Long to) {
        if (graph == null || graph.getNodes() == null || from == null || to == null) {
            return Collections.emptyList();
        }

        if (!graph.getNodes().containsKey(from) || !graph.getNodes().containsKey(to)) {
            return Collections.emptyList();
        }

        if (from.equals(to)) {
            return List.of(graph.getNodes().get(from));
        }

        PriorityQueue<AStarNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(AStarNode::getF));

        Map<Long, Double> gScore = new HashMap<>();
        Map<Long, Long> cameFrom = new HashMap<>();
        Set<Long> closedSet = new HashSet<>();

        gScore.put(from, 0.0);

        double hStart = heuristic(from, to, graph);
        openSet.add(new AStarNode(from, hStart));

        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            long currId = current.getId();

            if (currId == to) {
                return reconstructPath(cameFrom, to, graph);
            }

            if (closedSet.contains(currId)) continue;
            closedSet.add(currId);

            GraphNode currNode = graph.getNodes().get(currId);
            if (currNode == null || currNode.getEdges() == null) continue;

            for (GraphEdge edge : currNode.getEdges()) {
                Long neighborId = edge.getTarget();
                if (neighborId == null || !graph.getNodes().containsKey(neighborId)) continue;

                double tentativeG = gScore.get(currId) + edge.getWeight();

                if (tentativeG < gScore.getOrDefault(neighborId, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(neighborId, currId);
                    gScore.put(neighborId, tentativeG);

                    double fScore = tentativeG + heuristic(neighborId, to, graph);
                    openSet.add(new AStarNode(neighborId, fScore));
                }
            }
        }

        return Collections.emptyList();
    }

    public List<GraphNode> reconstructPath(Map<Long, Long> cameFrom, Long to, Graph graph) {
        List<Long> path = new ArrayList<>();
        Long current = to;

        while (current != null) {
            path.addFirst(current);
            current = cameFrom.get(current);
        }

        List<GraphNode> pathList = new ArrayList<>();

        for (Long aLong : path) {
            GraphNode node = graph.getNodes().get(aLong);
            if (node != null) {
                pathList.add(node);
            }
        }

        return pathList;
    }

    public double heuristic(Long from, Long to, Graph graph) {
        GraphNode fromNode = graph.getNodes().get(from);
        GraphNode toNode = graph.getNodes().get(to);

        if (fromNode == null || toNode == null) {
            return Double.POSITIVE_INFINITY;
        }

        return calculateHaversine(fromNode.getLat(), fromNode.getLon(), toNode.getLat(), toNode.getLon());
    }

    public long nearestNode(double lat, double lon, Graph graph) {
        if (graph == null || graph.getNodes() == null || graph.getNodes().isEmpty()) {
            return -1;
        }

        graph.BuildGridIndex();

        Map<Long, GraphNode> nodes = graph.getNodes();
        int baseGridX = (int) (lat * 50);
        int baseGridY = (int) (lon * 50);

        // Check expanding grid radius (up to 3 rings)
        List<Long> candidates = new ArrayList<>();
        for (int r = 0; r <= 3; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dy = -r; dy <= r; dy++) {
                    if (Math.max(Math.abs(dx), Math.abs(dy)) == r) {
                        String key = (baseGridX + dx) + "," + (baseGridY + dy);
                        List<Long> cellNodes = graph.getGrid().get(key);
                        if (cellNodes != null) {
                            candidates.addAll(cellNodes);
                        }
                    }
                }
            }
            if (!candidates.isEmpty()) {
                break;
            }
        }

        double dist = Double.MAX_VALUE;
        long id = -1;

        if (!candidates.isEmpty()) {
            for (Long nd : candidates) {
                GraphNode node = nodes.get(nd);
                if (node != null) {
                    double dLat = lat - node.getLat();
                    double dLon = lon - node.getLon();
                    double temp = dLat * dLat + dLon * dLon;
                    if (temp < dist) {
                        dist = temp;
                        id = node.getId();
                    }
                }
            }
            if (id != -1) {
                return id;
            }
        }

        // Fallback: search all nodes in the graph if no candidate in neighboring grid cells
        for (GraphNode node : nodes.values()) {
            double dLat = lat - node.getLat();
            double dLon = lon - node.getLon();
            double temp = dLat * dLat + dLon * dLon;
            if (temp < dist) {
                dist = temp;
                id = node.getId();
            }
        }

        return id;
    }
}
