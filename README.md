# osrs-pathfinder

A pathfinding library for Old School Runescape used to help bots navigate around the game world.  

Used in a Spring REST service at [github.com/dQw4w9WgXcQ/osrs-pathfinder-service](https://github.com/dQw4w9WgXcQ/osrs-pathfinder-service).  

Demonstrated at [osrspathfinder.github.io](https://osrspathfinder.github.io/). (Website repo: [github.com/dQw4w9WgXcQ/osrs-pathfinder-site](https://github.com/dQw4w9WgXcQ/osrs-pathfinder-site))
[![website](https://i.imgur.com/sk5XPSt.png)](https://osrspathfinder.github.io/)

## Project Layout
- [graph-generation](graph-generation/src/main/java/github/dqw4w9wgxcq/pathfinder/graphgeneration) - Generates a pathfinding graph from data extracted from the game's cache files.  
- [pathfinding](runelite-api/src/main/java/net/runelite/api) - Consumes the generated graph and finds paths with a hierarchical dijkstra/A* algorithm.


## Hierarchical Dijkstra/A* Algorithm
A path is first found through "contiguous components" of connected by links using a Dijkstra-like algorithm.

Contiguous components are zones that can be reached without going through links.  In the image below, each color represents a zone.  (note that colors are reused.)
![](https://i.imgur.com/MaD51oN.png)

In the two images above, the link path is Start -> Walk -> Dungeon#2 -> (some links at very high coordinates the game uses for the underground area) -> Walk -> Ship#0 -> Walk -> Door#2876 -> Walk -> Ship(tooltip hidden) -> Walk -> Finish.

Once a link path is found, A* can be used at the tile level.  A heuristic for A* is not possible without first finding a path through links.  Chebychev distance is used as the heuristic as it best estimates time to walk in game.  

Links are represented by cyan lines.  The tile path is represented by blue lines.  

### Details

The pathfinding actually executes in reverse (from destination to start) because of teleports.  Teleports are simply alternate origins.  Reversing allows teleports to be added as destinations instead.  Pathfinding with multiple destinations is much more efficient than multiple origins.  

Exact tile distances between links are calculated at the time of graph generation, however the tile paths need to be recalculated at runtime.  

Distances from the start/end tile to all links in the start/end component need to be calculated for each request.  Thankfully, these distances can be cached as they don't take up much space.  Finding distances to all links is O(N), but N can be in the millions.  Still, this isn't a performance issue for valid paths.    

Tile paths can also be cached, but they take up more space, so a LRU/LFU cache should be used (probably Guava's).  This is TODO.  

## Types of Links

![](https://i.imgur.com/k7bTfWe.png)

### Door
Doors are found by looking for game objects with locationType 0,1,2,3 (a wall), a name of "Door", "Large door", or "Gate", and  the action "Open".  

Some known IDs of non-bidirectional doors or doors that require keys need to be hardcoded as IGNORED_IDS.  These are added as SpecialLinks instead.  
### Stair
Stairs are found by looking for game objects with the "Climb-up" or "Climb-down" action.  The area in the plane above or below is checked.  The link is added if a non-blocked tile is found nearby.  Some stairs skip a plane (i.e. from 0 -> 2).  These stairs were found by looking for invalid stairs that link to known invalid components in planes 1-3.  Once they were found, their IDs were hardcoded in a blocklist.  There still may be some invalid stairs remaining.      

### Wilderness Ditch
Wilderness ditches are found by looking for game objects with ID 23271.  Only ditches with X coordinate % 10 == 0 are added.  A ditch near black knight's fortress needs to be hardcoded.  

### Ship/Dungeon
Ship and dungeon links are currently hardcoded, but could be found by parsing the cs2 script the game client uses to load the world map.  This would be difficult as the data isn't always consistent.  Another possible method is looking for dungeon entrances and connecting them Y+6400 (+100 regions) up.  
### Special
Special links are hardcoded links such as the Al Kharid toll gate or the entrance to Mortynia (blessed portal near Drezel).  
