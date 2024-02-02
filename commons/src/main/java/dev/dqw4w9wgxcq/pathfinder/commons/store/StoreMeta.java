package dev.dqw4w9wgxcq.pathfinder.commons.store;

import org.jetbrains.annotations.Nullable;

public record StoreMeta(String version, @Nullable String cacheMeta) {}
