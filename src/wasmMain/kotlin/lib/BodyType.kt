package lib

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
        fun of(int: Int): BodyType = when (int) {
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

data class BodyPart(val type: BodyType, val hits: Int)