package de.solidblocks.examples.javaspringwuphf;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class WuphfPageTest {

    private static final StorageBackend MEMORY =
            new StorageBackend("memory", "In memory", "kept in this process", false, false, Map.of());

    private static String page(Map<String, String> environment, StorageBackend backend) {
        return WuphfPage.render(
                new VisitReport(3, backend, new ProcessEnvironment(environment).report()));
    }

    @Test
    void the_page_shows_the_count() {
        assertThat(page(Map.of(), MEMORY)).contains("<p class=\"count\">3</p>");
    }

    @Test
    void the_page_names_the_backend_it_was_given() {
        Map<String, String> details = new LinkedHashMap<>();
        details.put("host", "10.0.0.42");
        StorageBackend database =
                new StorageBackend("database", "PostgreSQL", "in postgres", true, true, details);

        // The style block names both badges, so the badge that was rendered is what counts.
        assertThat(page(Map.of(), database))
                .contains("<span class=\"badge badge-database\">PostgreSQL</span>")
                .contains("10.0.0.42")
                .doesNotContain("badge badge-memory");
        assertThat(page(Map.of(), MEMORY))
                .contains("<span class=\"badge badge-memory\">In memory</span>")
                .doesNotContain("badge badge-database");
    }

    @Test
    void a_value_with_markup_in_it_cannot_reach_the_browser_as_markup() {
        // A linked service, or the shell that starts the application, decides what is in here.
        String page = page(Map.of("EVIL", "<script>alert(1)</script>"), MEMORY);

        assertThat(page).contains("&lt;script&gt;").doesNotContain("<script>alert(1)</script>");
    }

    @Test
    void a_secret_value_is_not_on_the_page() {
        String page = page(Map.of("BLCKS_DATABASE1_HELLO_WORLD_PASSWORD", "hunter2"), MEMORY);

        assertThat(page)
                .doesNotContain("hunter2")
                .contains("BLCKS_DATABASE1_HELLO_WORLD_PASSWORD")
                .contains(Secrets.MASK);
    }

    @Test
    void the_link_variables_and_the_rest_are_shown_apart() {
        String page = page(Map.of("BLCKS_DATABASE1_HOST", "10.0.0.42", "PATH", "/usr/bin"), MEMORY);

        assertThat(page).contains("Link variables").contains("more from the process");
        assertThat(page.indexOf("BLCKS_DATABASE1_HOST")).isLessThan(page.indexOf("PATH"));
    }

    @Test
    void the_page_says_so_when_no_link_gave_it_anything() {
        assertThat(page(Map.of("PATH", "/usr/bin"), MEMORY)).contains("lone dog");
    }

    @Test
    void the_page_asks_for_no_file_of_its_own() {
        // The application can run on a machine with no route to the internet.
        String page = page(Map.of(), MEMORY);

        assertThat(page).doesNotContain("http://").doesNotContain("https://").doesNotContain("<script");
    }
}
