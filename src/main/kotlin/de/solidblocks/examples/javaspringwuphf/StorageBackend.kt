package de.solidblocks.examples.javaspringwuphf

data class StorageBackend(
    val kind: String,
    val name: String,
    val description: String,
    val persistent: Boolean,
    val shared: Boolean,
    val details: Map<String, String>,
)
