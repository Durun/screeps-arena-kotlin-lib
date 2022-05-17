package lib

enum class Err(val code: Int) {
    // Ok(0), -> null
    NotOwner(-1),
    NoPath(-2),
    Busy(-4),
    NotFound(-5),
    NotEnoughResources(-6),
    InvalidTarget(-7),
    Full(-8),
    NotInRange(-9),
    InvalidArgs(-10),
    Tired(-11),
    NoBodyPart(-12),
    ;

    companion object {
        fun of(int: Int): Err? {
            return if (int==0) null
            else Err.values().find { it.code == int }
                ?: throw Exception("Illegal number of ReturnCode: $int")
        }
    }
}