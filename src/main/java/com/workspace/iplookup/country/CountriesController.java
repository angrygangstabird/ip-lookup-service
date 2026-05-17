package com.workspace.iplookup.country;

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
    public ResponseEntity<Country> getByCode(@PathVariable String code) {
        Country country = countryService.getByCode(code);
        if (country == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(country);
    }
}
