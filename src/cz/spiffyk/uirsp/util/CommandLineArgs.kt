package cz.spiffyk.uirsp.util

/**
 * A class representing parsed command line options.
 *
 * @property isValid `false` when an unknown command line switch has been passed; otherwise `true`
 * @property help Whether program help should be shown
 */
data class CommandLineArgs(var isValid: Boolean = true,
                           var help: Boolean = false) {

    /**
     * Parses command line arguments to from a [CommandLineArgs] object.
     *
     * @param args Command line arguments as passed to a `main(args: Array<String>)` entry point
     */
    constructor(args: Array<String>) : this() {
        for(arg in args) {
            if (arg.startsWith("-")) {
                when (arg) {
                    "-h", "--help" -> {
                        help = true
                    }
                    else -> {
                        isValid = false
                        System.err.println("Unknown switch '$arg'")
                    }
                }
            } else {
                isValid = false
                System.err.println("Invalid argument '$arg'")
            }
        }
    }

}