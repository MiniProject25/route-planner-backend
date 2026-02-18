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
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(AStarNode::getF));

        Map<Long, Double> gScore = new HashMap<>();
        Map<Long, Long> cameFrom = new HashMap<>();
        Set<Long> closedSet = new HashSet<>();

        gScore.put(from, 0.0);

        double hStart = heuristic(from, to, graph);
        openSet.add(new AStarNode(from, hStart));

        while(!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            long currId = current.getId();

            if (currId == to) {
                return reconstructPath(cameFrom, to, graph);
            }

            if (closedSet.contains(currId)) continue;
            closedSet.add(currId);

            GraphNode currNode = graph.getNodes().get(currId);

            for (GraphEdge edge : currNode.getEdges()) {
                Long neighborId = edge.getTarget();
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

        while(current != null) {
            path.addFirst(current);
            current = cameFrom.get(current);
        }

        List<GraphNode> pathList = new ArrayList<>();

        for (Long aLong : path) {
            pathList.add(graph.getNodes().get(aLong));
        }

        return pathList;
    }

    public double heuristic(Long from, Long to, Graph graph) {
        GraphNode fromNode = graph.getNodes().get(from);
        GraphNode toNode = graph.getNodes().get(to);

        return calculateHaversine(fromNode.getLat(), fromNode.getLon(), toNode.getLat(), toNode.getLon());
    }

    public long nearestNode(double lat, double lon, Graph graph) {
        graph.BuildGridIndex();

        String key = graph.getGrid(lat, lon);

        List<Long> candidates = graph.getGrid().get(key);
        Map<Long, GraphNode> nodes = graph.getNodes();

        double dist = Double.MAX_VALUE;
        long id = -1;

        for (Long nd : candidates) {
            GraphNode node = nodes.get(nd);
            double temp = Math.sqrt(Math.abs((lat-node.getLat())*(lat-node.getLat())) + Math.abs((lon-node.getLon())*(lon-node.getLon())));
            if (temp < dist) {
                dist = temp;
                id = node.getId();
            }
        }

        return id;
    }
}
