package com.workspace.iplookup.journal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface LookupJournalRepository extends JpaRepository<LookupJournalEntry, Long> {

    Page<LookupJournalEntry> findByIp(String ip, Pageable pageable);

    Page<LookupJournalEntry> findByCountryCode(String countryCode, Pageable pageable);

    Page<LookupJournalEntry> findByLookedUpAtBetween(Instant from, Instant to, Pageable pageable);
}
