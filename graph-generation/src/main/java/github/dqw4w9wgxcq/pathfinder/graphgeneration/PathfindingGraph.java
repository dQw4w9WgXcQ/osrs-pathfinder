package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import com.google.gson.GsonBuilder;
import github.dqw4w9wgxcq.pathfinder.graph.domain.LinkRef;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.cachedata.CacheData;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.component.ComponentGraph;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.component.ContiguousComponents;
import github.dqw4w9wgxcq.pathfinder.graph.domain.LinkEdge;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.component.LinkedComponents;
import github.dqw4w9wgxcq.pathfinder.graph.domain.link.Link;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Links;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileWorld;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.region.Location;
import net.runelite.cache.region.Position;
import net.runelite.cache.region.Region;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public record PathfindingGraph(
        TileWorld tileWorld,
        ContiguousComponents contiguousComponents,
        LinkedComponents linkedComponents,
        Links links,
        Map<LinkRef, List<LinkEdge>> componentGraph
) {
    public static PathfindingGraph generate(CacheData cacheData) {
        var objectLocations = getLocationsAdjustedFor0x2(cacheData.regionData().regions());
        var tileWorld = TileWorld.create(cacheData, objectLocations);
        var contiguousComponents = ContiguousComponents.create(tileWorld.getPlanes());
        var links = Links.create(cacheData, objectLocations, contiguousComponents);
        var linkedComponents = LinkedComponents.create(contiguousComponents, links);
        var componentGraph = ComponentGraph.createGraph(linkedComponents, contiguousComponents);
        return new PathfindingGraph(tileWorld, contiguousComponents, linkedComponents, links, componentGraph);
    }

    public void write(File dir) throws IOException {
        log.info("Writing graph to {}", dir);

        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        try (
                var fos = new FileOutputStream(new File(dir, "graph.zip"));
                var zos = new ZipOutputStream(fos)
        ) {
            zos.putNextEntry(new ZipEntry("components.dat"));
            var componentsOos = new ObjectOutputStream(zos);
            componentsOos.writeObject(contiguousComponents.planes());

            var gson = new GsonBuilder().create();

            zos.putNextEntry(new ZipEntry("links.json"));
            var linksOos = new ObjectOutputStream(zos);
//            gson.toJson();
        }
    }

    public void load(File file) {

    }

    //todo move this to a better place

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
//    private static Map<Position, DoorLink> purgeIntraComponentDoors(GridWorld world, Map<Position, DoorLink> doorLinks, ContiguousComponents components) {
//        log.info("removing intra-component doors, {} links", doorLinks.size());
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
//                log.debug("itracomponent door {},{} dest:{},{} component:{} dest:{}, in plane:{}", x, y, destX, destY, component, destComponent, position.getZ());
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