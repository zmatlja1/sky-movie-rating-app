package uk.sky.pm.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class MicrometerConfiguration {

    @Bean
    public Counter addMovieRatingApiCounter(final Environment environment, final MeterRegistry meterRegistry) {
        return Counter.builder("add_movie_rating_api_metric")
            .description("Call count of add movie rating API")
            .tags("environment", Arrays.stream(environment.getActiveProfiles())
                .collect(Collectors.joining(",")))
            .register(meterRegistry);
    }

}
