package dev.dqw4w9wgxcq.pathfinder.commons.store;

import org.jspecify.annotations.Nullable;

public record StoreMeta(String version, @Nullable String cacheMeta) {}
