package com.workspace.iplookup.controller;

import com.workspace.iplookup.model.ErrorResponse;
import com.workspace.iplookup.model.IpLookupResponse;
import com.workspace.iplookup.service.IpLookupService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/ip")
public class IpLookupController {

    private static final Logger log = LoggerFactory.getLogger(IpLookupController.class);

    private static final Pattern IPV4_PATTERN =
            Pattern.compile("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");

    private static final Pattern IPV6_PATTERN =
            Pattern.compile("^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|^(([0-9a-fA-F]{1,4}:)*:([0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{1,4})$");

    private final IpLookupService ipLookupService;

    public IpLookupController(IpLookupService ipLookupService) {
        this.ipLookupService = ipLookupService;
    }

    @GetMapping("/lookup/{ip}")
    public ResponseEntity<?> lookup(@PathVariable String ip) {
        if (!isValidIp(ip)) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(400, "Bad Request", "Invalid IP address format: " + ip));
        }

        try {
            IpLookupResponse response = ipLookupService.lookup(ip);
            return ResponseEntity.ok(response);
        } catch (IpLookupService.IpstackApiException e) {
            log.warn("ipstack API error for IP {}: [{}] {}", ip, e.getCode(), e.getMessage());
            int httpStatus = mapIpstackErrorCode(e.getCode());
            return ResponseEntity
                    .status(httpStatus)
                    .body(new ErrorResponse(httpStatus, e.getType(), e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during lookup for IP {}: {}", ip, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(new ErrorResponse(502, "Bad Gateway", "Failed to fetch data from ipstack: " + e.getMessage()));
        }
    }

    @GetMapping("/lookup")
    public ResponseEntity<?> lookupSelf(HttpServletRequest request) {
        String clientIp = extractClientIp(request);
        log.info("Self-lookup requested, resolved client IP: {}", clientIp);

        try {
            IpLookupResponse response = ipLookupService.lookup(clientIp);
            return ResponseEntity.ok(response);
        } catch (IpLookupService.IpstackApiException e) {
            log.warn("ipstack API error for self-lookup IP {}: [{}] {}", clientIp, e.getCode(), e.getMessage());
            int httpStatus = mapIpstackErrorCode(e.getCode());
            return ResponseEntity
                    .status(httpStatus)
                    .body(new ErrorResponse(httpStatus, e.getType(), e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during self-lookup for IP {}: {}", clientIp, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(new ErrorResponse(502, "Bad Gateway", "Failed to fetch data from ipstack: " + e.getMessage()));
        }
    }

    private int mapIpstackErrorCode(int ipstackCode) {
        return switch (ipstackCode) {
            case 101 -> 401;
            case 102, 103 -> 403;
            case 104, 106 -> 429;
            case 301, 302, 303 -> 400;
            default -> 502;
        };
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    private boolean isValidIp(String ip) {
        return IPV4_PATTERN.matcher(ip).matches() || IPV6_PATTERN.matcher(ip).matches();
    }
}
