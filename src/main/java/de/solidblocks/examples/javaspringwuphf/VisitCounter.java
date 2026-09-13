package de.solidblocks.examples.javaspringwuphf;

/** Records one visit and reports how many there have been, wherever they are kept. */
public interface VisitCounter {

    long recordVisit();

    /**
     * Where this counter keeps the visits. The application selects one counter at start-up, so this
     * stays the same for the life of the process.
     */
    StorageBackend storage();
}
