package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.componentgraph.ComponentGraph;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.componentgraph.ContiguousComponents;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Links;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileWorld;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.region.Location;
import net.runelite.cache.region.Position;
import net.runelite.cache.region.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public record PathfinderGraph(ContiguousComponents components, ComponentGraph componentGraph, Links links) {
    public static PathfinderGraph generate(CacheData cacheData) {
        var objectLocations = getLocationsAdjustedFor0x2(cacheData.regionData().regions());

        var gridWorld = TileWorld.create(cacheData, objectLocations);

        var components = ContiguousComponents.create(gridWorld.getPlanes());

        var links = Links.create(cacheData, objectLocations);

        var componentsGraph = ComponentGraph.create(components, links);

        return new PathfinderGraph(components, componentsGraph, links);
    }

    /**
     * The 0x2 render flag signifies that objects from the plane above should affect the collision map of the plane below.  Used for bridges and multi-level buildings.
     */
    private static List<Location> getLocationsAdjustedFor0x2(Collection<Region> regions) {
        log.info("getting 0x2 adjusted locations");
        var locations = new ArrayList<Location>();
        for (var region : regions) {
            for (var location : region.getLocations()) {
                var position = location.getPosition();
                var plane = position.getZ();
                if ((region.getTileSetting(1, position.getX() - region.getBaseX(), position.getY() - region.getBaseY()) & 0x2) == 0x2) {
                    log.debug("location is 0x2: {}", location);
                    plane--;
                }

                var adjLocation = new Location(
                        location.getId(),
                        location.getType(),
                        location.getOrientation(),
                        new Position(position.getX(), position.getY(), plane)
                );

                if (plane < 0) {
                    log.debug("0x2 location is below 0: {}", location);
                    continue;
                }

                locations.add(adjLocation);
            }
        }

        return locations;
    }

//    /**
//     * Some doors can be within the same component, just remove wall flags from world in that case
//     *
//     * @return remaining doors that actually link between two different components
//     */
//    private static Map<Position, DoorLink> purgeInterComponentDoors(GridWorld world, Map<Position, DoorLink> doorLinks, ContiguousComponents components) {
//        log.info("removing inter-component doors, {} links", doorLinks.size());
//
//        var remainingDoors = new HashMap<Position, DoorLink>();
//        for (var entry : doorLinks.entrySet()) {
//            var position = entry.getKey();
//            var doorLink = entry.getValue();
//
//            var z = position.getZ();
//            var x = position.getX();
//            var y = position.getY();
//            var destX = doorLink.destination().getX();
//            var destY = doorLink.destination().getY();
//
//            var map = components.map();
//            var component = map[z][x][y];
//            var destComponent = map[z][destX][destY];
//
//            if (component == -1 || destComponent == -1) {
//                log.debug("door at {} has component/destComponent -1", position);
//                continue;
//            }
//
//            if (component == destComponent) {
//                log.debug("intercomponent door {},{} dest:{},{} component:{} dest:{}, in plane:{}", x, y, destX, destY, component, destComponent, position.getZ());
//                var wall = Wall.fromDXY(destX - x, destY - y);
//                world.getPlane(position.getZ()).unmarkWall(x, y, wall);
//            } else {
//                remainingDoors.put(position, doorLink);
//            }
//        }
//
//        log.info("remaining doors {}/{}", remainingDoors.size(), doorLinks.size());
//        return remainingDoors;
//    }
}