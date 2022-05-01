package com.hz.housemonitor;

import com.hz.housemonitor.configuration.PropertiesConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(PropertiesConfig.class)
@EnableScheduling
/*@NativeHint(trigger = ISpringTemplateEngine.class, types = {
        @TypeHint( types = {
                AbstractConfigurableTemplateResolver.class,
                ITemplateResolver.class,
                AbstractTemplateResolver.class,
                SpringResourceTemplateResolver.class,
        }),
        @TypeHint(
                types= {
                        ThymeleafView.class,
                        ThymeleafReactiveView.class
                }, typeNames= {
                "org.thymeleaf.spring5.expression.Mvc$Spring41MvcUriComponentsBuilderDelegate",
                "org.thymeleaf.spring5.expression.Mvc$NonSpring41MvcUriComponentsBuilderDelegate"
        }
        ),
        @TypeHint(types = { Fields.class, Temporals.class, AdditionExpression.class }, access = { TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.DECLARED_METHODS}),
        @TypeHint(types = IterationStatusVar.class, access = { TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_METHODS}),
        @TypeHint(types = { Aggregates.class,
                Arrays.class,
                Bools.class,
                Calendars.class,
                Conversions.class,
                Dates.class,
                ExecutionInfo.class,
                Ids.class,
                Lists.class,
                Maps.class,
                Messages.class,
                Numbers.class,
                Objects.class,
                Sets.class,
                Strings.class,
                Uris.class
        }, access = { TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS})
})
@TypeHint(
        types = {
                com.hz.housemonitor.models.hubitat.Device.class,
                com.hz.housemonitor.models.hubitat.Attribute.class,
                com.hz.housemonitor.models.hubitat.Command.class,
                com.hz.housemonitor.models.weather.ForecastWeather.class,
                com.hz.housemonitor.models.weather.CurrentWeather.class,
                com.hz.housemonitor.models.weather.Current.class,
                com.hz.housemonitor.models.weather.Daily.class,
                com.hz.housemonitor.models.weather.Weather.class,
                com.hz.housemonitor.models.weather.Alert.class,
                com.hz.housemonitor.models.weather.Clouds.class,
                com.hz.housemonitor.models.weather.Coordinates.class,
                com.hz.housemonitor.models.weather.Hourly.class,
                com.hz.housemonitor.models.weather.Main.class,
                com.hz.housemonitor.models.weather.Minutely.class,
                com.hz.housemonitor.models.weather.Rain.class,
                com.hz.housemonitor.models.weather.Sys.class,
                com.hz.housemonitor.models.weather.Temperature.class,
                com.hz.housemonitor.models.weather.TemperatureFull.class,
                com.hz.housemonitor.models.weather.WeatherPoint.class,
                com.hz.housemonitor.models.weather.Wind.class
        })*/
@EnableCaching
public class HouseMonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(HouseMonitorApplication.class, args);
    }
}
