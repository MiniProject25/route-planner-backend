package com.project.routeplanner.services;

import com.project.routeplanner.model.Coordinates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class GeocodingService {
    private final RestClient restClient = RestClient.create();

    @Value("${route.geocoding.user-agent:RoutePlanner-App/1.0 (routeplanner-support@project.org)}")
    private String userAgent;

    public Coordinates geocoding(String address) {
        if (address == null || address.isBlank()) {
            return null;
        }

        try {
            String encodedAddress = URLEncoder.encode(address.trim(), StandardCharsets.UTF_8);
            List<Coordinates> res = restClient.get()
                    .uri("https://nominatim.openstreetmap.org/search?format=json&q=" + encodedAddress)
                    .header("User-Agent", userAgent)
                    .header("Accept", "application/json")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Coordinates>>() {
                    });

            if (res != null && !res.isEmpty()) {
                return res.getFirst();
            }

            return null;
        } catch (Exception e) {
            System.err.println("Geocoding error for address '" + address + "': " + e.getMessage());
            return null;
        }
    }
}
