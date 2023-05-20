package com.exercise.travel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Crawler Service.
 * <p>
 * Properties are configured in the application.yml file.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

}
