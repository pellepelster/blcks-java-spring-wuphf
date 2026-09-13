package de.solidblocks.examples.javaspringwuphf;

/**
 * The answer to a request for {@code /}, which the application gives as a page or as json.
 *
 * <p>{@code visited} stays a number at the top of the json document: it is what the integration tests of
 * blcks read to know that the deployed application runs and counts.
 *
 * @param visited how many visits there have been, this one included
 * @param storage where those visits are kept
 * @param environment the variables the process was given
 */
public record VisitReport(long visited, StorageBackend storage, EnvironmentReport environment) {}
