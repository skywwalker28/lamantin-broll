package com.wsky.lomantin.service;


import com.wsky.lomantin.Client.PexelsClient;
import com.wsky.lomantin.dto.VideoItemDto;
import com.wsky.lomantin.dto.VideoSearchResponse;
import com.wsky.lomantin.dto.pexels.PexelsVideo;
import com.wsky.lomantin.dto.pexels.PexelsVideoFile;
import com.wsky.lomantin.dto.pexels.PexelsVideoSearchResponse;
import com.wsky.lomantin.dto.pexels.ScoredVideo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoSearchService {
    private final PexelsClient pexelsClient;

    private static final int MAX_PAGES_TO_AGGREGATE = 3;
    private static final int MAX_VARIANTS = 4;

    public VideoSearchResponse search(String query, int page, int perPage, String orientation) {
        List<String> queryVariants = buildQueryVariants(query);

        Map<Long, ScoredVideo> uniqueVideos = new HashMap<>();

        for (String variant : queryVariants) {
            for (int p = page; p < page + MAX_PAGES_TO_AGGREGATE; p++) {
                PexelsVideoSearchResponse response =
                        pexelsClient.searchVideos(variant, p, Math.min(perPage, 80), orientation);

                if (response == null || response.getVideos() == null || response.getVideos().isEmpty()) break;

                for (PexelsVideo video : response.getVideos()) {
                    long id = video.getId();
                    int score = scoreVideo(video, query, variant, orientation);

                    uniqueVideos.merge(
                            id,
                            new ScoredVideo(video, score),
                            (oldValue, newValue) ->
                                    oldValue.score >= newValue.score ? oldValue : newValue
                    );
                }

                if (response.getVideos().size() < Math.min(perPage, 80)) break;
            }
        }

        List<VideoItemDto> items = uniqueVideos.values().stream()
                .sorted(Comparator.comparingInt((ScoredVideo v) -> v.score).reversed()
                        .thenComparingInt(v -> safeDuration(v.video))
                        .thenComparingLong(v -> v.video.getId()))
                .limit(perPage)
                .map(v -> toDto(v.video))
                .collect(Collectors.toList());

        return new VideoSearchResponse(query, page, perPage, items);
    }

    private VideoItemDto toDto(PexelsVideo video) {
        String bestVideoUrl = extractBestVideoUrl(video);
        return new VideoItemDto(
                video.getId(),
                video.getImage(),
                video.getUser() != null ? video.getUser().getName() : null,
                video.getUrl(),
                bestVideoUrl,
                video.getDuration(),
                video.getWidth(),
                video.getHeight()
        );
    }

    private String extractBestVideoUrl(PexelsVideo video) {
        if (video.getVideo_files() == null || video.getVideo_files().isEmpty()) return null;
        return video.getVideo_files().stream()
                .filter(file -> "video/mp4".equalsIgnoreCase(file.getFile_type()))
                .max(Comparator.comparingInt(file ->
                        safeFileWidth(file) * safeFileHeight(file)))
                .map(PexelsVideoFile::getLink)
                .orElse(null);
    }

    private List<String> buildQueryVariants(String query) {
        if (query == null || query.isBlank()) return List.of();

        String q = query.trim().toLowerCase(Locale.ROOT);

        LinkedHashSet<String> variants = new LinkedHashSet<>();
        variants.add(q);

        switch (q) {
            case "football" -> {
                variants.add("soccer");
                variants.add("football match");
                variants.add("stadium");
            }
            case "basketball" -> {
                variants.add("basketball game");
                variants.add("court");
                variants.add("training");
            }
            case "animal" -> {
                variants.add("wildlife");
                variants.add("nature animal");
            }
            case "business" -> {
                variants.add("office");
                variants.add("startup");
                variants.add("meeting");
            }
        }

        variants.add(q + " b roll");
        variants.add(q + " cinematic");
        variants.add(q + " background");

        return variants.stream().limit(MAX_VARIANTS).toList();
    }

    private int scoreVideo(PexelsVideo video, String originalQuery, String matchedVariant, String orientation) {
        int score = 0;

        String url = safe(video.getUrl()).toLowerCase(Locale.ROOT);
        String image = safe(video.getImage()).toLowerCase(Locale.ROOT);

        if (url.contains(originalQuery.toLowerCase(Locale.ROOT))) score += 50;
        if (!matchedVariant.equalsIgnoreCase(originalQuery)) score += 10;
        else score += 30;

        if (matchesOrientation(video, orientation)) score += 20;

        int area = safeWidth(video) * safeHeight(video);
        if (area >= 1920 * 1080) score += 25;
        else if (area >= 1280 * 720) score += 15;

        int duration = safeDuration(video);
        if (duration >= 5 && duration <= 20) score += 15;
        else if (duration <= 40) score += 5;

        if (image.contains("cover")) score += 1;
        return score;
    }

    private boolean matchesOrientation(PexelsVideo video, String orientation) {
        if (orientation == null || orientation.isEmpty()) return true;

        int width = safeWidth(video);
        int height = safeHeight(video);

        return switch (orientation.toLowerCase(Locale.ROOT)) {
            case "landscape" -> width > height;
            case "portrait" -> height > width;
            case "square" -> width == height;
            default -> true;
        };
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private int safeDuration(PexelsVideo video) {
        return video.getDuration() == 0 ? 0 : video.getDuration();
    }

    private int safeWidth(PexelsVideo video) {
        return video.getWidth();
    }

    private int safeHeight(PexelsVideo video) {
        return video.getHeight() == 0 ? 0 : video.getHeight();
    }

    private int safeFileWidth(PexelsVideoFile file) {
        return file.getWidth() == 0 ? 0 : file.getWidth();
    }

    private int safeFileHeight(PexelsVideoFile file) {
        return file.getHeight() == 0 ? 0 : file.getHeight();
    }
}
