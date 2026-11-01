package com.project.routeplanner.controller;

import com.project.routeplanner.dtos.GraphNodeResponse;
import com.project.routeplanner.services.RoutingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/route")
public class RoutingController {

    private final RoutingService routingService;

    @Value("${route.osm.file-path:data/mangalore.osm}")
    private String defaultOsmPath;

    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @GetMapping("/load")
    public String loadAndParse(@RequestParam(required = false) String filePath) {
        return routingService.loadOsmData(filePath != null && !filePath.isBlank() ? filePath : defaultOsmPath);
    }

    @GetMapping("/graph")
    public String createAndViewGraph(@RequestParam(required = false) String filePath) {
        return routingService.createGraph(filePath != null && !filePath.isBlank() ? filePath : defaultOsmPath);
    }

    @GetMapping("/path")
    public List<GraphNodeResponse> path(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(required = false) String filePath) {
        return routingService.getPathUsingAStar(filePath != null && !filePath.isBlank() ? filePath : defaultOsmPath, from, to);
    }

    @GetMapping("/pathByLatlng")
    public List<GraphNodeResponse> pathByLatlng(
            @RequestParam Double lat1,
            @RequestParam Double lng1,
            @RequestParam Double lat2,
            @RequestParam Double lng2,
            @RequestParam(required = false) String filePath) {
        return routingService.getPathUsingAStarUsingLatLng(filePath != null && !filePath.isBlank() ? filePath : defaultOsmPath, lat1, lng1, lat2, lng2);
    }
}
