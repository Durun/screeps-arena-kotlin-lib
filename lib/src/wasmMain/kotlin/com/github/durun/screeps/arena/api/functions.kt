package com.github.durun.screeps.arena.api


/**
 * Get CPU wall time elapsed in the current tick in nanoseconds.
 */
fun getCpuTime(): Int {
    return com.github.durun.screeps.arena.jsinterop.getCpuTime()
}

class HeapStatistics

fun getHeapStatistics(): HeapStatistics {
    TODO()
}

enum class Terrain(val int: Int) {
    Wall(1),
    Swamp(2)
    ;

    companion object {
        fun of(int: Int): Terrain? = when (int) {
            0 -> null
            1 -> Wall
            2 -> Swamp
            else -> throw IllegalArgumentException("Terrain.of(): int=$int")
        }
    }
}

fun getTerrainAt(x: Int, y: Int): Terrain? {
    val code = com.github.durun.screeps.arena.jsinterop.getTerrainAt(x, y)
    return Terrain.of(code)
}

/**
 * Get count of game ticks passed since the start of the game
 */
fun getTicks(): Int {
    return com.github.durun.screeps.arena.jsinterop.getTicks()
}