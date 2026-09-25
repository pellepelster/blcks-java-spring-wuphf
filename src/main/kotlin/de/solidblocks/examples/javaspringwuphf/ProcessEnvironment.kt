package de.solidblocks.examples.javaspringwuphf

class ProcessEnvironment(environment: Map<String, String>) {
    private val report = report(environment)

    fun report() = report

    companion object {
        const val LINK_PREFIX = "BLCKS_"

        private val BY_NAME =
            compareBy<EnvironmentVariable, String>(String.CASE_INSENSITIVE_ORDER) { it.name }
                .thenBy { it.name }

        private fun report(environment: Map<String, String>): EnvironmentReport {
            val (link, other) =
                environment
                    .map { (name, value) ->
                        EnvironmentVariable(
                            name,
                            Secrets.maskIfSecret(name, value),
                            Secrets.isSecretName(name),
                        )
                    }
                    .partition { it.name.startsWith(LINK_PREFIX) }

            return EnvironmentReport(link.sortedWith(BY_NAME), other.sortedWith(BY_NAME))
        }
    }
}
