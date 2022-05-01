package com.hz.housemonitor.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
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
    public final static String ALL = "/apps/api/2/devices/all";
    public final static String DEVICES = "/apps/api/2/devices";
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
                .setReadTimeout(Duration.ofSeconds(30))
                .requestFactory(() -> new BufferingClientHttpRequestFactory(factory))
                .build();

    }
}
