package cz.spiffyk.uirsp.util

fun Throwable.printMessagesRecursively(indent: Int = 0) {
    val sb = StringBuilder()
    for (i in 1..indent) {
        sb.append(">> ")
    }
    sb.append("(${this.javaClass.simpleName}): ${this.message}")
    println(sb)
    this.cause?.printMessagesRecursively(indent + 1)
}
