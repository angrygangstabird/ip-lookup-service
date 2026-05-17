package com.workspace.iplookup.city;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByCityNameIgnoreCaseAndCountryCodeIgnoreCase(String cityName, String countryCode);

    List<City> findByCountryCodeIgnoreCase(String countryCode);
}
