package de.solidblocks.examples.javaspringwuphf

interface VisitCounter {
    fun recordVisit(): Long

    fun storage(): StorageBackend
}
