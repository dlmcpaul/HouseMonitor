package com.hz.housemonitor.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class MakerRestClientConfig {
    public static final String ALL = "/apps/api/2/devices/all";
    public static final String DEVICES = "/apps/api/2/devices";

    public static String DEVICE(String id) {
        return DEVICES + "/" + id;
    }

    private final PropertiesConfig config;

    @Bean
    public RestTemplate makerRestTemplate(RestTemplateBuilder builder) {
        log.info("Reading from Hubitat Maker endpoint {}", config.getHubitatMakerAPI().getUrl());

        HttpClient httpClient = HttpClients
                .custom()
                .useSystemProperties()
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return builder
                .rootUri(config.getHubitatMakerAPI().getUrl())
                .setConnectTimeout(Duration.ofSeconds(5))
                .requestFactory(() -> new BufferingClientHttpRequestFactory(factory))
                .build();

    }
}
