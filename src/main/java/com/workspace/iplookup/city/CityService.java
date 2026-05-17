package com.workspace.iplookup.city;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {

    private static final Logger log = LoggerFactory.getLogger(CityService.class);

    private final CityRepository repository;

    public CityService(CityRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public City store(String cityName, String countryCode) {
        String name = cityName.strip();
        String code = countryCode.toUpperCase().strip();

        Optional<City> existing = repository.findByCityNameIgnoreCaseAndCountryCodeIgnoreCase(name, code);
        if (existing.isPresent()) {
            return existing.get();
        }

        City city = new City();
        city.setCityName(name);
        city.setCountryCode(code);
        City saved = repository.save(city);
        log.info("Stored new city: {} ({})", name, code);
        return saved;
    }

    public List<City> getAll() {
        return repository.findAll();
    }

    public List<City> getByCountry(String countryCode) {
        return repository.findByCountryCodeIgnoreCase(countryCode);
    }

    public Optional<City> getById(Long id) {
        return repository.findById(id);
    }

    public boolean exists(String cityName, String countryCode) {
        return repository.findByCityNameIgnoreCaseAndCountryCodeIgnoreCase(
                cityName.strip(), countryCode.toUpperCase().strip()).isPresent();
    }

    public List<City> autocomplete(String prefix, String countryCode, int limit) {
        PageRequest page = PageRequest.of(0, limit, Sort.by("cityName").ascending());

        boolean hasPrefix = prefix != null && !prefix.isBlank();
        boolean hasCountry = countryCode != null && !countryCode.isBlank();

        if (hasPrefix && hasCountry) {
            return repository.findByCityNameStartingWithIgnoreCaseAndCountryCodeIgnoreCase(
                    prefix, countryCode.toUpperCase(), page);
        }
        if (hasPrefix) {
            return repository.findByCityNameStartingWithIgnoreCase(prefix, page);
        }
        return repository.findByCountryCodeIgnoreCase(countryCode.toUpperCase(), page);
    }
}
