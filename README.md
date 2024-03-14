# OSRS Pathfinder

OSRS Pathfinder is a REST service that serves shortest paths between two locations in Old School Runescape.  Handles doors, stairs, ships, dungeons, teleports, and other links.

The pathfinding graph is generated from data extracted from the game's cache files.


Demonstrated at [osrspathfinder.github.io](https://osrspathfinder.github.io/).

[![website](https://i.imgur.com/sk5XPSt.png)](https://osrspathfinder.github.io/)

## Project Layout

- [osrs-pathfinder (library)](osrs-pathfinder/) - Graph generation and pathfinding library.  
- [osrs-pathfinder-service-2](https://github.com/dQw4w9WgXcQ/osrs-pathfinder-service-2) - REST service.  (depends on osrs-pathfinder)
- [osrs-pathfinder-tile](https://github.com/dQw4w9WgXcQ/osrs-pathfinder-tile) - Tile pathfinding was rewritten in Rust and separated out into its own REST service.  The main service calls the tile service.  (it is not exposed publically to the web)
- [osrs-pathfinder-site](https://github.com/dQw4w9WgXcQ/osrs-pathfinder-site) - Website made with Leaflet.js that visualizes paths from the REST service.  

## The Pathfinding Algorithm

The pathfinder needs to handle links (weighted edges) such as doors, stairs, ships, and teleports.  However, the majority of pathfinding is done on individual tiles, for which Dijkstra's is inefficient.  The pathfinder uses a two stage design to handle this.

To find a path, the link path is first found using Dijkstra's. Then, tile paths connecting the links are found using A*.  

In the image below, cyan lines represent links while blue lines represent the tile path. The colored areas represent "components", which are islands of contiguous tiles connected by links.

![](https://i.imgur.com/MaD51oN.png)

This design was chosen for parallelization, replication, and caching.  

During graph generation, distances from every link to every other link in its component are calculated to create the weighted Dijkstra's graph.  However, at runtime, distances from the start/end tile to all links in the start/end component need to be calculated for each request. Distances take up very little space and cached with Redis.

Since A* uses a heuristic, it can't be used on a weighted graph. Additionally, while A* is fast in the average case, it's very inefficient in the worst case. By finding the link path, we can guarantee a valid path exists and will never hit the worst case.  

The majority of resource usage is in finding the tile path. Tile pathfinding was rewritten in Rust and separated out into its own service, which can be replicated independently.  

The two stage design allows tile paths to be cached independently of the link path. Tile paths are compressed with Snappy and cached with Redis. The link path can vary between calls because users may have different item/quest unlocks and therefore can't be cached.  

## Types of Links

![](https://i.imgur.com/k7bTfWe.png)

### Door

Doors are found by looking for game objects with locationType 0,1,2,3 (a wall), a name of "Door", "Large door", or "Gate", and the action "Open".

Some known IDs of non-bidirectional doors or doors that require keys need to be hardcoded as IGNORED_IDS. These are added as SpecialLinks instead.

### Stair

Stairs are found by looking for game objects with the "Climb-up" or "Climb-down" action. The area in the plane above or below is checked. The link is added if a non-blocked tile is found nearby.

Some stairs skip a plane (i.e. from 0 -> 2). These stairs were found by looking for invalid stairs that link to known invalid components in planes 1-3. Once they were found, their IDs were hardcoded in a blocklist. There still may be some invalid stairs remaining.

### Wilderness Ditch

Wilderness ditches are found by looking for game objects with ID 23271. Only ditches with X coordinate % 10 == 0 are added. A ditch near black knight's fortress needs to be hardcoded.

### Ship/Dungeon

Ship and dungeon links are currently hardcoded, but could be found by parsing the cs2 script the game client uses to load the world map. This would be difficult as the data isn't always consistent. Another possible method is looking for dungeon entrances and connecting them Y+6400 (+100 regions) up.

### Special

Special links are hardcoded links such as the Al Kharid toll gate or the entrance to Mortynia (blessed portal near Drezel).  
