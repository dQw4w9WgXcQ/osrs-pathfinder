# OSRS Pathfinder

A pathfinding library to help bots navigate around Old School Runescape.  Handles traversal through doors, stairs, ships, dungeons, and other links.  

Demonstrated at [osrspathfinder.github.io](https://osrspathfinder.github.io/). 

Used in a REST service at: [github.com/dQw4w9WgXcQ/osrs-pathfinder-service](https://github.com/dQw4w9WgXcQ/osrs-pathfinder-service)

Website repo: [github.com/dQw4w9WgXcQ/osrs-pathfinder-site](https://github.com/dQw4w9WgXcQ/osrs-pathfinder-site)

[![website](https://i.imgur.com/sk5XPSt.png)](https://osrspathfinder.github.io/)

## Project Layout
- [graph-generation](graph-generation/src/main/java/github/dqw4w9wgxcq/pathfinder/graphgeneration) - Generates a pathfinding graph from data extracted from the game's cache files.  
- [pathfinding](/pathfinding/src/main/java/github/dqw4w9wgxcq/pathfinder) - Consumes the generated graph and finds paths with a hierarchical dijkstra/A* algorithm.


## The Pathfinding Algorithm
Pathfinding is done in two stages.  First, a path is found through links between "components".  Then, guided by the link path, the tile path can be found efficiently.  

Components are islands of tiles that are internally reachable to eachother.  (similar to the popular coding interview question "number of islands")  Links are doors, stairs, ships, and other shortcuts that connect components to eachother.  Distances between links (through components) are calcualted during graph generation.  An edge is added from each link to all other links in it's component.  

In the image below, each color represents an island aka component.  Cyan lines represent links, while blue lines represent the tile path.  
![](https://i.imgur.com/MaD51oN.png)


### Details

A* was used originally, but was later replaced by BFS.  Although A* is generally faster, BFS was chosen over A* is becasue BFS generates paths with fewer changes of direction.  A* is prone to closely hugging the boundry of a barrier.  Paths generated by BFS look cleaner when displayed in the visualizer website.  Additionally, they can be better minified and use less space for caching/transmission.  

Although distances between links are calculated during graph generation, distances from the start/end tile to all links in the start/end component need to be calculated for each request.  Finding distances to all links in a component is O(N), but N can be in the millions.  Still, this isn't a performance issue for valid paths.  Additionally, these distances take up very little space and are cached.  

Tile paths could also be cached, but they take up more space, so an LRU/LFU cache should be used (probably Guava's).  This is TODO.  

The pathfinding actually executes in reverse (from destination to start) because of teleports.  Teleports are simply alternate origins.  Reversing allows teleports to be added as destinations instead.  Pathfinding with multiple destinations is more efficient than multiple origins.  

## Types of Links

![](https://i.imgur.com/k7bTfWe.png)

### Door
Doors are found by looking for game objects with locationType 0,1,2,3 (a wall), a name of "Door", "Large door", or "Gate", and  the action "Open".  

Some known IDs of non-bidirectional doors or doors that require keys need to be hardcoded as IGNORED_IDS.  These are added as SpecialLinks instead.  
### Stair
Stairs are found by looking for game objects with the "Climb-up" or "Climb-down" action.  The area in the plane above or below is checked.  The link is added if a non-blocked tile is found nearby.  ![image](https://user-images.githubusercontent.com/22358065/218912932-b59a069f-f3ee-49bc-b3c3-200fbddd01f8.png)


Some stairs skip a plane (i.e. from 0 -> 2).  These stairs were found by looking for invalid stairs that link to known invalid components in planes 1-3.  Once they were found, their IDs were hardcoded in a blocklist.  There still may be some invalid stairs remaining.      
### Wilderness Ditch
Wilderness ditches are found by looking for game objects with ID 23271.  Only ditches with X coordinate % 10 == 0 are added.  A ditch near black knight's fortress needs to be hardcoded.  
### Ship/Dungeon
Ship and dungeon links are currently hardcoded, but could be found by parsing the cs2 script the game client uses to load the world map.  This would be difficult as the data isn't always consistent.  Another possible method is looking for dungeon entrances and connecting them Y+6400 (+100 regions) up.  
### Special
Special links are hardcoded links such as the Al Kharid toll gate or the entrance to Mortynia (blessed portal near Drezel).  
