package de.solidblocks.examples.javaspringwuphf;

import java.util.List;
import java.util.Map;
import org.springframework.web.util.HtmlUtils;

/**
 * Renders a {@link VisitReport} as one page.
 *
 * <p>The page is self-contained: one style block, one drawing, no script and no file that a browser
 * must fetch. The application can run on a machine with no route to the internet, and a page that
 * asks for a font or a stylesheet from somewhere else would be broken there.
 *
 * <p>Every value that comes from outside - the name and the value of an environment variable, the
 * details of the backend - is escaped. A linked service, or a shell that starts the application by
 * hand, can put html in a variable.
 */
final class WuphfPage {

    private static final String HEAD =
            """
            <!doctype html>
            <html lang="en">
              <head>
                <meta charset="utf-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1" />
                <title>WUPHF.com</title>
                <link rel="icon" href="data:," />
                <style>
                  :root {
                    --ink: #12183a;
                    --orange: #ff6a13;
                    --yellow: #ffc300;
                    --pink: #ff2e88;
                    --blue: #1d6fb8;
                    --cream: #fff7ec;
                    --grey: #6b7280;
                    --sans: "Trebuchet MS", "Gill Sans", "Segoe UI", system-ui, Arial, sans-serif;
                    --mono: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;
                  }
                  * { box-sizing: border-box; }
                  body {
                    margin: 0;
                    padding: 32px 20px 64px;
                    background: var(--cream);
                    background-image: repeating-linear-gradient(135deg,
                      rgba(255, 195, 0, 0.16) 0 18px, transparent 18px 36px);
                    color: var(--ink);
                    font-family: var(--sans);
                    line-height: 1.5;
                  }
                  .wrap { max-width: 960px; margin: 0 auto; }
                  .card {
                    background: #fff;
                    border: 3px solid var(--ink);
                    border-radius: 14px;
                    box-shadow: 6px 6px 0 var(--ink);
                    padding: 24px;
                    margin: 0 0 28px;
                  }
                  .masthead { text-align: center; margin: 0 0 32px; }
                  .logo { display: inline-flex; align-items: center; gap: 12px; transform: rotate(-2deg); }
                  .paw { width: 54px; height: 54px; fill: var(--orange); }
                  .wordmark {
                    font-size: clamp(38px, 8vw, 64px);
                    font-weight: 800;
                    text-transform: uppercase;
                    text-shadow: 3px 3px 0 var(--yellow);
                  }
                  .bang { color: var(--pink); }
                  .tagline { font-size: 18px; font-weight: 700; margin: 14px 0 4px; }
                  .strapline { color: var(--grey); margin: 0; }
                  h2 {
                    margin: 0 0 16px;
                    font-size: 20px;
                    text-transform: uppercase;
                    letter-spacing: 0.05em;
                    border-bottom: 4px solid var(--yellow);
                    padding-bottom: 8px;
                  }
                  h3 { font-size: 14px; text-transform: uppercase; letter-spacing: 0.08em; margin: 22px 0 6px; }
                  .hero { text-align: center; }
                  .eyebrow {
                    margin: 0;
                    color: var(--orange);
                    font-weight: 800;
                    text-transform: uppercase;
                    letter-spacing: 0.16em;
                  }
                  .count {
                    margin: 4px 0 10px;
                    font-size: clamp(64px, 14vw, 132px);
                    line-height: 1;
                    font-weight: 800;
                    color: var(--pink);
                    text-shadow: 4px 4px 0 var(--ink);
                  }
                  .lede { margin: 0; }
                  .badge {
                    display: inline-block;
                    padding: 6px 14px;
                    border: 3px solid var(--ink);
                    border-radius: 999px;
                    color: #fff;
                    font-size: 13px;
                    font-weight: 800;
                    text-transform: uppercase;
                    letter-spacing: 0.06em;
                  }
                  .badge-memory { background: var(--orange); }
                  .badge-database { background: var(--blue); }
                  .chips { margin: 14px 0 0; }
                  .chip {
                    display: inline-block;
                    margin: 0 8px 8px 0;
                    padding: 4px 10px;
                    border: 2px solid var(--ink);
                    border-radius: 6px;
                    background: var(--cream);
                    font-size: 12px;
                    font-weight: 700;
                    text-transform: uppercase;
                  }
                  .chip-no { background: #fff; color: var(--grey); border-color: var(--grey); }
                  .details { display: grid; grid-template-columns: max-content 1fr; gap: 6px 16px; margin: 18px 0 0; }
                  .details dt {
                    color: var(--grey);
                    font-size: 12px;
                    font-weight: 800;
                    text-transform: uppercase;
                    letter-spacing: 0.06em;
                  }
                  .details dd { margin: 0; font-family: var(--mono); font-size: 13px; word-break: break-all; }
                  table { width: 100%; border-collapse: collapse; font-size: 13px; }
                  th {
                    padding: 6px 8px;
                    border-bottom: 2px solid var(--ink);
                    color: var(--grey);
                    font-size: 11px;
                    letter-spacing: 0.08em;
                    text-align: left;
                    text-transform: uppercase;
                  }
                  td { padding: 6px 8px; border-bottom: 1px solid #e7e2d8; vertical-align: top; }
                  tbody tr:nth-child(even) td { background: #faf7f1; }
                  .name { font-family: var(--mono); font-weight: 700; word-break: break-all; }
                  .value { font-family: var(--mono); word-break: break-all; }
                  .link-group { border-left: 6px solid var(--yellow); padding-left: 14px; }
                  .note { margin: 0 0 10px; color: var(--grey); font-size: 13px; }
                  .masked td { background: #f4f4f5; }
                  .mask { color: var(--grey); }
                  .tag {
                    margin-left: 8px;
                    padding: 1px 6px;
                    border-radius: 4px;
                    background: var(--grey);
                    color: #fff;
                    font-size: 10px;
                    text-transform: uppercase;
                    letter-spacing: 0.08em;
                  }
                  .pill { margin-left: 6px; padding: 1px 9px; border-radius: 999px; background: var(--ink); color: #fff; font-size: 12px; }
                  summary { padding: 8px 0; cursor: pointer; font-size: 13px; font-weight: 800; text-transform: uppercase; }
                  footer { color: var(--grey); font-size: 13px; text-align: center; }
                </style>
              </head>
              <body>
                <div class="wrap">
                  <header class="masthead">
                    <div class="logo">
                      <svg class="paw" viewBox="0 0 64 64" role="img" aria-label="A paw">
                        <ellipse cx="32" cy="44" rx="17" ry="13" />
                        <circle cx="13" cy="27" r="7" />
                        <circle cx="25" cy="16" r="7.5" />
                        <circle cx="39" cy="16" r="7.5" />
                        <circle cx="51" cy="27" r="7" />
                      </svg>
                      <span class="wordmark">Wuphf<span class="bang">!</span>com</span>
                    </div>
                    <p class="tagline">It&rsquo;s the sound a dog makes when he&rsquo;s excited.</p>
                    <p class="strapline">
                      One message. Email, text, twitter, facebook, and the fax machine. All at once.
                    </p>
                  </header>
            """;

