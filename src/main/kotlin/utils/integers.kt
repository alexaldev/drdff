package utils

fun oneThousand(): Set<Int> {
    return (0 ..1_000)
        .fold(mutableSetOf()) {acc, i -> acc.add(i); acc }
}

val Int.isOdd: Boolean
    get() = this%2 != 0

val Int.isEven: Boolean
    get() = this%2 == 0