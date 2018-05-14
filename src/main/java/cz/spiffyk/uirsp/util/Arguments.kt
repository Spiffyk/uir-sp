package cz.spiffyk.uirsp.util

import java.io.File

/**
 * A class representing parsed command line options.
 */
data class Arguments(val inputFile: File,
                     val outputDir: File,
                     val preprocessorType: PreprocessorType,
                     val n: Int,
                     val classifierType: ClassifierType,
                     val k: Int,
                     val teacherRatio: Double) {

    companion object {
        private const val DEFAULT_OUTPUT_DIR = "tweets-output"
        private const val DEFAULT_N_GRAM_N = 3
        private const val DEFAULT_K_NN_K = 6
        private const val DEFAULT_TEACHER_RATIO = 0.0

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
                        "-t <ratio>      |  --teacher <ratio>       - sets the percentage of tweets to use for teaching (default: $DEFAULT_TEACHER_RATIO)\n" +
                        "-h              |  --help                  - shows this help\n" +
                        "\n" +
                        "Supported preprocessors: $PREPROCESSOR_LIST\n" +
                        "Supported classifiers: $CLASSIFIER_LIST\n"

        /**
         * Parses command line arguments into a [Arguments] object.
         *
         * @param args Command line arguments as passed to a `main(args: Array<String>)` entry point
         * @return an [Arguments] object parsed from CLI arguments or `null` if `--help` or `-h` was found
         *
         * @throws InvalidArgsException when command line arguments are invalid
         */
        @Throws(InvalidArgsException::class)
        fun of(args: Array<String>): Arguments? {
            var inputFile: File? = null
            var outputDir: File? = null
            var preprocessorType: PreprocessorType? = null
            var n = DEFAULT_N_GRAM_N
            var classifierType: ClassifierType? = null
            var k = DEFAULT_K_NN_K
            var teacherRatio = DEFAULT_TEACHER_RATIO

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
                                n = stringN.toInt()

                                if (n < 1) {
                                    throw InvalidArgsException("n must be 1 or greater!")
                                }
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
                                k = stringK.toInt()

                                if (k < 1) {
                                    throw InvalidArgsException("k must be 1 or greater!")
                                }
                            } catch (e: NumberFormatException) {
                                throw InvalidArgsException("'$stringK' is not a valid integer!", e)
                            }
                        }

                        "-t", "--teacher" -> {
                            if (!iterator.hasNext()) {
                                throw InvalidArgsException("The '$arg' switch requires another argument as a parameter!")
                            }

                            val stringTeacherRatio = iterator.next()
                            try {
                                teacherRatio = stringTeacherRatio.toDouble()

                                if (teacherRatio < 0.0 || teacherRatio > 1.0) {
                                    throw InvalidArgsException("The ratio must be a real number between 0 and 1!")
                                }
                            } catch (e: NumberFormatException) {
                                throw InvalidArgsException("'$stringTeacherRatio' is not a valid real number!", e)
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

            return Arguments(
                    inputFile = inputFile ?: throw InvalidArgsException("An input file was not provided!"),
                    outputDir = outputDir ?: File(DEFAULT_OUTPUT_DIR),
                    preprocessorType = preprocessorType ?: throw InvalidArgsException("A preprocessor type was not provided!"),
                    n = n,
                    classifierType = classifierType ?: throw InvalidArgsException("A classifier type was not provided!"),
                    k = k,
                    teacherRatio = teacherRatio)
        }
    }

    fun toReadableString(): String {
        val sb = StringBuilder()

        sb.append("input file: ${inputFile.absolutePath}\n")
        sb.append("output directory: ${outputDir.absolutePath}\n")
        sb.append("preprocessor algorithm: ${preprocessorType.code}")
        if (preprocessorType === PreprocessorType.N_GRAM) {
            sb.append(" n = $n")
        }
        sb.append('\n')
        sb.append("classifier algorithm: ${classifierType.code}")
        if (classifierType === ClassifierType.K_NN) {
            sb.append(" k = $k")
        }
        sb.append("\n")
        sb.append("teacher ratio: $teacherRatio")

        return sb.toString()
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