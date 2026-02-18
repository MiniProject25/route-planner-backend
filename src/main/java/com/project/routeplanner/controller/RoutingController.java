package com.project.routeplanner.controller;

import com.project.routeplanner.dtos.GraphNodeInput;
import com.project.routeplanner.dtos.GraphNodeResponse;
import com.project.routeplanner.services.RoutingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/route")
public class RoutingController {
    RoutingService routingService;

    RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @GetMapping("/load")
    public String loadAndParse() {
        return routingService.loadOsmData("/D:/Work/mangalore.osm");
    }

    @GetMapping("/graph")
    public String createAndViewGraph() {
        return routingService.createGraph("/D:/Work/mangalore.osm");
    }

    @GetMapping("/path")
    public List<GraphNodeResponse> path(@RequestParam String from, @RequestParam String to) {
        return routingService.getPathUsingAStar("/D:/Work/mangalore.osm", from, to);
    }

    @GetMapping("/pathByLatlng")
    public List<GraphNodeResponse> pathByLatlng(@RequestParam Double lat1, @RequestParam Double lng1, @RequestParam Double lat2, @RequestParam Double lng2) {
        return routingService.getPathUsingAStarUsingLatLng("/D:/Work/mangalore.osm", lat1, lng1, lat2, lng2);
    }
}
