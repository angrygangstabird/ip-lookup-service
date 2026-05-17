package com.workspace.iplookup.service;

import com.workspace.iplookup.journal.LookupJournalService;
import com.workspace.iplookup.model.IpLookupResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class IpLookupService {

    private static final Logger log = LoggerFactory.getLogger(IpLookupService.class);

    private final RestTemplate restTemplate;
    private final LookupJournalService journalService;
    private final String apiKey;
    private final String baseUrl;

    public IpLookupService(
            RestTemplate restTemplate,
            LookupJournalService journalService,
            @Value("${ipstack.api.key}") String apiKey,
            @Value("${ipstack.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.journalService = journalService;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    @Cacheable(value = "ipLookup", key = "#ip")
    public IpLookupResponse lookup(String ip) {
        log.info("Fetching IP data from ipstack for: {}", ip);

        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .pathSegment(ip)
                .queryParam("access_key", apiKey)
                .toUriString();

        try {
            IpLookupResponse response = restTemplate.getForObject(url, IpLookupResponse.class);
            if (response == null) {
                throw new RuntimeException("Empty response from ipstack for IP: " + ip);
            }
            if (Boolean.FALSE.equals(response.getSuccess()) && response.getError() != null) {
                IpLookupResponse.IpstackError err = response.getError();
                log.error("ipstack API error for IP {}: [{}] {} - {}", ip, err.getCode(), err.getType(), err.getInfo());
                throw new IpstackApiException(err.getCode(), err.getType(), err.getInfo());
            }
            log.info("Successfully fetched IP data for: {}", ip);
            journalService.record(response);
            return response;
        } catch (IpstackApiException e) {
            throw e;
        } catch (HttpClientErrorException e) {
            log.error("HTTP error from ipstack for IP {}: {} {}", ip, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("ipstack HTTP error: " + e.getMessage(), e);
        }
    }

    public static class IpstackApiException extends RuntimeException {
        private final int code;
        private final String type;

        public IpstackApiException(int code, String type, String info) {
            super(info);
            this.code = code;
            this.type = type;
        }

        public int getCode() { return code; }
        public String getType() { return type; }
    }
}
