package com.workspace.iplookup.journal;

import com.workspace.iplookup.model.IpLookupResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class LookupJournalService {

    private static final Logger log = LoggerFactory.getLogger(LookupJournalService.class);

    private final LookupJournalRepository repository;

    public LookupJournalService(LookupJournalRepository repository) {
        this.repository = repository;
    }

    @Async
    public void record(IpLookupResponse response) {
        try {
            LookupJournalEntry entry = new LookupJournalEntry();
            entry.setIp(response.getIp());
            entry.setType(response.getType());
            entry.setCity(response.getCity());
            entry.setZip(response.getZip());
            entry.setRegionCode(response.getRegionCode());
            entry.setRegionName(response.getRegionName());
            entry.setCountryCode(response.getCountryCode());
            entry.setCountryName(response.getCountryName());
            entry.setContinentCode(response.getContinentCode());
            entry.setContinentName(response.getContinentName());
            entry.setLatitude(response.getLatitude());
            entry.setLongitude(response.getLongitude());
            entry.setLookedUpAt(Instant.now());
            repository.save(entry);
            log.debug("Journaled lookup for IP: {}", response.getIp());
        } catch (Exception e) {
            log.error("Failed to journal lookup for IP {}: {}", response.getIp(), e.getMessage());
        }
    }

    public Page<LookupJournalEntry> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<LookupJournalEntry> getByIp(String ip, Pageable pageable) {
        return repository.findByIp(ip, pageable);
    }

    public Page<LookupJournalEntry> getByCountry(String countryCode, Pageable pageable) {
        return repository.findByCountryCode(countryCode.toUpperCase(), pageable);
    }

    public Page<LookupJournalEntry> getByDateRange(Instant from, Instant to, Pageable pageable) {
        return repository.findByLookedUpAtBetween(from, to, pageable);
    }
}
