package com.wsky.lomantin.Client;

import com.wsky.lomantin.config.PexelsProperties;
import com.wsky.lomantin.dto.pexels.PexelsVideoSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class PexelsClient {

    private final PexelsProperties properties;
    private final RestClient restClient = RestClient.builder().build();

    public PexelsVideoSearchResponse searchVideos(String query, int page, int perPage, String orientation) {
        return restClient.get()
                .uri(properties.getBaseUrl() + "/videos/search", uriBuilder -> uriBuilder
                        .queryParam("query", query)
                        .queryParam("page", page)
                        .queryParam("per_page", perPage)
                        .queryParamIfPresent("orientation", Optional.ofNullable(orientation))
                        .build())
                .header("Authorization", properties.getApiKey())
                .retrieve()
                .body(PexelsVideoSearchResponse.class);
    }
}
