package com.workspace.iplookup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class IpLookupApplication {

    public static void main(String[] args) {
        SpringApplication.run(IpLookupApplication.class, args);
    }
}
