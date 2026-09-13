package de.solidblocks.examples.javaspringwuphf;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

class VisitCounterControllerTest {

    private static final StorageBackend BACKEND =
            new StorageBackend("memory", "In memory", "kept here", false, false, Map.of());

    private final VisitCounterController controller =
            new VisitCounterController(
                    new StubVisitCounter(), new ProcessEnvironment(Map.of("PATH", "/usr/bin")));

    private static class StubVisitCounter implements VisitCounter {
        private long visits;

        @Override
        public long recordVisit() {
            return ++visits;
        }

        @Override
        public StorageBackend storage() {
            return BACKEND;
        }
    }

    @Test
    void an_explicit_json_request_gets_the_report() {
        ResponseEntity<Object> response = controller.visit(MediaType.APPLICATION_JSON_VALUE);

        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isInstanceOfSatisfying(
                VisitReport.class,
                report -> {
                    // The blcks integration tests read this number to know that the deployed
                    // application runs, thus it stays a number at the top of the document.
                    assertThat(report.visited()).isEqualTo(1);
                    assertThat(report.storage()).isEqualTo(BACKEND);
                    assertThat(report.environment().other()).isNotEmpty();
                });
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(
            strings = {
                "*/*",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
                "text/html",
                "application"
            })
    void everything_else_gets_the_page(String accept) {
        ResponseEntity<Object> response = controller.visit(accept);

        assertThat(response.getHeaders().getContentType())
                .isEqualTo(new MediaType(MediaType.TEXT_HTML, java.nio.charset.StandardCharsets.UTF_8));
        assertThat(response.getBody()).isInstanceOfSatisfying(
                String.class, page -> assertThat(page).startsWith("<!doctype html>"));
    }

    @Test
    void a_json_request_that_also_takes_anything_else_gets_json() {
        assertThat(controller.visit("application/json, */*").getBody()).isInstanceOf(VisitReport.class);
    }

    @Test
    void every_request_counts_one_visit() {
        controller.visit(null);
        controller.visit(null);

        assertThat(((VisitReport) controller.visit(MediaType.APPLICATION_JSON_VALUE).getBody()).visited())
                .isEqualTo(3);
    }
}
