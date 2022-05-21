import com.github.durun.screeps.arena.api.*
import com.github.durun.screeps.arena.utils.attackCatching
import com.github.durun.screeps.arena.utils.harvestCatching
import com.github.durun.screeps.arena.utils.spawnCreepCatching
import com.github.durun.screeps.arena.utils.transferCatching


/*
// simple move
val creep = Creep.getAll().first()
val flag = Flag.getAll().first()

fun main() {
    creep.moveTo(flag)
}
 */

/*
// first attack
val myCreep = Creep.getAll().find { it.my }!!
val enemy = Creep.getAll().find { !it.my }!!

fun main() {
    if (myCreep.attack(enemy) == Err.NotInRange) {
        myCreep.moveTo(enemy)
    }
}
 */

/*
// creeps bodies
val creeps = Creep.getAll()
val myCreeps = creeps.filter { it.my }
val enemy = creeps.find { !it.my }!!
fun main() {
    myCreeps.forEach { creep ->
        when {
            creep.body.any { it.type == BodyType.ATTACK } -> {
                if (creep.attack(enemy) == Err.NotInRange) {
                    creep.moveTo(enemy)
                }
            }
            creep.body.any { it.type == BodyType.RANGED_ATTACK } -> {
                if (creep.rangedAttack(enemy) == Err.NotInRange) {
                    creep.moveTo(enemy)
                }
            }
            creep.body.any { it.type == BodyType.HEAL } -> {
                val damagedCreep = myCreeps.find { it.hits < it.hitsMax }
                if (damagedCreep != null) {
                    if (creep.heal(damagedCreep) == Err.NotInRange) {
                        creep.moveTo(damagedCreep)
                    }
                }
            }
        }
    }
}
 */

/*
// store and transfer
val creeps = Creep.getAll()
val myCreep = creeps.first { it.my }
val otherCreeps = creeps.filter { it != myCreep }
val enemy = creeps.first { !it.my }

val tower = Tower.getAll().first { it.my }
val container = Container.getAll().first()

fun main() {
    if (tower.store.energy < 10) {
        if (myCreep.store.energy == 0) {
            myCreep.withdraw(container)
        } else {
            myCreep.transfer(tower)
        }
    } else {
        tower.attack(enemy)
    }

    time++
    println("Energy = ${myCreep.store.energy}")
    println("Capacity = ${myCreep.store.getCapacity()}")
    println("FreeCapacity = ${myCreep.store.getFreeCapacity()}")
    println("UsedCapacity = ${myCreep.store.getUsedCapacity()}")
}
 */

/*
// spawn creeps

val mySpawn = Spawn.getAll().first()
val flags = Flag.getAll()

var creep1: Creep? = null
var creep2: Creep? = null

fun main() {
    val c1 = creep1
    if (c1 == null) {
        creep1 = mySpawn.spawnCreep(listOf(BodyType.MOVE)).first
    } else {
        c1.moveTo(flags[0])

        val c2 = creep2
        if (c2 == null) {
            creep2 = mySpawn.spawnCreep(listOf(BodyType.MOVE)).first
        } else {
            c2.moveTo(flags[1])
        }
    }
}
 */

/*
// harvest energy
val creep = Creep.getAll().first { it.my }
val spawn = Spawn.getAll().first { it.my }
val source = Source.getAll().first()

fun main() {
    if (0 < creep.store.getFreeCapacity(RESOURCE_ENERGY)) {
        if (creep.harvest(source) == Err.NotInRange) {
            creep.moveTo(source)
        }
    } else {
        if (creep.transfer(spawn) == Err.NotInRange) {
            creep.moveTo(spawn)
        }
    }
}
 */

/*
// construction

val creep = Creep.getAll().first {
    println("Hello")
    it.my
}
val container = Container.getAll().first()
var site: ConstructionSite<Tower>? = null

fun main() {
    if (creep.store.energy == 0) {
        if (creep.withdraw(container) == Err.NotInRange) {
            creep.moveTo(container)
        }
    } else {
        val s = site
        if (s == null) {
            site = ConstructionSite.createTower(50, 55).first
        } else {
            if (creep.build(s) == Err.NotInRange) {
                creep.moveTo(s)
            }
        }
    }
}
 */

// final test

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