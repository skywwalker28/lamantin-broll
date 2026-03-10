package com.wsky.lomantin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "pexels")
public class PexelsProperties {
    private String apiKey, baseUrl;
}

