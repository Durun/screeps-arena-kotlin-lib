package com.github.durun.screeps.arena.api

/**
 * cost 50
 * Decreases fatigue by 2 points per tick.
 */
val MOVE = BodyType.MOVE

/**
 * cost	100
 * Harvests 2 energy units from a source per tick.
 * Builds a structure for 5 energy units per tick.
 * Dismantles a structure for 50 hits per tick.
 */
val WORK = BodyType.WORK

/**
 * cost 50
 * Can contain up to 50 resource units.
 */
val CARRY = BodyType.CARRY

/**
 * cost 80
 * Attacks another creep/structure with 30 hits per tick in a short-ranged attack.
 */
val ATTACK = BodyType.ATTACK

/**
 * cost 150
 * Attacks another single creep/structure with 10 hits per tick in a long-range attack up to 3 squares long.
 * Attacks all hostile creeps/structures within 3 squares range with 1-4-10 hits (depending on the range).
 */
val RANGED_ATTACK = BodyType.RANGED_ATTACK

/**
 * cost 250
 * Heals self or another creep restoring 12 hits per tick in short range or 4 hits per tick at a distance.
 */
val HEAL = BodyType.HEAL

/**
 * cost 10
 * No effect, just additional hit points to the creep's body.
 */
val TOUGH = BodyType.TOUGH

enum class BodyType(val int: Int) {
    MOVE(0),
    WORK(1),
    CARRY(2),
    ATTACK(3),
    RANGED_ATTACK(4),
    HEAL(5),
    TOUGH(6),
    ;

    companion object {
        internal fun of(int: Int): BodyType = when (int) {
            0 -> MOVE
            1 -> WORK
            2 -> CARRY
            3 -> ATTACK
            4 -> RANGED_ATTACK
            5 -> HEAL
            6 -> TOUGH
            else -> {
                throw Exception("Illegal Number of BodyType: $int")
            }
        }
    }
}

data class BodyPart internal constructor(val type: BodyType, val hits: Int)