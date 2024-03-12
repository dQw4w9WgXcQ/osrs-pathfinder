package dev.dqw4w9wgxcq.pathfinder.pathfinder;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.pathfinder.redis.CacheResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

// todo test env
@Disabled
class RemoteTilePathfinderTest {
    @Test
    void testFindPath() throws ExecutionException, InterruptedException, MalformedURLException {
        var exe = Executors.newCachedThreadPool();
        var remoteTilePathfinder = new RemoteTilePathfinder("http://localhost:8081/", "127.0.0.1", 6379);

        var pathFuture1 = exe.submit(
                () -> remoteTilePathfinder.internalFindPath(0, new Point(3200, 3200), new Point(3232, 3232), Algo.BFS));
        var pathFuture2 = exe.submit(
                () -> remoteTilePathfinder.internalFindPath(0, new Point(3200, 3200), new Point(3232, 3232), Algo.BFS));
        var status1 = pathFuture1.get().status();
        assertTrue(status1 == CacheResult.Status.MISS || status1 == CacheResult.Status.MISS_COMPUTED_ELSEWHERE);
        var status2 = pathFuture2.get().status();
        assertTrue(status2 == CacheResult.Status.MISS || status2 == CacheResult.Status.MISS_COMPUTED_ELSEWHERE);
        assertNotSame(status1, status2);

        var path3 = remoteTilePathfinder.internalFindPath(0, new Point(3200, 3200), new Point(3232, 3232), Algo.BFS);
        assertEquals(CacheResult.Status.HIT, path3.status());
    }

    @Test
    void testFindDistances() throws ExecutionException, InterruptedException, MalformedURLException {
        var exe = Executors.newCachedThreadPool();
        var remoteTilePathfinder = new RemoteTilePathfinder("http://localhost:8081/", "127.0.0.1", 6379);

        var distancesFuture1 = exe.submit(() ->
                remoteTilePathfinder.internalFindDistances(new Position(3200, 3200, 0), Set.of(new Point(3232, 3232))));
        var distancesFuture2 = exe.submit(() ->
                remoteTilePathfinder.internalFindDistances(new Position(3200, 3200, 0), Set.of(new Point(3232, 3232))));
        var status1 = distancesFuture1.get().status();
        assertTrue(status1 == CacheResult.Status.MISS || status1 == CacheResult.Status.MISS_COMPUTED_ELSEWHERE);
        var status2 = distancesFuture2.get().status();
        assertTrue(status2 == CacheResult.Status.MISS || status2 == CacheResult.Status.MISS_COMPUTED_ELSEWHERE);
        assertNotSame(status1, status2);

        var distances3 =
                remoteTilePathfinder.internalFindDistances(new Position(3200, 3200, 0), Set.of(new Point(3232, 3232)));
        assertEquals(CacheResult.Status.HIT, distances3.status());
    }
}
