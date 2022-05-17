package com.github.durun.screeps.arena

val MOVE = BodyType.MOVE
val WORK = BodyType.WORK
val CARRY = BodyType.CARRY
val ATTACK = BodyType.ATTACK
val RANGED_ATTACK = BodyType.RANGED_ATTACK
val HEAL = BodyType.HEAL
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