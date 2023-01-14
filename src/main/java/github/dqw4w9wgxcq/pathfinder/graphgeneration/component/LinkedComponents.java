package github.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Link;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Links;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public record LinkedComponents(LinkedComponent[][][] planes, LinkedComponent[] components) {
    public static LinkedComponents create(ContiguousComponents contiguousComponents, Links links) {
        var components = new LinkedComponent[contiguousComponents.count()];
        var planes = new LinkedComponent[contiguousComponents.planes().length][contiguousComponents.planes()[0].length][contiguousComponents.planes()[0][0].length];
        for (var z = 0; z < contiguousComponents.planes().length; z++) {
            for (var x = 0; x < contiguousComponents.planes()[0].length; x++) {
                for (var y = 0; y < contiguousComponents.planes()[0][0].length; y++) {
                    var id = contiguousComponents.planes()[z][x][y];
                    if (id == -1) {
                        continue;
                    }

                    var component = components[id];
                    if (component == null) {
                        var sourceLinks = new ArrayList<Link>();
                        var destinationLinks = new ArrayList<Link>();
                        for (var link : links.all()) {
                            var sourceId = contiguousComponents.planes()[link.origin().getZ()][link.origin().getX()][link.origin().getY()];
                            if (sourceId == id) {
                                sourceLinks.add(link);
                            }

                            var destinationId = contiguousComponents.planes()[link.destination().getZ()][link.destination().getX()][link.destination().getY()];
                            if (destinationId == id) {
                                destinationLinks.add(link);
                            }
                        }
                        component = new LinkedComponent(id, sourceLinks, destinationLinks);
                        components[id] = component;
                    }

                    planes[z][x][y] = contiguousComponents.planes()[z][x][y] == -1 ? null : components[id];
                }
            }
        }

        return new LinkedComponents(planes, components);
    }
}
