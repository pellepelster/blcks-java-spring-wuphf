package de.solidblocks.examples.javaspringwuphf;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * The environment variables that the process was started with.
 *
 * <p>The environment of a process does not change while it runs, so the report is built once and
 * given out as it is. The map is a constructor argument and not a call to {@link System#getenv()},
 * which lets a test give its own environment.
 */
public class ProcessEnvironment {

    /** The first part of the name of every variable that a blcks link sets. */
    static final String LINK_PREFIX = "BLCKS_";

    /** Sorts by name, ignoring case, with the exact name as the second key to make it stable. */
    private static final Comparator<EnvironmentVariable> BY_NAME =
            Comparator.comparing(EnvironmentVariable::name, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(EnvironmentVariable::name);

    private final EnvironmentReport report;

    /** {@code environment} is {@link System#getenv()} in the application. */
    public ProcessEnvironment(Map<String, String> environment) {
        this.report = report(environment);
    }

    /** The environment in the two groups, masked and sorted. */
    public EnvironmentReport report() {
        return report;
    }

    private static EnvironmentReport report(Map<String, String> environment) {
        List<EnvironmentVariable> link = new ArrayList<>();
        List<EnvironmentVariable> other = new ArrayList<>();

        environment.forEach(
                (name, value) -> {
                    // Masked here, where the variable is read, so that no other part of the
                    // application ever holds a value that it must not print.
                    EnvironmentVariable variable =
                            new EnvironmentVariable(
                                    name,
                                    Secrets.maskIfSecret(name, value),
                                    Secrets.isSecretName(name));
                    (name.startsWith(LINK_PREFIX) ? link : other).add(variable);
                });

        link.sort(BY_NAME);
        other.sort(BY_NAME);
        return new EnvironmentReport(List.copyOf(link), List.copyOf(other));
    }
}
