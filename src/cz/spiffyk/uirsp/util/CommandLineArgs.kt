package cz.spiffyk.uirsp.util

/**
 * A class representing parsed command line options.
 *
 * @property help Whether program help should be shown
 */
data class CommandLineArgs(var file: String? = null,
                           var help: Boolean = false) {

    /**
     * Parses command line arguments to from a [CommandLineArgs] object.
     *
     * @param args Command line arguments as passed to a `main(args: Array<String>)` entry point
     *
     * @throws InvalidArgsException when command line arguments are invalid
     */
    @Throws(InvalidArgsException::class)
    constructor(args: Array<String>) : this() {
        for(arg in args) {
            if (arg.startsWith("-")) {
                when (arg) {
                    "-h", "--help" -> {
                        help = true
                    }
                    else -> {
                        throw InvalidArgsException("Unknown switch '$arg'")
                    }
                }
            } else {
                if (file !== null) {
                    throw InvalidArgsException("Only one input file is allowed!")
                }
                file = arg
            }
        }
    }

    /**
     * Thrown when CLI arguments are invalid.
     */
    class InvalidArgsException(override var message: String? = null, override var cause: Throwable? = null):
            Exception(message, cause)
}