package de.solidblocks.examples.javaspringwuphf;

import java.util.List;

/**
 * The environment of the process in the two groups that the page shows. Each group is sorted by
 * name, and every value in it is already masked.
 *
 * @param link the variables that a blcks link set, which all start with {@code BLCKS_}
 * @param other the remaining variables of the process
 */
public record EnvironmentReport(List<EnvironmentVariable> link, List<EnvironmentVariable> other) {}
