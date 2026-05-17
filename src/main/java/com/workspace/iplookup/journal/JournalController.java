package com.workspace.iplookup.journal;

import com.workspace.iplookup.model.ErrorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/api/ip/journal")
public class JournalController {

    private final LookupJournalService journalService;

    public JournalController(LookupJournalService journalService) {
        this.journalService = journalService;
    }

    @GetMapping
    public ResponseEntity<Page<LookupJournalEntry>> getJournal(
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        size = Math.min(size, 100);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lookedUpAt"));

        Page<LookupJournalEntry> result;

        if (ip != null && !ip.isBlank()) {
            result = journalService.getByIp(ip.trim(), pageable);
        } else if (country != null && !country.isBlank()) {
            result = journalService.getByCountry(country.trim(), pageable);
        } else if (from != null && to != null) {
            Instant fromInstant = from.toInstant(ZoneOffset.UTC);
            Instant toInstant = to.toInstant(ZoneOffset.UTC);
            result = journalService.getByDateRange(fromInstant, toInstant, pageable);
        } else {
            result = journalService.getAll(pageable);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEntry(@PathVariable Long id) {
        return journalService.getAll(PageRequest.of(0, 1))
                .stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
