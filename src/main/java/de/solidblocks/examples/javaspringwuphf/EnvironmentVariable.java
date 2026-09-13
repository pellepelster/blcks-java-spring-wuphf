package de.solidblocks.examples.javaspringwuphf;

/**
 * One environment variable as it may be shown.
 *
 * @param name the name the process was given
 * @param value what to show; already {@link Secrets#MASK} when {@code secret} is true, so this is always
 *     safe to print
 * @param secret whether the name made this a secret, which tells a reader that {@code value} is a mask
 *     and not the value itself
 */
public record EnvironmentVariable(String name, String value, boolean secret) {}
