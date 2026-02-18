package com.project.routeplanner.services;

import com.project.routeplanner.model.Coordinates;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class GeocodingService {
    public Coordinates geocoding(String address) {
        RestClient restClient = RestClient.create();

        try {
            List<Coordinates> res = restClient.get()
                    .uri("https://nominatim.openstreetmap.org/search?format=json&q=" + address)
                    .header("User-Agent", "route-planner/1.0")
                    .header("Accept", "application/json")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Coordinates>>() {
                    });

            if (res != null && !res.isEmpty()) {
//                System.out.println(res.getFirst());

                return res.getFirst();
            }

            return null;
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }
}
