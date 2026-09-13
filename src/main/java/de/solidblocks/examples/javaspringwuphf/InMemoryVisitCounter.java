package de.solidblocks.examples.javaspringwuphf;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * The count with no database behind it: per process, so restarting starts over and two instances
 * count separately. What the application does when it is not linked to a database.
 */
@Component
@Profile(ExampleApplication.MEMORY_PROFILE)
public class InMemoryVisitCounter implements VisitCounter {

    private static final StorageBackend STORAGE =
            new StorageBackend(
                    "memory",
                    "In memory",
                    "No database is linked, thus the count stays in this process. A restart starts"
                            + " it again at zero, and two instances count separately.",
                    false,
                    false,
                    Map.of());

    private final AtomicLong visits = new AtomicLong();

    @Override
    public long recordVisit() {
        return visits.incrementAndGet();
    }

    @Override
    public StorageBackend storage() {
        return STORAGE;
    }
}
