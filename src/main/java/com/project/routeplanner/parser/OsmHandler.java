package com.project.routeplanner.parser;

import com.project.routeplanner.model.OsmNode;
import com.project.routeplanner.model.OsmWay;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
public class OsmHandler extends DefaultHandler {
    // must listen to SAX events
    // store all nodes
    // store only highway ways
    // track the current way while parsing

    // store the final output as a map
    private Map<Long, OsmNode> nodes = new HashMap<Long, OsmNode>();
    private List<OsmWay> ways = new ArrayList<>();

    // current way - temporary
    private OsmWay currentWay = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        switch (qName) {
            case "node":
                long nodeId = Long.parseLong(attributes.getValue("id"));
                double lat = Double.parseDouble(attributes.getValue("lat"));
                double lon = Double.parseDouble(attributes.getValue("lon"));

                OsmNode node = new OsmNode(nodeId, lat, lon);

                nodes.put(nodeId, node);
                break;

            case "way":
                long wayId = Long.parseLong(attributes.getValue("id"));
                currentWay = new OsmWay();
                currentWay.setId(wayId);
                currentWay.setNodeRefs(new ArrayList<>());
                currentWay.setHighway(false);

                break;

            case "nd":
                if (currentWay != null) {
                    long ref = Long.parseLong(attributes.getValue("ref"));
                    currentWay.getNodeRefs().add(ref);
                }
                break;

            case "tag":
                if (currentWay != null) {
                    String k = attributes.getValue("k");
                    if ("highway".equals(k)) {
                        currentWay.setHighway(true);
                    }
                }

                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if ("way".equals(qName)) {

            if (currentWay != null && currentWay.isHighway()) {
                ways.add(currentWay);
            }

            currentWay = null;
        }
    }

}
