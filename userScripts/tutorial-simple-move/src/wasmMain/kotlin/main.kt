import com.github.durun.screeps.arena.api.Creep
import com.github.durun.screeps.arena.api.Flag

val creep = Creep.getAll().first { it.my }
val flag = Flag.getAll().first()

fun main() {
    creep.moveTo(flag)
}
