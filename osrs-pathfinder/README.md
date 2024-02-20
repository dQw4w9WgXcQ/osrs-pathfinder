## Project Layout

- [graph-generation](graph-generation/src/main/java/dev/dqw4w9wgxcq/pathfinder/graphgeneration) - Generates the graph from data extracted from the game's cache files and serializes it for later use.
- [pathfinder](pathfinder/src/main/java/dev/dqw4w9wgxcq/pathfinder) - Consumes the generated graph and finds paths with a hierarchical dijkstra/A* algorithm. This package is used by the REST service.
