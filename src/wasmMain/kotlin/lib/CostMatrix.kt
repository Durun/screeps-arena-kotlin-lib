package lib

import com.github.durun.screeps.arena.jsinterop.costMatrixClone
import com.github.durun.screeps.arena.jsinterop.costMatrixGet
import com.github.durun.screeps.arena.jsinterop.costMatrixSet
import com.github.durun.screeps.arena.jsinterop.newCostMatrix

class CostMatrix private constructor(val index: Int) {
    companion object {
        fun new(): CostMatrix {
            return CostMatrix(newCostMatrix())
        }
    }

    operator fun get(x: Int, y: Int): Int {
        return costMatrixGet(this.index, x, y)
    }

    operator fun set(x: Int, y: Int, cost: Int) {
        return costMatrixSet(this.index, x, y, cost)
    }

    fun clone(): CostMatrix {
        val newIndex = costMatrixClone(this.index)
        return CostMatrix(newIndex)
    }
}