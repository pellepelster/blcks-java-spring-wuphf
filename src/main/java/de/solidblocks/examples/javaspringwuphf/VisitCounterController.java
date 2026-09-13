package de.solidblocks.examples.javaspringwuphf;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * Counts every visit and renders it in the format the caller asked for. Where the count is kept is
 * the {@link VisitCounter}'s business: the linked postgres database when there is one, this
 * process otherwise.
 *
 * <p>The page and the json show the same {@link VisitReport}: the count, the backend that holds it,
 * and the environment the process was given.
 */
@RestController
public class VisitCounterController {

    private final VisitCounter visits;
    private final ProcessEnvironment environment;

    public VisitCounterController(VisitCounter visits, ProcessEnvironment environment) {
        this.visits = visits;
        this.environment = environment;
    }

    @GetMapping("/")
    public ResponseEntity<Object> visit(
            @RequestHeader(value = HttpHeaders.ACCEPT, required = false) String accept) {
        VisitReport report =
                new VisitReport(visits.recordVisit(), visits.storage(), environment.report());

        if (wantsJson(accept)) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(report);
        }

        return ResponseEntity.ok()
                .contentType(new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8))
                .body(WuphfPage.render(report));
    }

    /**
     * Only an explicit {@code application/json} counts. A wildcard is what browsers and a plain
     * {@code curl} send when they have no preference, and the page is what those should get, so
     * matching a wildcard here would make html the exception rather than the default.
     */
    private static boolean wantsJson(String accept) {
        if (accept == null || accept.isBlank()) {
            return false;
        }

        List<MediaType> accepted;
        try {
            accepted = MediaType.parseMediaTypes(accept);
        } catch (InvalidMediaTypeException e) {
            // A header that cannot be read is not a request for json. This is the exception
            // that parseMediaTypes throws; it does not extend InvalidMimeTypeException.
            return false;
        }

        return accepted.stream()
                .filter(type -> !type.isWildcardType() && !type.isWildcardSubtype())
                .anyMatch(MediaType.APPLICATION_JSON::equalsTypeAndSubtype);
    }
}
