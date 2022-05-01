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
public class OpenWeatherRestClientConfig {
    public final static String CURRENT = "/data/2.5/weather";
    public final static String ONECALL = "/data/2.5/onecall";
    public final static String WEATHERICON = "/img/wn/";
    //?q=London,uk&APPID=";

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
                .setReadTimeout(Duration.ofSeconds(30))
                .requestFactory(() -> new BufferingClientHttpRequestFactory(factory))
                .build();

    }
}
