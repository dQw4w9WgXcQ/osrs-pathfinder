package dev.dqw4w9wgxcq.pathfinder.pathfinder;

import com.google.gson.Gson;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.pathfinder.redis.CacheResult;
import dev.dqw4w9wgxcq.pathfinder.pathfinder.redis.CacheState;
import dev.dqw4w9wgxcq.pathfinder.pathfinder.redis.Compute;
import dev.dqw4w9wgxcq.pathfinder.pathfinder.redis.RedisCache;
import okhttp3.*;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.*;

class RemoteTilePathfinder {
    public record PathResult(boolean cached, List<Point> path) {}

    public record DistancesResult(boolean cached, Map<Point, Integer> distances) {}

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");
    private static final Gson GSON = new Gson();

    private final OkHttpClient http = new OkHttpClient.Builder()
            .followRedirects(false)
            .callTimeout(Duration.ofSeconds(10))
            .build();

    private final URL findPathUrl;
    private final URL findDistancesUrl;
    private final RedisCache cache;

    public RemoteTilePathfinder(String remoteAddress, String redisHost, int redisPort) {
        try {
            var remoteUrl = new URL(remoteAddress);
            this.findPathUrl = new URL(remoteUrl, "find-path");
            this.findDistancesUrl = new URL(remoteUrl, "find-distances");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        this.cache = new RedisCache(redisHost, redisPort);
    }

    public PathResult findPath(int plane, Point start, Point end, Algo algo) throws PathfinderException {
        var cacheResult = internalFindPath(plane, start, end, algo);

        if (cacheResult.state().status() == CacheState.Status.SUCCESS) {
            var path = GSON.fromJson(cacheResult.state().jsonOrThrow(), Dto.FindPathResponse.class);
            return new PathResult(cacheResult.status() == CacheResult.Status.HIT, path.path());
        }

        throw new PathfinderException("find path failed");
    }

    public DistancesResult findDistances(Position from, Set<Point> ends) throws PathfinderException {
        var cacheResult = internalFindDistances(from, ends);

        return switch (cacheResult.state().status()) {
            case SUCCESS -> {
                var distancesResponse =
                        GSON.fromJson(cacheResult.state().jsonOrThrow(), Dto.FindDistancesResponse.class);

                var map = new HashMap<Point, Integer>();
                for (var entry : distancesResponse.distances()) {
                    map.put(entry.point(), entry.distance());
                }

                yield new DistancesResult(cacheResult.status() == CacheResult.Status.HIT, map);
            }
            case FAILURE, PERMANENT_FAILURE -> throw new PathfinderException("find distances failed");
        };
    }

    @VisibleForTesting
    CacheResult internalFindPath(int plane, Point start, Point end, Algo algo) {
        var key = String.format("path:%s-%d-%d,%d-%d,%d", algo, plane, start.x(), start.y(), end.x(), end.y());
        return cache.computeIfAbsent(key, () -> {
            var dto = new Dto.FindPathRequest(plane, start, end, algo);

            var call = http.newCall(new Request.Builder()
                    .url(findPathUrl)
                    .post(RequestBody.create(GSON.toJson(dto), MEDIA_TYPE_JSON))
                    .build());

            var json = executeCall(call);

            Dto.FindPathResponse resDto;
            try {
                resDto = GSON.fromJson(json, Dto.FindPathResponse.class);
            } catch (Exception e) {
                throw new Compute.FatalComputeException("deserialization failed find-path", e);
            }

            return GSON.toJson(resDto);
        });
    }

    @VisibleForTesting
    CacheResult internalFindDistances(Position from, Set<Point> tos) {
        var key = String.format("distances:%d,%d,%d", from.plane(), from.x(), from.y());
        return cache.computeIfAbsent(key, () -> {
            var dto = new Dto.FindDistancesRequest(from.plane(), from.toPoint(), tos);

            var call = http.newCall(new Request.Builder()
                    .url(findDistancesUrl)
                    .post(RequestBody.create(GSON.toJson(dto), MEDIA_TYPE_JSON))
                    .build());

            var json = executeCall(call);

            Dto.FindDistancesResponse resDto;
            try {
                resDto = GSON.fromJson(json, Dto.FindDistancesResponse.class);
            } catch (Exception e) {
                throw new Compute.FatalComputeException("deserialization failed find-distances", e);
            }

            return GSON.toJson(resDto);
        });
    }

    private static String executeCall(Call call) throws Compute.FatalComputeException, Compute.ComputeException {
        try (var response = call.execute()) {
            var code = response.code();
            if (code >= 500) {
                throw new Compute.ComputeException("server error: " + code, 10);
            } else if (code >= 400) {
                throw new Compute.FatalComputeException("client error: " + code);
            } else if (code >= 300) {
                throw new Compute.FatalComputeException("unexpected redirect: " + code);
            }

            var contentType = response.header("Content-Type");
            if (contentType == null || !contentType.contains("application/json")) {
                throw new Compute.FatalComputeException("unexpected content type: " + contentType);
            }

            return Objects.requireNonNull(response.body()).string();
        } catch (IOException e) {
            throw new Compute.ComputeException("io exception", e, 10);
        }
    }
}
