# OSRS Pathfinder

A pathfinding service that helps bots navigate around Old School Runescape.

Uses data from the game cache to generate the pathfinding graph.

Handles doors, stairs, ships, dungeons, teleports, and other links.

Demonstrated at [osrspathfinder.github.io](https://osrspathfinder.github.io/).

[![website](https://i.imgur.com/sk5XPSt.png)](https://osrspathfinder.github.io/)

### Related repos:

Spring REST
service: [github.com/dQw4w9WgXcQ/osrs-pathfinder-service](https://github.com/dQw4w9WgXcQ/osrs-pathfinder-service)

Leaflet visualization
website: [github.com/dQw4w9WgXcQ/osrs-pathfinder-site](https://github.com/dQw4w9WgXcQ/osrs-pathfinder-site)

Rust A* tile
pathfinder: [github.com/dQw4w9WgXcQ/osrs-pathfinder-tile](https://github.com/dQw4w9WgXcQ/osrs-pathfinder-tile)

## Project Layout

- [graph-generation](graph-generation/src/main/java/dev/dqw4w9wgxcq/pathfinder/graphgeneration) - Generates the graph
  from data extracted from the game's cache files and serializes it for later use.
- [pathfinding](/pathfinding/src/main/java/dev/dqw4w9wgxcq/pathfinder) - Consumes the generated graph and finds paths
  with a hierarchical dijkstra/A* algorithm. This package is used by the REST service.

## The Pathfinding Algorithm

Pathfinding is done in two stages. First, a link path is found using Dijkstra's. Then, tile paths are found connecting
links using A*.

In the image below, cyan lines represent the link path, while blue lines represent the tile path. The colored areas
represent "components", which are islands of contiguous tiles connected by links.

Links are doors, stairs, ships, and other shortcuts. During graph generation, Distances between links (through
components) are calculated to create the weighted Dijkstra's graph. An edge is added from each link to all other links
in the same component.

This design was chosen for performance, caching, and replication.

![](https://i.imgur.com/MaD51oN.png)

### Details

Since A* uses a heuristic, it can only be used on tiles after the link path is found. Additionally, while A* is fast in
the average case, it's inefficient in the worst case. By finding the link path, we can guarantee a valid path exists and
will never hit the worst case.

The majority of resource usage is in finding the tile path. The two stage design allows tile paths to be cached. The
tile pathfinder was also rewritten in Rust and separated out into its own service, which can be replicated
independently.

Although distances between links are calculated during graph generation, distances from the start/end tile to all links
in the start/end component need to be calculated for each request. Finding distances to all links in a component is O(
N), but N can be in the millions. Still, this isn't a performance issue for valid paths. Additionally, distances take up
very little space and are cached in process memory without eviction.

## Types of Links

![](https://i.imgur.com/k7bTfWe.png)

### Door

Doors are found by looking for game objects with locationType 0,1,2,3 (a wall), a name of "Door", "Large door", or "
Gate", and the action "Open".

Some known IDs of non-bidirectional doors or doors that require keys need to be hardcoded as IGNORED_IDS. These are
added as SpecialLinks instead.

### Stair

Stairs are found by looking for game objects with the "Climb-up" or "Climb-down" action. The area in the plane above or
below is checked. The link is added if a non-blocked tile is found nearby.

Some stairs skip a plane (i.e. from 0 -> 2). These stairs were found by looking for invalid stairs that link to known
invalid components in planes 1-3. Once they were found, their IDs were hardcoded in a blocklist. There still may be some
invalid stairs remaining.

### Wilderness Ditch

Wilderness ditches are found by looking for game objects with ID 23271. Only ditches with X coordinate % 10 == 0 are
added. A ditch near black knight's fortress needs to be hardcoded.

### Ship/Dungeon

Ship and dungeon links are currently hardcoded, but could be found by parsing the cs2 script the game client uses to
load the world map. This would be difficult as the data isn't always consistent. Another possible method is looking for
dungeon entrances and connecting them Y+6400 (+100 regions) up.

### Special

Special links are hardcoded links such as the Al Kharid toll gate or the entrance to Mortynia (blessed portal near
Drezel).  
