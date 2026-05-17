package com.workspace.iplookup.country;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CountryService {

    private static final Logger log = LoggerFactory.getLogger(CountryService.class);

    private final CountryRepository repository;

    public CountryService(CountryRepository repository) {
        this.repository = repository;
    }

    @Async("journalExecutor")
    @Transactional
    public void store(String countryCode, String countryName) {
        if (countryCode == null || countryCode.isBlank()) return;
        try {
            if (!repository.existsById(countryCode)) {
                Country country = new Country();
                country.setCountryCode(countryCode.toUpperCase());
                country.setCountryName(countryName != null ? countryName : countryCode);
                repository.save(country);
                log.debug("Stored new country: {} ({})", countryName, countryCode);
            }
        } catch (Exception e) {
            log.error("Failed to store country {} {}: {}", countryCode, countryName, e.getMessage());
        }
    }

    public List<Country> getAll() {
        return repository.findAll();
    }

    public Country getByCode(String code) {
        return repository.findById(code.toUpperCase()).orElse(null);
    }
}
