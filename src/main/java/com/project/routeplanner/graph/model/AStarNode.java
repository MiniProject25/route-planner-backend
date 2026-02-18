package com.project.routeplanner.graph.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AStarNode {
    Long id;
    double f;
}
