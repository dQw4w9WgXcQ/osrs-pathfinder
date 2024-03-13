package dev.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.Links;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public record LinkedComponents(List<LinkedComponent> linkedComponents) {
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
                        var startLinks = new ArrayList<Link>();
                        var endLinks = new ArrayList<Link>();
                        for (var link : links.all()) {
                            var startId = contiguousComponents
                                    .planes()[link.start().plane()][link.start().x()][
                                    link.start().y()];
                            var endId = contiguousComponents
                                    .planes()[link.end().plane()][link.end().x()][
                                    link.end().y()];

                            if (startId == id) {
                                startLinks.add(link);
                            }

                            if (endId == id) {
                                endLinks.add(link);
                            }
                        }
                        component = new LinkedComponent(startLinks, endLinks);
                        components[id] = component;
                    }
                }
            }
        }

        log.info("found {} linked components", components.length);
        return new LinkedComponents(Arrays.asList(components));
    }
}
