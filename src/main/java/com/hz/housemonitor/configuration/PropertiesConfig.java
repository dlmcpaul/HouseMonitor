package com.hz.housemonitor.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("housemonitor")
@Data
@Log4j2
public class PropertiesConfig {

    private HTTPResource hubitatMakerAPI;
    private String makerToken;

    private HTTPResource openWeatherAPI;
    private String openWeatherToken;
    private String locationId;
    private double locationLat;
    private double locationLon;

    private String cronSchedule = "*/5 * * * *";

    @Data
    @NoArgsConstructor
    public static class HTTPResource {
        private String host;
        private int port;
        private String context;

        public String getUrl() {
            if (port == 80) {
                return "http://" + host + (context == null || context.isEmpty() ? "" : "/" + context);
            }
            if (port == 443) {
                return "https://" + host + (context == null || context.isEmpty() ? "" : "/" + context);
            }
            return "http://" + host + ":" + port + (context == null || context.isEmpty() ? "" : "/" + context);
        }
    }
}
