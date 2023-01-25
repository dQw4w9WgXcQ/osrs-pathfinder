package github.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import github.dqw4w9wgxcq.pathfinder.domain.link.Link;
import github.dqw4w9wgxcq.pathfinder.pathfinding.domain.Links;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
public record LinkedComponents(LinkedComponent[] linkedComponents) {
    public static LinkedComponents create(ContiguousComponents contiguousComponents, Links links) {
        log.info("Creating linked components");

        var components = new LinkedComponent[contiguousComponents.count()];
        for (var z = 0; z < contiguousComponents.planes().length; z++) {
            for (var x = 0; x < contiguousComponents.planes()[0].length; x++) {
                for (var y = 0; y < contiguousComponents.planes()[0][0].length; y++) {
                    var id = contiguousComponents.planes()[z][x][y];
                    if (id == -1) {
                        continue;
                    }

                    var component = components[id];
                    if (component == null) {
                        var originLinks = new ArrayList<Link>();
                        var destinationLinks = new ArrayList<Link>();
                        for (var link : links.all()) {
                            var originId = contiguousComponents.planes()[link.origin().plane()][link.origin().x()][link.origin().y()];
                            var destinationId = contiguousComponents.planes()[link.destination().plane()][link.destination().x()][link.destination().y()];

                            if (originId == id) {
                                originLinks.add(link);
                            }

                            if (destinationId == id) {
                                destinationLinks.add(link);
                            }
                        }
                        component = new LinkedComponent(originLinks, destinationLinks);
                        components[id] = component;
                    }
                }
            }
        }

        log.info("found {} linked components", components.length);
        return new LinkedComponents(components);
    }
}
