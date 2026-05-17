package com.workspace.iplookup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class IpLookupApplication {

    public static void main(String[] args) {
        SpringApplication.run(IpLookupApplication.class, args);
    }
}
