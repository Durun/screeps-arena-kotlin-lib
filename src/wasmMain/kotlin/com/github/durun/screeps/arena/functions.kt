package com.github.durun.screeps.arena

fun getCpuTime(): Long {
    TODO()
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
        fun of(int: Int) = when (int) {
            1 -> Wall
            2 -> Swamp
            else -> throw IllegalArgumentException("Terrain.of(): int=$int")
        }
    }
}

fun getTerrainAt(x: Int, y: Int): Terrain? {
    TODO()
}

fun getTicks(): Int {
    TODO()
}