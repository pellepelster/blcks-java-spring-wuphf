package de.solidblocks.examples.javaspringwuphf;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * The one place that decides what this application must not print. Everything that shows a value -
 * the page, the json, a log line - goes through here first.
 *
 * <p>The decision is made on the <em>name</em> of a variable and never on its value: a name is a
 * contract, and a value that looks harmless today can hold a password tomorrow. A name that only
 * looks like a secret is masked as well. Too much masking costs a path in a demo page; too little
 * costs a database password.
 */
final class Secrets {

    /** What takes the place of a secret value, wherever one would have been shown. */
    static final String MASK = "********";

    /**
     * Name parts that make a variable a secret. {@code PWD} is not one of them, because that is the
     * working directory; {@code PASSWD} is.
     */
    private static final List<String> SECRET_PARTS =
            List.of(
                    "PASSWORD",
                    "PASSWD",
                    "SECRET",
                    "TOKEN",
                    "KEY",
                    "CREDENTIAL",
                    "PRIVATE",
                    "AUTH",
                    "SIGNATURE",
                    "SALT",
                    "CERT");

    /** The {@code user:password@} part of a url, which a jdbc url is permitted to hold. */
    private static final Pattern USER_INFO = Pattern.compile("(//[^/@\\s]*:)[^/@\\s]*@");

    /** A query parameter whose name makes it a secret, for example {@code ?password=...}. */
    private static final Pattern SECRET_PARAMETER =
            Pattern.compile(
                    "([?&][^=&\\s]*(?:password|passwd|secret|token|key|credential)[^=&\\s]*=)[^&\\s]*",
                    Pattern.CASE_INSENSITIVE);

    /** True when a variable of this name must not show its value. */
    static boolean isSecretName(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }

        String upper = name.toUpperCase(Locale.ROOT);
        return SECRET_PARTS.stream().anyMatch(upper::contains);
    }

    /** {@code value} as it may be shown for a variable named {@code name}. */
    static String maskIfSecret(String name, String value) {
        if (isSecretName(name)) {
            return MASK;
        }

        return value == null ? "" : value;
    }

    /**
     * {@code url} with the credentials taken out of it. A blcks link keeps the password in a variable of
     * its own, so the jdbc url this application builds holds none - but a copy of this example may
     * put one in the url, and then the url is still safe to show.
     */
    static String maskUrl(String url) {
        if (url == null || url.isBlank()) {
            return "";
        }

        String withoutUserInfo = USER_INFO.matcher(url).replaceAll("$1" + MASK + "@");
        return SECRET_PARAMETER.matcher(withoutUserInfo).replaceAll("$1" + MASK);
    }

    private Secrets() {}
}
