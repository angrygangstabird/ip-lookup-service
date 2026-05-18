package com.workspace.iplookup.city;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByCityNameIgnoreCaseAndCountryCodeIgnoreCase(String cityName, String countryCode);

    List<City> findByCountryCodeIgnoreCase(String countryCode);

    @Query("""
            SELECT c FROM City c
            WHERE LOWER(c.cityName) LIKE LOWER(CONCAT(:t, '%'))
               OR LOWER(c.countryCode) LIKE LOWER(CONCAT(:t, '%'))
            ORDER BY c.cityName ASC
            """)
    List<City> findByToken(@Param("t") String token, Pageable pageable);

    @Query("""
            SELECT c FROM City c
            WHERE (LOWER(c.cityName) LIKE LOWER(CONCAT(:t1, '%')) OR LOWER(c.countryCode) LIKE LOWER(CONCAT(:t1, '%')))
              AND (LOWER(c.cityName) LIKE LOWER(CONCAT(:t2, '%')) OR LOWER(c.countryCode) LIKE LOWER(CONCAT(:t2, '%')))
            ORDER BY c.cityName ASC
            """)
    List<City> findByTwoTokens(@Param("t1") String token1, @Param("t2") String token2, Pageable pageable);
}
