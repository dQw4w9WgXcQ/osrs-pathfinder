# OSRS Pathfinder

A pathfinding library to help bots navigate around Old School Runescape.  Handles doors, stairs, ships, dungeons, teleports, and other links.  Demonstrated at [osrspathfinder.github.io](https://osrspathfinder.github.io/). 

[![website](https://i.imgur.com/sk5XPSt.png)](https://osrspathfinder.github.io/)


### Related repos:
Spring REST service: [github.com/dQw4w9WgXcQ/osrs-pathfinder-service](https://github.com/dQw4w9WgXcQ/osrs-pathfinder-service)

Leaflet visualizer website: [github.com/dQw4w9WgXcQ/osrs-pathfinder-site](https://github.com/dQw4w9WgXcQ/osrs-pathfinder-site)

Rust tile pathfinder: [github.com/dQw4w9WgXcQ/osrs-pathfinder-tile](https://github.com/dQw4w9WgXcQ/osrs-pathfinder-tile)


## Project Layout
- [graph-generation](graph-generation/src/main/java/dev/dqw4w9wgxcq/pathfinder/graphgeneration) - Generates the graph from data extracted from the game's cache files and serializes it for later use.
- [pathfinding](/pathfinding/src/main/java/dev/dqw4w9wgxcq/pathfinder) - Consumes the generated graph and finds paths with a hierarchical dijkstra/A* algorithm.  This package is used by the REST service.  


## The Pathfinding Algorithm
Pathfinding is done in two stages.  First, a link path is found using Dijkstra's.  Then, guided by the link path, the tile path can be found efficiently using A*.  

Links are doors, stairs, ships, and other shortcuts.  "Components" are islands of tiles connected by links.  Distances between links (through components) are calcualted during graph generation to create the weighted Dijkstra's graph.  An edge is added from each link to all other links in it's component.  

The tile path is found with A*.  A* requires a heuristic and can only be used on tiles.  Once the link path is found, it becomes possible to create a heuristic.  Additionally, while A* is fast in the average case, it is very inefficient in the worst case.  By finding the link path, we can gaurentee a valid path exists and will never hit the worst case.  

In the image below, each color represents a component.  Cyan lines represent links, while blue lines represent the tile path.  
![](https://i.imgur.com/MaD51oN.png)


### Details
The two stage design also allows for better caching and duplication.  Finding the tile path is the majority of resource usage, so the A* pathfinder was rewritten in Rust and separated out into it's own service.  Additionally, the tile pathfinder is also compiled to WASM and ran client-side in the visualizer website.  Users of the website only need to request link paths from the API (bots using the API directly still request full paths).  

Although distances between links are calculated during graph generation, distances from the start/end tile to all links in the start/end component need to be calculated for each request.  Finding distances to all links in a component is O(N), but N can be in the millions.  Still, this isn't a performance issue for valid paths.  Additionally, distances take up very little space and are cached in process memory without eviction.  

Tile and link paths are also cached, but they take up more space and are cached in Redis with LFU eviction.  


## Types of Links
![](https://i.imgur.com/k7bTfWe.png)


### Door
Doors are found by looking for game objects with locationType 0,1,2,3 (a wall), a name of "Door", "Large door", or "Gate", and  the action "Open".  

Some known IDs of non-bidirectional doors or doors that require keys need to be hardcoded as IGNORED_IDS.  These are added as SpecialLinks instead.  


### Stair
Stairs are found by looking for game objects with the "Climb-up" or "Climb-down" action.  The area in the plane above or below is checked.  The link is added if a non-blocked tile is found nearby.

Some stairs skip a plane (i.e. from 0 -> 2).  These stairs were found by looking for invalid stairs that link to known invalid components in planes 1-3.  Once they were found, their IDs were hardcoded in a blocklist.  There still may be some invalid stairs remaining.      


### Wilderness Ditch
Wilderness ditches are found by looking for game objects with ID 23271.  Only ditches with X coordinate % 10 == 0 are added.  A ditch near black knight's fortress needs to be hardcoded.  


### Ship/Dungeon
Ship and dungeon links are currently hardcoded, but could be found by parsing the cs2 script the game client uses to load the world map.  This would be difficult as the data isn't always consistent.  Another possible method is looking for dungeon entrances and connecting them Y+6400 (+100 regions) up.  


### Special
Special links are hardcoded links such as the Al Kharid toll gate or the entrance to Mortynia (blessed portal near Drezel).  
