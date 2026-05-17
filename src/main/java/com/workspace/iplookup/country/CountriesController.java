package com.workspace.iplookup.country;

import com.workspace.iplookup.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ip/countries")
public class CountriesController {

    private final CountryService countryService;

    public CountriesController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping
    public ResponseEntity<List<Country>> getAll() {
        return ResponseEntity.ok(countryService.getAll());
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {
        return countryService.getByCode(code)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> addCountry(@RequestBody CountryRequest request) {
        if (request.countryCode() == null || request.countryCode().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Bad Request", "countryCode is required"));
        }
        if (request.countryCode().strip().length() != 2) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Bad Request", "countryCode must be a 2-letter ISO code"));
        }
        if (request.countryName() == null || request.countryName().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "Bad Request", "countryName is required"));
        }

        boolean existed = countryService.getByCode(request.countryCode()).isPresent();
        Country country = countryService.store(request.countryCode(), request.countryName());

        if (existed) {
            return ResponseEntity.ok(country);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(country);
    }

    public record CountryRequest(String countryCode, String countryName) {}
}
