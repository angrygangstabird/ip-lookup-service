package com.workspace.iplookup.country;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CountryService {

    private static final Logger log = LoggerFactory.getLogger(CountryService.class);

    private final CountryRepository repository;

    public CountryService(CountryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Country store(String countryCode, String countryName) {
        String code = countryCode.toUpperCase().strip();
        Optional<Country> existing = repository.findById(code);
        if (existing.isPresent()) {
            return existing.get();
        }
        Country country = new Country();
        country.setCountryCode(code);
        country.setCountryName(countryName.strip());
        Country saved = repository.save(country);
        log.info("Stored new country: {} ({})", countryName, code);
        return saved;
    }

    public List<Country> getAll() {
        return repository.findAll();
    }

    public Optional<Country> getByCode(String code) {
        return repository.findById(code.toUpperCase().strip());
    }
}
