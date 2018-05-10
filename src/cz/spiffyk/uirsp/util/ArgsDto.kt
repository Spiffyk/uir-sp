package cz.spiffyk.uirsp.util

import java.io.File

/**
 * A class representing parsed command line options.
 */
data class ArgsDto(val inputFile: File,
                   val outputDir: File,
                   val preprocessorType: PreprocessorType,
                   val n: Int,
                   val classifierType: ClassifierType,
                   val k: Int) {

    companion object {
        private const val DEFAULT_OUTPUT_DIR = "tweets-output"
        private const val DEFAULT_N_GRAM_N = 3
        private const val DEFAULT_K_NN_K = 6

        private val PREPROCESSOR_LIST = PreprocessorType.values().joinToString(
                separator = ", ",
                transform = { it.code })

        private val CLASSIFIER_LIST = ClassifierType.values().joinToString(
                separator = ", ",
                transform = { it.code })

        val HELP_TEXT =
                "Supported switches:\n" +
                        "-i <file>       |  --input <file>          - sets the input file\n" +
                        "-o <directory>  |  --output <directory>    - sets the output directory (default: \"$DEFAULT_OUTPUT_DIR\")\n" +
                        "-p <algo>       |  --preprocessor <algo>   - sets the preprocessor algorithm\n" +
                        "-n <n>          |  --n <n>                 - sets the parameter for n-gram (default: $DEFAULT_N_GRAM_N)\n" +
                        "-c <algo>       |  --classifier <algo>     - sets the classifier algorithm\n" +
                        "-k <k>          |  --k <k>                 - sets the parameter for k-NN (default: $DEFAULT_K_NN_K)\n" +
                        "-h              |  --help                  - shows this help\n" +
                        "\n" +
                        "Supported preprocessors: $PREPROCESSOR_LIST\n" +
                        "Supported classifiers: $CLASSIFIER_LIST\n"

        /**
         * Parses command line arguments into a [ArgsDto] object.
         *
         * @param args Command line arguments as passed to a `main(args: Array<String>)` entry point
         *
         * @return an [ArgsDto] object parsed from CLI arguments or `null` if `--help` or `-h` was found
         *
         * @throws InvalidArgsException when command line arguments are invalid
         */
        @Throws(InvalidArgsException::class)
        fun of(args: Array<String>): ArgsDto? {
            var inputFile: File? = null
            var outputDir: File? = null
            var preprocessorType: PreprocessorType? = null
            var n = DEFAULT_N_GRAM_N
            var classifierType: ClassifierType? = null
            var k = DEFAULT_K_NN_K

            val iterator = args.iterator()
            while (iterator.hasNext()) {
                val arg = iterator.next()

                if (arg.startsWith("-")) {
                    when (arg) {
                        "-i", "--input" -> {
                            if (!iterator.hasNext()) {
                                throw InvalidArgsException("The '$arg' switch requires another argument as a parameter!")
                            }

                            inputFile = File(iterator.next())
                        }

                        "-o", "--output" -> {
                            if (!iterator.hasNext()) {
                                throw InvalidArgsException("The '$arg' switch requires another argument as a parameter!")
                            }

                            outputDir = File(iterator.next())
                        }

                        "-p", "--preprocessor" -> {
                            if (!iterator.hasNext()) {
                                throw InvalidArgsException("The '$arg' switch requires another argument as a parameter!")
                            }

                            preprocessorType = PreprocessorType.of(iterator.next())
                        }

                        "-n", "--n" -> {
                            if (!iterator.hasNext()) {
                                throw InvalidArgsException("The '$arg' switch requires another argument as a parameter!")
                            }

                            val stringN = iterator.next()
                            try {
                                n = Integer.parseInt(stringN)
                            } catch (e: NumberFormatException) {
                                throw InvalidArgsException("'$stringN' is not a valid integer!", e)
                            }
                        }

                        "-c", "--classifier" -> {
                            if (!iterator.hasNext()) {
                                throw InvalidArgsException("The '$arg' switch requires another argument as a parameter!")
                            }

                            classifierType = ClassifierType.of(iterator.next())
                        }

                        "-k", "--k" -> {
                            if (!iterator.hasNext()) {
                                throw InvalidArgsException("The '$arg' switch requires another argument as a parameter!")
                            }

                            val stringK = iterator.next()
                            try {
                                k = Integer.parseInt(stringK)
                            } catch (e: NumberFormatException) {
                                throw InvalidArgsException("'$stringK' is not a valid integer!", e)
                            }
                        }

                        "-h", "--help" -> {
                            return null
                        }
                        else -> {
                            throw InvalidArgsException("Unknown switch '$arg'")
                        }
                    }
                } else {
                    throw InvalidArgsException("Dangling argument '$arg'!")
                }
            }

            return ArgsDto(
                    inputFile = inputFile ?: throw InvalidArgsException("An input file was not provided!"),
                    outputDir = outputDir ?: File(DEFAULT_OUTPUT_DIR),
                    preprocessorType = preprocessorType ?: throw InvalidArgsException("A preprocessor type was not provided!"),
                    n = n,
                    classifierType = classifierType ?: throw InvalidArgsException("A classifier type was not provided!"),
                    k = k)
        }
    }

    enum class PreprocessorType(val code: String) {
        BAG_OF_WORDS("bow"),
        N_GRAM("ngram"),
        TF_IDF("tfidf");

        companion object {
            fun of(code: String): PreprocessorType {
                values().forEach {
                    if (it.code == code) {
                        return it
                    }
                }

                throw InvalidArgsException("Unknown preprocessor type '$code'!")
            }
        }
    }

    enum class ClassifierType(val code: String) {
        K_MEANS("kmeans"),
        K_NN("knn");

        companion object {
            fun of(code: String): ClassifierType {
                values().forEach {
                    if (it.code == code) {
                        return it
                    }
                }

                throw InvalidArgsException("Unknown classifier type '$code'!")
            }
        }
    }

    /**
     * Thrown when CLI arguments are invalid.
     */
    class InvalidArgsException(override var message: String? = null, override var cause: Throwable? = null):
            Exception(message, cause)
}