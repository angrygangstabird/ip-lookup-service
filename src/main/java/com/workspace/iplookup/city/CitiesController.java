package com.workspace.iplookup.city;

import com.workspace.iplookup.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ip/cities")
public class CitiesController {

    private final CityService cityService;

    public CitiesController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public ResponseEntity<List<City>> getAll(@RequestParam(required = false) String country) {
        List<City> result = (country != null && !country.isBlank())
                ? cityService.getByCountry(country.strip())
                : cityService.getAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "10") int limit) {

        if (q == null || q.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Bad Request", "q is required"));
        }

        limit = Math.min(Math.max(limit, 1), 20);

        return ResponseEntity.ok(cityService.autocomplete(q, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return cityService.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> addCity(@RequestBody CityRequest request) {
        if (request.cityName() == null || request.cityName().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Bad Request", "cityName is required"));
        }
        if (request.countryCode() == null || request.countryCode().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Bad Request", "countryCode is required"));
        }
        if (request.countryCode().strip().length() != 2) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Bad Request", "countryCode must be a 2-letter ISO code"));
        }

        boolean existed = cityService.exists(request.cityName(), request.countryCode());
        City city = cityService.store(request.cityName(), request.countryCode());

        return existed
                ? ResponseEntity.ok(city)
                : ResponseEntity.status(HttpStatus.CREATED).body(city);
    }

    public record CityRequest(String cityName, String countryCode) {}
}
