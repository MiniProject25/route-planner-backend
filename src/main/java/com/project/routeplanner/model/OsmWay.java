package com.project.routeplanner.model;

import lombok.Data;

import java.util.List;

@Data
public class OsmWay {
    private long id;
    private List<Long> nodeRefs;
    private boolean highway;
}
