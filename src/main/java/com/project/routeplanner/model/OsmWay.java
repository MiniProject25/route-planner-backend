package com.project.routeplanner.model;

import lombok.Data;

import java.util.List;

@Data
public class OsmWay {
    private double id;
    private List<Long> nodeRefs;
    private boolean highway;
}
