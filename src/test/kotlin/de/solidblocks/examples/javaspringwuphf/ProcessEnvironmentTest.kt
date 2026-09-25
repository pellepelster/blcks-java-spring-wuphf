package de.solidblocks.examples.javaspringwuphf

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test

class ProcessEnvironmentTest {
    private fun report(environment: Map<String, String>) = ProcessEnvironment(environment).report()

    @Test
    fun `the link variables are one group and the rest another`() {
        val report =
            report(
                mapOf(
                    "BLCKS_DATABASE1_HOST" to "10.0.0.42",
                    "BLCKS_DATABASE1_PORT" to "5432",
                    "PATH" to "/usr/bin",
                    "HOME" to "/opt/visits",
                )
            )

        report.link.map { it.name } shouldContainExactly
            listOf("BLCKS_DATABASE1_HOST", "BLCKS_DATABASE1_PORT")
        report.other.map { it.name } shouldContainExactly listOf("HOME", "PATH")
    }

    @Test
    fun `each group is sorted whatever order the environment had`() {
        val environment = linkedMapOf("zebra" to "1", "Apple" to "2", "mango" to "3")

        report(environment).other.map { it.name } shouldContainExactly
            listOf("Apple", "mango", "zebra")
    }

    @Test
    fun `a secret value is replaced and the name stays`() {
        val report = report(mapOf(PASSWORD to "hunter2"))

        report.link shouldHaveSize 1
        val variable = report.link.single()
        variable.name shouldBe PASSWORD
        variable.value shouldBe Secrets.MASK
        variable.secret.shouldBeTrue()
    }

    @Test
    fun `a secret value is nowhere in the report`() {
        report(mapOf(PASSWORD to "hunter2", "PATH" to "/usr/bin")).toString() shouldNotContain
            "hunter2"
    }

    @Test
    fun `an empty environment gives two empty groups`() {
        val report = report(emptyMap())

        report.link.shouldBeEmpty()
        report.other.shouldBeEmpty()
    }

    private companion object {
        const val PASSWORD = "BLCKS_DATABASE1_WUPHF_PASSWORD"
    }
}
