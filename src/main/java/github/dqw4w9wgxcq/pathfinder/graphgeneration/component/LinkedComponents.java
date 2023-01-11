package github.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import com.google.common.base.Preconditions;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.algo.Algo;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Point;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Link;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Links;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.region.Position;

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
                        var componentLinks = new ArrayList<Link>();
                        for (var link : links.all()) {
                            var sourceId = contiguousComponents.planes()[link.source().getZ()][link.source().getX()][link.source().getY()];
                            if (sourceId == id) {
                                componentLinks.add(link);
                            }
                        }
                        component = new LinkedComponent(id, componentLinks);
                        components[id] = component;
                    }

                    planes[z][x][y] = contiguousComponents.planes()[z][x][y] == -1 ? null : components[id];
                }
            }
        }

        return new LinkedComponents(planes, components);
    }

    private static int chebychevDistance(Position p1, Position p2) {
        Preconditions.checkArgument(p1.getZ() == p2.getZ(), "p1({}) and p2({}) must be on the same plane", p1, p2);

        return Algo.chebyshevDistance(toPoint(p1), toPoint(p2));
    }

    private static Point toPoint(Position position) {
        return new Point(position.getX(), position.getY());
    }
}
