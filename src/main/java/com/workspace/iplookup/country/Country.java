package com.workspace.iplookup.country;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "countries")
@Data
@NoArgsConstructor
public class Country {

    @Id
    @Column(name = "country_code", length = 2, nullable = false)
    private String countryCode;

    @Column(name = "country_name", nullable = false)
    private String countryName;

    @Column(name = "first_seen", nullable = false, updatable = false)
    private Instant firstSeen;

    @PrePersist
    public void prePersist() {
        if (firstSeen == null) {
            firstSeen = Instant.now();
        }
    }
}
