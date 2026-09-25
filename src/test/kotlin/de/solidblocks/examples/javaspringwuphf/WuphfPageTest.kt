package de.solidblocks.examples.javaspringwuphf

import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test

class WuphfPageTest {
    private fun page(environment: Map<String, String>, backend: StorageBackend) =
        PAGE.render(VisitReport(3, backend, ProcessEnvironment(environment).report()))

    @Test
    fun `the page shows the count`() {
        page(emptyMap(), MEMORY) shouldContain "<p class=\"count\">3</p>"
    }

    @Test
    fun `the page names the backend it was given`() {
        val database =
            StorageBackend(
                "database",
                "PostgreSQL",
                "in postgres",
                true,
                true,
                mapOf("host" to "10.0.0.42"),
            )

        val databasePage = page(emptyMap(), database)
        databasePage shouldContain "<span class=\"badge badge-database\">PostgreSQL</span>"
        databasePage shouldContain "10.0.0.42"
        databasePage shouldNotContain "badge badge-memory"

        val memoryPage = page(emptyMap(), MEMORY)
        memoryPage shouldContain "<span class=\"badge badge-memory\">In memory</span>"
        memoryPage shouldNotContain "badge badge-database"
    }

    @Test
    fun `a value with markup in it cannot reach the browser as markup`() {
        val page = page(mapOf("EVIL" to "<script>alert(1)</script>"), MEMORY)

        page shouldContain "&lt;script&gt;"
        page shouldNotContain "<script>alert(1)</script>"
    }

    @Test
    fun `a secret value is not on the page`() {
        val page = page(mapOf("BLCKS_DATABASE1_WUPHF_PASSWORD" to "hunter2"), MEMORY)

        page shouldNotContain "hunter2"
        page shouldContain "BLCKS_DATABASE1_WUPHF_PASSWORD"
        page shouldContain Secrets.MASK
    }

    @Test
    fun `the link variables and the rest are shown apart`() {
        val page = page(mapOf("BLCKS_DATABASE1_HOST" to "10.0.0.42", "PATH" to "/usr/bin"), MEMORY)

        page shouldContain "Link variables"
        page shouldContain "more from the process"
        page.indexOf("BLCKS_DATABASE1_HOST") shouldBeLessThan page.indexOf("PATH")
    }

    @Test
    fun `the page says so when no link gave it anything`() {
        page(mapOf("PATH" to "/usr/bin"), MEMORY) shouldContain "lone dog"
    }

    @Test
    fun `the page asks for no file of its own`() {
        val page = page(emptyMap(), MEMORY)

        page shouldNotContain "http://"
        page shouldNotContain "https://"
        page shouldNotContain "<script"
    }

    private companion object {
        val PAGE = wuphfPage()

        val MEMORY =
            StorageBackend("memory", "In memory", "kept in this process", false, false, emptyMap())
    }
}
