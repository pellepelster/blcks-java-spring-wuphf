package de.solidblocks.examples.javaspringwuphf;

import java.util.Map;

/**
 * Where the visits are kept.
 *
 * <p>The {@link VisitCounter} that keeps them is what makes this, so no other part of the
 * application has to find out which profile it runs on. The page and the json both read this one
 * record, and thus they cannot disagree.
 *
 * @param kind {@code memory} or {@code database}, for a caller that reads the json
 * @param name the name of the backend for a reader
 * @param description one sentence about what this backend means for the count
 * @param persistent whether the count stays after a restart
 * @param shared whether all instances of the application count together
 * @param details more about the backend, in the order it is shown, and free of secrets
 */
public record StorageBackend(
        String kind,
        String name,
        String description,
        boolean persistent,
        boolean shared,
        Map<String, String> details) {}
