package com.github.durun.screeps.arena

import com.github.durun.screeps.arena.jsinterop.*

object ArenaInfo {
    val name: String by lazy {
        val size = arenaInfoName()
        val bytes = ByteArray(size) { getHeapUint8(it) }
        bytes.decodeToString()
    }
    val level: Int by lazy { arenaInfoLevel() }
    val season: String by lazy {
        val size = arenaInfoSeason()
        val bytes = ByteArray(size) { getHeapUint8(it) }
        bytes.decodeToString()
    }
    val ticksLimit: Int by lazy { arenaInfoTicksLimit() }
    val cpuTimeLimit: Int by lazy { arenaInfoCpuTimeLimit() }
    val cpuTimeLimitFirstTick: Int by lazy { arenaInfoCpuTimeLimitFirstTick() }
}