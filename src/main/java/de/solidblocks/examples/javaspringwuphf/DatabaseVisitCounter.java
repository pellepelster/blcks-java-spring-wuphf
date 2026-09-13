package de.solidblocks.examples.javaspringwuphf;

import java.net.URI;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * The count kept in the linked postgres database: one row per visit in the table liquibase
 * created, so the total survives a restart and is shared by every instance connected to it.
 */
@Component
@Profile(ExampleApplication.DATABASE_PROFILE)
public class DatabaseVisitCounter implements VisitCounter {

    /** The name of the table that liquibase makes, which the description shows. */
    static final String TABLE = "visits";

    private final JdbcTemplate jdbc;
    private final StorageBackend storage;

    public DatabaseVisitCounter(JdbcTemplate jdbc, DataSourceProperties dataSource) {
        this.jdbc = jdbc;
        // Made once, at start-up: a request must not pay for a description that cannot change.
        this.storage = describe(dataSource);
    }

    @Override
    @Transactional
    public long recordVisit() {
        jdbc.update("INSERT INTO visits (visited_at) VALUES (?)", Timestamp.from(Instant.now()));
        Long visits = jdbc.queryForObject("SELECT count(*) FROM visits", Long.class);
        return visits == null ? 0 : visits;
    }

    @Override
    public StorageBackend storage() {
        return storage;
    }

    /**
     * The backend that {@code properties} describes. These are the properties that spring made the
     * datasource from, thus this is the configuration itself and not a second reading of the link
     * variables.
     *
     * <p>{@link DataSourceProperties#getPassword()} is not read here, and it must stay that way:
     * this record goes on a page that has no authentication.
     */
    static StorageBackend describe(DataSourceProperties properties) {
        String url = properties.getUrl();
        Map<String, String> details = new LinkedHashMap<>();

        URI address = address(url);
        if (address != null) {
            details.put("host", address.getHost());
            if (address.getPort() != -1) {
                details.put("port", String.valueOf(address.getPort()));
            }
            details.put("database", address.getPath().replaceFirst("^/", ""));
        }

        details.put("user", properties.getUsername() == null ? "" : properties.getUsername());
        details.put("url", Secrets.maskUrl(url));
        details.put("table", TABLE);

        return new StorageBackend(
                "database",
                "PostgreSQL",
                "One row for each visit in the linked postgres database, thus the count stays after"
                        + " a restart and all instances count together.",
                true,
                true,
                Collections.unmodifiableMap(details));
    }

    /**
     * The address in a jdbc url, or null when it does not have one. A url that this cannot read
     * must not stop the application: the description is then the masked url alone.
     */
    private static URI address(String url) {
        if (url == null || !url.startsWith("jdbc:")) {
            return null;
        }

        try {
            URI address = URI.create(url.substring("jdbc:".length()));
            return address.getHost() == null || address.getPath() == null ? null : address;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
