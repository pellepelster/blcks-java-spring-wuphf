package de.solidblocks.examples.javaspringwuphf

data class VisitReport(
    val visited: Long,
    val storage: StorageBackend,
    val environment: EnvironmentReport,
)
