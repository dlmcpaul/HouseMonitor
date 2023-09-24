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
public class OpenWeatherRestClientConfig {
    public static final String CURRENT = "/data/2.5/weather";
    public static final String ONECALL = "/data/2.5/onecall";
    public static final String WEATHERICON = "/img/wn/";

    private final PropertiesConfig config;

    @Bean
    public RestTemplate openWeatherRestTemplate(RestTemplateBuilder builder) {
        log.info("Reading from Open Weather endpoint {}", config.getOpenWeatherAPI().getUrl());

        HttpClient httpClient = HttpClients
                .custom()
                .useSystemProperties()
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return builder
                .rootUri(config.getOpenWeatherAPI().getUrl())
                .setConnectTimeout(Duration.ofSeconds(5))
                .requestFactory(() -> new BufferingClientHttpRequestFactory(factory))
                .build();

    }
}
