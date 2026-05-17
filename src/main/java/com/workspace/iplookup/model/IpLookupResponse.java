package com.workspace.iplookup.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IpLookupResponse {

    private Boolean success;

    private IpstackError error;

    private String ip;
    private String type;

    @JsonProperty("continent_code")
    private String continentCode;

    @JsonProperty("continent_name")
    private String continentName;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("country_name")
    private String countryName;

    @JsonProperty("region_code")
    private String regionCode;

    @JsonProperty("region_name")
    private String regionName;

    private String city;

    private String zip;

    private Double latitude;

    private Double longitude;

    private Location location;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IpstackError {
        private Integer code;
        private String type;
        private String info;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {

        @JsonProperty("geoname_id")
        private Long geonameId;

        private String capital;

        @JsonProperty("country_flag")
        private String countryFlag;

        @JsonProperty("country_flag_emoji")
        private String countryFlagEmoji;

        @JsonProperty("country_flag_emoji_unicode")
        private String countryFlagEmojiUnicode;

        @JsonProperty("calling_code")
        private String callingCode;

        @JsonProperty("is_eu")
        private Boolean isEu;
    }
}
