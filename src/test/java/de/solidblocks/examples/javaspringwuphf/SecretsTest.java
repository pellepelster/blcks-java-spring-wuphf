package de.solidblocks.examples.javaspringwuphf;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SecretsTest {

    @ParameterizedTest
    @ValueSource(
            strings = {
                "BLCKS_DATABASE1_HELLO_WORLD_PASSWORD",
                "password",
                "PgPassWd",
                "MY_SECRET",
                "GITHUB_TOKEN",
                "AWS_ACCESS_KEY_ID",
                "SOME_CREDENTIALS",
                "PRIVATE_THING",
                "SSH_AUTH_SOCK",
                "SIGNATURE",
                "PASSWORD_SALT",
                "CERT_PATH"
            })
    void names_that_must_not_show_a_value(String name) {
        assertThat(Secrets.isSecretName(name)).isTrue();
        assertThat(Secrets.maskIfSecret(name, "hunter2")).isEqualTo(Secrets.MASK);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "PATH",
                "PWD",
                "HOME",
                "LANG",
                "JAVA_HOME",
                "BLCKS_DATABASE1_HOST",
                "BLCKS_DATABASE1_PORT",
                "BLCKS_DATABASE1_HELLO_WORLD_USER"
            })
    void names_that_may_show_a_value(String name) {
        assertThat(Secrets.isSecretName(name)).isFalse();
        assertThat(Secrets.maskIfSecret(name, "/usr/bin")).isEqualTo("/usr/bin");
    }

    @Test
    void a_name_that_is_not_there_is_not_a_secret() {
        assertThat(Secrets.isSecretName(null)).isFalse();
        assertThat(Secrets.isSecretName("")).isFalse();
    }

    @Test
    void a_value_that_is_not_there_becomes_an_empty_one() {
        assertThat(Secrets.maskIfSecret("PATH", null)).isEmpty();
    }

    @Test
    void a_url_keeps_its_address() {
        assertThat(Secrets.maskUrl("jdbc:postgresql://10.0.0.42:5432/hello-world"))
                .isEqualTo("jdbc:postgresql://10.0.0.42:5432/hello-world");
    }

    @Test
    void a_url_loses_the_credentials_in_it() {
        assertThat(Secrets.maskUrl("jdbc:postgresql://user:hunter2@db:5432/orders"))
                .isEqualTo("jdbc:postgresql://user:" + Secrets.MASK + "@db:5432/orders")
                .doesNotContain("hunter2");
    }

    @Test
    void a_url_loses_a_secret_parameter() {
        assertThat(Secrets.maskUrl("jdbc:postgresql://db:5432/orders?user=a&password=hunter2&ssl=true"))
                .isEqualTo(
                        "jdbc:postgresql://db:5432/orders?user=a&password="
                                + Secrets.MASK
                                + "&ssl=true")
                .doesNotContain("hunter2");
    }

    @Test
    void a_url_that_is_not_there_becomes_an_empty_one() {
        assertThat(Secrets.maskUrl(null)).isEmpty();
        assertThat(Secrets.maskUrl("  ")).isEmpty();
    }
}
