package com.project.routeplanner.graph;

import com.project.routeplanner.graph.model.Graph;
import com.project.routeplanner.graph.model.GraphEdge;
import com.project.routeplanner.graph.model.GraphNode;
import com.project.routeplanner.model.OsmNode;
import com.project.routeplanner.model.OsmWay;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GraphBuilder {
// logic
    // for each highway
    // get its node ref list
        // for each consecutive pair, build an edge in both directions.
    // next, calculate distance via Haversine formula using the coordinates

    public Graph BuildGraph(Map<Long, OsmNode> osmNodes, List<OsmWay> osmWays) {

        Map<Long, GraphNode> graphNodes = new HashMap<>();

        // creating graph nodes for the node references in osmNodes
        for (OsmWay osmWay : osmWays) {
            for (long noderef : osmWay.getNodeRefs()) {

                OsmNode node = osmNodes.get(noderef);

                if (node != null) {
                    graphNodes.putIfAbsent(noderef, new GraphNode(noderef, node.getLat(), node.getLon()));
                }
            }
        }

        // create edges
        for (OsmWay osmWay : osmWays) {
            List<Long> refs = osmWay.getNodeRefs();

            for (int i = 0; i < refs.size()-1; i++) {
                GraphNode from = graphNodes.get(refs.get(i));
                GraphNode to = graphNodes.get(refs.get(i+1));

                if (from != null && to != null) {
                    double distance = calculateHaversine(from.getLat(), from.getLon(), to.getLat(), to.getLon());

                    from.getEdges().add(new GraphEdge(to.getId(), distance));
                    to.getEdges().add(new GraphEdge(from.getId(), distance));
                }
            }
        }

        return new Graph(graphNodes);
    }

    public static double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * 6371 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

}