    private static final String FOOT =
            """
                  <footer>WUPHF.com &mdash; a demo service for blcks. No dogs were disturbed.</footer>
                </div>
              </body>
            </html>
            """;

    /** The page for {@code report}. The same report always gives the same page. */
    static String render(VisitReport report) {
        StringBuilder page = new StringBuilder(HEAD);
        hero(page, report.visited());
        storage(page, report.storage());
        environment(page, report.environment());
        return page.append(FOOT).toString();
    }

    private static void hero(StringBuilder page, long visited) {
        page.append("      <section class=\"card hero\">\n")
                .append("        <p class=\"eyebrow\">Wuphfs sent</p>\n")
                .append("        <p class=\"count\">")
                .append(visited)
                .append("</p>\n")
                .append("        <p class=\"lede\">This service has been visited <strong>")
                .append(visited)
                .append("</strong> time(s).</p>\n")
                .append("      </section>\n");
    }

    private static void storage(StringBuilder page, StorageBackend backend) {
        page.append("      <section class=\"card\">\n")
                .append("        <h2>Where the wuphfs sleep</h2>\n")
                .append("        <p><span class=\"badge badge-")
                .append(e(backend.kind()))
                .append("\">")
                .append(e(backend.name()))
                .append("</span></p>\n")
                .append("        <p class=\"lede\">")
                .append(e(backend.description()))
                .append("</p>\n")
                .append("        <p class=\"chips\">")
                .append(chip(backend.persistent(), "Survives a restart", "Gone on restart"))
                .append(chip(backend.shared(), "Shared by every instance", "This process only"))
                .append("</p>\n");

        if (!backend.details().isEmpty()) {
            page.append("        <dl class=\"details\">\n");
            for (Map.Entry<String, String> detail : backend.details().entrySet()) {
                page.append("          <dt>")
                        .append(e(detail.getKey()))
                        .append("</dt><dd>")
                        .append(e(detail.getValue()))
                        .append("</dd>\n");
            }
            page.append("        </dl>\n");
        }

        page.append("      </section>\n");
    }

    private static String chip(boolean yes, String whenYes, String whenNo) {
        return "<span class=\"chip "
                + (yes ? "chip-yes" : "chip-no")
                + "\">"
                + e(yes ? whenYes : whenNo)
                + "</span>";
    }

    private static void environment(StringBuilder page, EnvironmentReport report) {
        page.append("      <section class=\"card\">\n")
                .append("        <h2>What the process was handed</h2>\n")
                .append("        <div class=\"link-group\">\n")
                .append("          <h3>Link variables<span class=\"pill\">")
                .append(report.link().size())
                .append("</span></h3>\n");

        if (report.link().isEmpty()) {
            page.append("          <p class=\"note\">No link variables. This one is a lone dog.</p>\n");
        } else {
            page.append("          <p class=\"note\">A blcks link put these here.</p>\n");
            table(page, report.link());
        }

        page.append("        </div>\n")
                .append("        <details>\n")
                .append("          <summary>And ")
                .append(report.other().size())
                .append(" more from the process</summary>\n");
        table(page, report.other());
        page.append("        </details>\n").append("      </section>\n");
    }

    private static void table(StringBuilder page, List<EnvironmentVariable> variables) {
        page.append("          <table>\n")
                .append("            <thead><tr><th scope=\"col\">Name</th>")
                .append("<th scope=\"col\">Value</th></tr></thead>\n")
                .append("            <tbody>\n");

        for (EnvironmentVariable variable : variables) {
            page.append("              <tr")
                    .append(variable.secret() ? " class=\"masked\"" : "")
                    .append("><td class=\"name\">")
                    .append(e(variable.name()))
                    .append("</td><td class=\"value\">");

            if (variable.secret()) {
                page.append("<span class=\"mask\" title=\"the name says this is a secret\">")
                        .append(e(variable.value()))
                        .append("</span><span class=\"tag\">hidden</span>");
            } else {
                page.append(e(variable.value()));
            }

            page.append("</td></tr>\n");
        }

        page.append("            </tbody>\n").append("          </table>\n");
    }

    /** {@code text} as it may go into the page. */
    private static String e(String text) {
        return text == null ? "" : HtmlUtils.htmlEscape(text);
    }

    private WuphfPage() {}
}
