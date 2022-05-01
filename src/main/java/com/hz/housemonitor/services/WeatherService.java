package com.hz.housemonitor.services;

import com.hz.housemonitor.configuration.OpenWeatherRestClientConfig;
import com.hz.housemonitor.configuration.PropertiesConfig;
import com.hz.housemonitor.models.weather.CurrentWeather;
import com.hz.housemonitor.models.weather.ForecastWeather;
import com.hz.housemonitor.models.weather.Weather;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownContentTypeException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class WeatherService {
    private final RestTemplate openWeatherRestTemplate;
    private final PropertiesConfig config;

    @Cacheable("fetchForecast")
    public ForecastWeather fetchForecast() {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(OpenWeatherRestClientConfig.ONECALL)
                .queryParam("lat", config.getLocationLat())
                .queryParam("lon", config.getLocationLon())
                .queryParam("exclude", "minutely, hourly")
                .queryParam("units", "metric")
                .queryParam("appid", config.getOpenWeatherToken());
        UriComponentsBuilder redacted = UriComponentsBuilder
                .fromPath(OpenWeatherRestClientConfig.ONECALL)
                .queryParam("lat", config.getLocationLat())
                .queryParam("lon", config.getLocationLon())
                .queryParam("exclude", "minutely, hourly")
                .queryParam("units", "metric")
                .queryParam("appid", "hidden");

        log.info("fetching weather from endpoint: {}{}", config.getOpenWeatherAPI().getUrl(), redacted.toUriString());
        try {
            ResponseEntity<ForecastWeather> response =
                    openWeatherRestTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {}
                    );
            if (response.getStatusCodeValue() == 200) {
                return response.getBody();
            }
        } catch (UnknownContentTypeException e) {
            log.error("Error fetching OpenWeather {}", e.getMessage());
        }
        return new ForecastWeather();
    }

    @Cacheable("fetchCurrent")
    public CurrentWeather fetchCurrent() {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(OpenWeatherRestClientConfig.CURRENT)
                .queryParam("id", config.getLocationId())
                .queryParam("units", "metric")
                .queryParam("appid", config.getOpenWeatherToken());
        UriComponentsBuilder redacted = UriComponentsBuilder
                .fromPath(OpenWeatherRestClientConfig.CURRENT)
                .queryParam("id", config.getLocationId())
                .queryParam("units", "metric")
                .queryParam("appid", "hidden");

        log.info("fetching weather from endpoint: {}{}", config.getOpenWeatherAPI().getUrl(), redacted.toUriString());
        try {
            ResponseEntity<CurrentWeather> response =
                    openWeatherRestTemplate.exchange(
                            builder.toUriString(),
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {}
                    );
            if (response.getStatusCodeValue() == 200) {
                return response.getBody();
            }
        } catch (UnknownContentTypeException e) {
            log.error("Error fetching OpenWeather {}", e.getMessage());
        }
        return new CurrentWeather();
    }

    @CacheEvict(cacheNames = {"fetchForecast","fetchCurrent","attributes"})
    public void cacheClean() {
        log.debug("Cache cleared");
    }

    public String getWeatherIcon(List<Weather> weather) {
        Weather firstWeather = weather.stream().findFirst().orElse(new Weather("01d", 200));
        int code = firstWeather.getId();
        String icon = "wi wi-owm-";

        // If we are not in the ranges mentioned above, add a day/night prefix.
     //   if (!(code > 699 && code < 800) && !(code > 899 && code < 1000)) {
       //     icon = icon + "day-";
        //}

        // Finally, tack on the code.
        return icon + code;
    }

}
