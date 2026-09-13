package de.solidblocks.examples.javaspringwuphf;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

class DatabaseVisitCounterTest {

    private static DataSourceProperties properties(String url) {
        DataSourceProperties properties = new DataSourceProperties();
        properties.setUrl(url);
        properties.setUsername("hello-world_admin");
        properties.setPassword("hunter2");
        return properties;
    }

    @Test
    void the_description_reads_the_address_out_of_the_url() {
        StorageBackend backend =
                DatabaseVisitCounter.describe(
                        properties("jdbc:postgresql://10.0.0.42:5432/hello-world"));

        assertThat(backend.kind()).isEqualTo("database");
        assertThat(backend.persistent()).isTrue();
        assertThat(backend.shared()).isTrue();
        assertThat(backend.details())
                .contains(
                        entry("host", "10.0.0.42"),
                        entry("port", "5432"),
                        entry("database", "hello-world"),
                        entry("user", "hello-world_admin"),
                        entry("table", DatabaseVisitCounter.TABLE));
    }

    @Test
    void the_description_holds_no_password() {
        StorageBackend backend =
                DatabaseVisitCounter.describe(
                        properties("jdbc:postgresql://db:5432/orders?password=hunter2"));

        assertThat(backend.details().get("url")).contains(Secrets.MASK).doesNotContain("hunter2");
        assertThat(backend.toString()).doesNotContain("hunter2");
    }

    @Test
    void a_url_that_cannot_be_read_still_gives_a_description() {
        // A description that fails must not take the endpoint with it.
        StorageBackend backend = DatabaseVisitCounter.describe(properties("jdbc:not a url"));

        assertThat(backend.kind()).isEqualTo("database");
        assertThat(backend.details()).containsKey("url").doesNotContainKey("host");
    }
}
