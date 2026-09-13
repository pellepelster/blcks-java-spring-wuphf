package de.solidblocks.examples.javaspringwuphf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ExampleApplication {

    /** Profile that keeps the visits in postgres, and the one that keeps them in memory. */
    static final String DATABASE_PROFILE = "database";
    static final String MEMORY_PROFILE = "memory";

    /**
     * The environment variable a blcks link to a postgresql-standalone service named {@code
     * database1} sets. Its presence is what decides where the visits are kept: linked, they go to
     * the database; unlinked - running the archive by hand, say - they stay in the process.
     */
    static final String HOST_VARIABLE = "BLCKS_DATABASE1_HOST";

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ExampleApplication.class);
        application.setAdditionalProfiles(profile(System.getenv(HOST_VARIABLE)));
        application.run(args);
    }

    static String profile(String host) {
        return host == null || host.isBlank() ? MEMORY_PROFILE : DATABASE_PROFILE;
    }

    /**
     * The environment of the process, read once at start-up.
     *
     * <p>The map is given to the constructor here rather than read in it. A {@code Map<String,
     * String>} parameter of a bean is a parameter that spring fills with all beans of type {@code
     * String} by their name, and the environment would then be empty without an error.
     */
    @Bean
    ProcessEnvironment processEnvironment() {
        return new ProcessEnvironment(System.getenv());
    }
}
