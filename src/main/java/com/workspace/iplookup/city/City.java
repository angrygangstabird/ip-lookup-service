package com.workspace.iplookup.city;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
    name = "cities",
    uniqueConstraints = @UniqueConstraint(columnNames = {"city_name", "country_code"})
)
@Data
@NoArgsConstructor
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_name", nullable = false)
    private String cityName;

    @Column(name = "country_code", length = 2, nullable = false)
    private String countryCode;

    @Column(name = "first_seen", nullable = false, updatable = false)
    private Instant firstSeen;

    @PrePersist
    public void prePersist() {
        if (firstSeen == null) {
            firstSeen = Instant.now();
        }
    }
}
