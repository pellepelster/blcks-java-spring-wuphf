package de.solidblocks.examples.javaspringwuphf;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ProcessEnvironmentTest {

    private static final String PASSWORD = "BLCKS_DATABASE1_HELLO_WORLD_PASSWORD";

    private static EnvironmentReport report(Map<String, String> environment) {
        return new ProcessEnvironment(environment).report();
    }

    @Test
    void the_link_variables_are_one_group_and_the_rest_another() {
        EnvironmentReport report =
                report(
                        Map.of(
                                "BLCKS_DATABASE1_HOST", "10.0.0.42",
                                "BLCKS_DATABASE1_PORT", "5432",
                                "PATH", "/usr/bin",
                                "HOME", "/opt/visits"));

        assertThat(report.link()).extracting(EnvironmentVariable::name)
                .containsExactly("BLCKS_DATABASE1_HOST", "BLCKS_DATABASE1_PORT");
        assertThat(report.other()).extracting(EnvironmentVariable::name)
                .containsExactly("HOME", "PATH");
    }

    @Test
    void each_group_is_sorted_whatever_order_the_environment_had() {
        Map<String, String> environment = new LinkedHashMap<>();
        environment.put("zebra", "1");
        environment.put("Apple", "2");
        environment.put("mango", "3");

        assertThat(report(environment).other()).extracting(EnvironmentVariable::name)
                .containsExactly("Apple", "mango", "zebra");
    }

    @Test
    void a_secret_value_is_replaced_and_the_name_stays() {
        EnvironmentReport report = report(Map.of(PASSWORD, "hunter2"));

        assertThat(report.link()).singleElement()
                .satisfies(
                        variable -> {
                            assertThat(variable.name()).isEqualTo(PASSWORD);
                            assertThat(variable.value()).isEqualTo(Secrets.MASK);
                            assertThat(variable.secret()).isTrue();
                        });
    }

    @Test
    void a_secret_value_is_nowhere_in_the_report() {
        // The masking happens where the environment is read, so that no other part of the
        // application can print a value that it must not print.
        EnvironmentReport report = report(Map.of(PASSWORD, "hunter2", "PATH", "/usr/bin"));

        assertThat(report.toString()).doesNotContain("hunter2");
    }

    @Test
    void an_empty_environment_gives_two_empty_groups() {
        EnvironmentReport report = report(Map.of());

        assertThat(report.link()).isEmpty();
        assertThat(report.other()).isEmpty();
    }
}
