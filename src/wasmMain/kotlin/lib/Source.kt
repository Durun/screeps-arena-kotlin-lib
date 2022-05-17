package lib

import com.github.durun.screeps.arena.jsinterop.getHeapInt32
import com.github.durun.screeps.arena.jsinterop.getSources_heap
import com.github.durun.screeps.arena.jsinterop.sourceEnergy
import com.github.durun.screeps.arena.jsinterop.sourceEnergyCapacity

class Source private constructor(index: Int): GameObject(index) {
    companion object {
        fun getAll(): List<Source> {
            val size = getSources_heap()
            return (0 until size).map { Source(getHeapInt32(it)) }
        }
    }

    val energy: Int get() = sourceEnergy(this.index)
    val energyCapacity: Int get() = sourceEnergyCapacity(this.index)
}