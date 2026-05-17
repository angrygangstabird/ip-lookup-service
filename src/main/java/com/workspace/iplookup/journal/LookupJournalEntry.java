package com.workspace.iplookup.journal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "lookup_journal")
@Data
@NoArgsConstructor
public class LookupJournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ip;

    private String type;
    private String city;
    private String zip;
    private String regionCode;
    private String regionName;
    private String countryCode;
    private String countryName;
    private String continentCode;
    private String continentName;
    private Double latitude;
    private Double longitude;

    @Column(nullable = false)
    private Instant lookedUpAt;

    @PrePersist
    public void prePersist() {
        if (lookedUpAt == null) {
            lookedUpAt = Instant.now();
        }
    }
}
