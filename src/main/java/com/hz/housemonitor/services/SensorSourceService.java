package com.hz.housemonitor.services;

import com.hz.housemonitor.configuration.MakerRestClientConfig;
import com.hz.housemonitor.configuration.PropertiesConfig;
import com.hz.housemonitor.models.hubitat.Device;
import com.hz.housemonitor.models.hubitat.Devices;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownContentTypeException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
@Log4j2
public class SensorSourceService {
    private final RestTemplate makerRestTemplate;
    private final PropertiesConfig config;

    public Devices fetchData() {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(MakerRestClientConfig.DEVICES)
                .queryParam("access_token", config.getMakerToken());
        UriComponentsBuilder redacted = UriComponentsBuilder
                .fromPath(MakerRestClientConfig.DEVICES)
                .queryParam("access_token", "hidden");

        log.info("fetching all device id's from endpoint: {}{}", config.getHubitatMakerAPI().getUrl(), redacted.toUriString());
        try {
            ResponseEntity<List<Device>> devices =
                    makerRestTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<List<Device>>() {}
                            );
            if (devices.getStatusCode().is2xxSuccessful()) {
                return new Devices(requireNonNull(devices.getBody()).stream()
                        .map(this::fetchDevice)
                        .filter(device -> device.getAttributes() != null && device.getAttributes().isEmpty() == false)
                        .toList());
            }
        } catch (UnknownContentTypeException e) {
            log.error("Error fetching device id's {}", e.getMessage());
        }
        return new Devices(new ArrayList<>());
    }

    private Device fetchDevice(Device device) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(MakerRestClientConfig.DEVICE(device.getId()))
                .queryParam("access_token", config.getMakerToken());

        log.info("Fetching {} using {}", device.getId(), builder.toUriString());
        try {
            ResponseEntity<Device> responseEntity = makerRestTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
        } catch (UnknownContentTypeException e) {
            log.error("Error Fetching Device {} {}", device.getId(), e.getMessage());
        }
        return device;
    }
}
