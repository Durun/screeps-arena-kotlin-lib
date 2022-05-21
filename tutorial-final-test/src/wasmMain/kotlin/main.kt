import com.github.durun.screeps.arena.api.*
import com.github.durun.screeps.arena.utils.attackCatching
import com.github.durun.screeps.arena.utils.harvestCatching
import com.github.durun.screeps.arena.utils.spawnCreepCatching
import com.github.durun.screeps.arena.utils.transferCatching

val spawn = Spawn.getAll().first { it.my }
val source = Source.getAll().first()
val attackers: MutableList<Creep> = mutableListOf()
val miners: MutableList<Creep> = mutableListOf()
val states: MutableMap<Creep, Int> = mutableMapOf()
const val MINING = 0
const val TRANSFERRING = 1
val enemies = Creep.getAll().filter { !it.my }
fun main() {
    if (miners.isEmpty()) {
        spawn.spawnCreepCatching(listOf(MOVE, CARRY, WORK, WORK, WORK, WORK))
            .onSuccess {
                miners.add(it)
                states[it] = MINING
            }
    } else {
        spawn.spawnCreepCatching(listOf(MOVE, ATTACK, ATTACK, ATTACK))
            .onSuccess { attackers.add(it) }
    }
    miners.forEach { creep ->
        when (states[creep]) {
            MINING -> {
                creep.harvestCatching(source)
                    .on(Err.NotInRange) { creep.moveTo(source) }
                if (creep.store.getFreeCapacity() <= 0) states[creep] = TRANSFERRING
            }
            TRANSFERRING -> {
                creep.transferCatching(spawn)
                    .on(Err.NotInRange) { creep.moveTo(spawn) }
                if (creep.store.energy <= 0) states[creep] = MINING
            }
        }
    }
    val target = spawn.findClosestByRange(enemies)
    if (target != null) attackers.forEach { creep ->
        creep.attackCatching(target)
            .on(Err.NotInRange) { creep.moveTo(target) }
    }
    showVisual()
    println("time: ${getCpuTime()}/${ArenaInfo.cpuTimeLimit}")
}


val font = TextStyle(
    font = "32px",
    opacity = 0.85f,
    color = Color.of(0xBBDDFF),
    bgColor = Color.of(0x111111),
    padding = 0f
)
val style = Style(
    stroke = Color.of(0xFFEE00),
    strokeWidth = 0.05f,
    opacity = 0.60f,
    lineStyle = LineStyle.Dotted
)
val fillStyle = Style(
    strokeWidth = 0f,
    fill = Color.of(0xFF4400),
    opacity = 0.1f,
)

fun showVisual() {
    val visual = Visual.new(10, false)
        .text("Energy: ${spawn.store.energy}", spawn, font)
    miners.forEach { creep ->
        when (states[creep]) {
            MINING -> visual.text(
                "Mining ${creep.store.energy}/${creep.store.getCapacity(RESOURCE_ENERGY)}",
                creep,
                font
            )
            TRANSFERRING -> visual.text("Transferring", creep, font)
        }
    }
}