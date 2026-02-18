package com.project.routeplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OsmNode {
    private long id;
    private double lat;
    private double lon;
}
