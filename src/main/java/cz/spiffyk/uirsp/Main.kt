package cz.spiffyk.uirsp

import cz.spiffyk.uirsp.classification.ClassificationResult
import cz.spiffyk.uirsp.classification.classifiers.KMeansClassifier
import cz.spiffyk.uirsp.classification.classifiers.KNNClassifier
import cz.spiffyk.uirsp.preprocess.preprocessors.BagOfWordsPreprocessor
import cz.spiffyk.uirsp.preprocess.preprocessors.NGramPreprocessor
import cz.spiffyk.uirsp.preprocess.PreprocessResult
import cz.spiffyk.uirsp.preprocess.preprocessors.TfIdfPreprocessor
import cz.spiffyk.uirsp.stats.StatsCalculator
import cz.spiffyk.uirsp.tweet.Tweet
import cz.spiffyk.uirsp.tweet.TweetsCsvParser
import cz.spiffyk.uirsp.util.Arguments
import cz.spiffyk.uirsp.util.printMessagesRecursively
import java.io.File
import kotlin.system.exitProcess

const val STATUS_HELP: Int = 255
const val STATUS_INVALID_ARGS: Int = 1
const val STATUS_PARSER_EXCEPTION: Int = 2

const val OUTPUT_STATS_FILENAME = "stats.txt"
const val OUTPUT_ARGS_FILENAME = "config.txt"
const val OUTPUT_TIMES_FILENAME = "times.txt"
const val OUTPUT_TWEETS_FILE_EXT = ".csv"

/**
 * Program entry point.
 *
 * @param rawArgs Command line arguments (parsed by [Arguments])
 */
fun main(rawArgs: Array<String>) {
    try {
        val args = Arguments.of(rawArgs)

        if (args === null) {
            println(Arguments.HELP_TEXT)
            exitProcess(STATUS_HELP)
        }

        print("Parsing...")
        val parseStart = System.currentTimeMillis()
        val tweets = TweetsCsvParser.parseFile(args.inputFile)
        val parseEnd = System.currentTimeMillis()
        val parseTime = (parseEnd - parseStart) * 0.001
        println(" done (${tweets.size} tweets in ${String.format("%.3f", parseTime)} s)")

        print("Preprocessing...")
        val preprocessStart = System.currentTimeMillis()
        val preprocessResult: PreprocessResult = preprocess(tweets, args.preprocessorType)
        val preprocessEnd = System.currentTimeMillis()
        val preprocessTime = (preprocessEnd - preprocessStart) * 0.001
        println(" done (in ${String.format("%.3f", preprocessTime)} s)")

        print("Classifying...")
        val classificationStart = System.currentTimeMillis()
        val classificationResult: ClassificationResult = classify(
                preprocessResult,
                args.classifierType,
                args.teacherRatio,
                args.k)
        val classificationEnd = System.currentTimeMillis()
        val classificationTime = (classificationEnd - classificationStart) * 0.001
        println(" done (in ${String.format("%.3f", classificationTime)} s)")

        print("Saving...")
        val saveStart = System.currentTimeMillis()
        save(
                args = args,
                classificationResult = classificationResult,
                times = Times(parseTime, preprocessTime, classificationTime),
                outputDir = args.outputDir)
        val saveEnd = System.currentTimeMillis()
        println(" done (in ${String.format("%.3f", (saveEnd - saveStart) * 0.001)} s)")

    } catch (e: Arguments.InvalidArgsException) {
        e.printMessagesRecursively()
        println(Arguments.HELP_TEXT)
        exitProcess(STATUS_INVALID_ARGS)
    } catch (e: TweetsCsvParser.ParserException) {
        println()
        e.printMessagesRecursively()
        exitProcess(STATUS_PARSER_EXCEPTION)
    }
}



/**
 * Starts preprocessing.
 */
private fun preprocess(tweets: List<Tweet>, preprocessorType: Arguments.PreprocessorType): PreprocessResult =
        when(preprocessorType) {
            Arguments.PreprocessorType.BAG_OF_WORDS ->
                BagOfWordsPreprocessor.preprocess(tweets)
            Arguments.PreprocessorType.N_GRAM ->
                NGramPreprocessor.preprocess(tweets, 3)
            Arguments.PreprocessorType.TF_IDF ->
                TfIdfPreprocessor.preprocess(tweets)
        }

/**
 * Starts classification.
 */
private fun classify(preprocessResult: PreprocessResult,
                     classifierType: Arguments.ClassifierType,
                     teacherRatio: Double,
                     k: Int): ClassificationResult =
        when(classifierType) {
            Arguments.ClassifierType.K_MEANS -> KMeansClassifier.classify(preprocessResult, teacherRatio)
            Arguments.ClassifierType.K_NN -> KNNClassifier.classify(preprocessResult, teacherRatio, k)
        }

/**
 * Saves a program result into the specified directory.
 *
 * @param args parsed command line [Arguments]
 * @param classificationResult the [ClassificationResult] to save
 * @param outputDir the target directory
 */
private fun save(args: Arguments,
                 classificationResult: ClassificationResult,
                 times: Times,
                 outputDir: File) {
    if (outputDir.exists() && !outputDir.isDirectory) {
        throw IllegalStateException("Output path exists and is not a directory!")
    }
    outputDir.deleteRecursively()
    outputDir.mkdirs()

    saveArgs(args, outputDir)
    saveStats(classificationResult, outputDir)
    saveTimes(times, outputDir)
    saveTweets(classificationResult, outputDir)
}

/**
 * Saves tweets in CSV files separated by their detected topics.
 *
 * @param classificationResult the [ClassificationResult] to get the data from
 * @param outputDir the directory to saves the file into
 */
private fun saveTweets(classificationResult: ClassificationResult,
                       outputDir: File) {

    classificationResult.resultGroups.forEach { topic, group ->
        val out = File(outputDir, "${topic.name}$OUTPUT_TWEETS_FILE_EXT").printWriter()

        group.tweets.forEach {  tweet ->
            out.println(tweet.toCsv())
        }

        out.close()
    }
}

/**
 * Saves precisions, recalls and f-measures of a [ClassificationResult] into a human-readable text file.
 *
 * @param classificationResult the [ClassificationResult] to get the data from
 * @param outputDir the directory to save the file into
 */
private fun saveStats(classificationResult: ClassificationResult,
                      outputDir: File) {
    val out = File(outputDir, OUTPUT_STATS_FILENAME).printWriter()

    val stats = StatsCalculator.calculate(classificationResult)
    stats.topics.forEach {
        out.println("${it.key}:\n" +
                "\tprecision: ${it.value.precision}\n" +
                "\trecall: ${it.value.recall}\n" +
                "\tf-measure: ${it.value.fMeasure}\n")
    }
    out.println("=== MEAN STATS ===\n" +
            "\tprecision: ${stats.mean.precision}\n" +
            "\trecall: ${stats.mean.recall}\n" +
            "\tf-measure: ${stats.mean.fMeasure}\n")

    out.close()
}

/**
 * Saves arguments in a human-readable text file.
 *
 * @param args command line [Arguments]
 * @param outputDir the directory to save the file into
 */
private fun saveArgs(args: Arguments,
                     outputDir: File) {
    val out = File(outputDir, OUTPUT_ARGS_FILENAME).printWriter()
    out.println(args.toReadableString())
    out.close()
}

/**
 * Saves times in a human-readable text file.
 *
 * @param times process [Times]
 * @param outputDir the directory to save the file into
 */
private fun saveTimes(times: Times,
                      outputDir: File) {
    val out = File(outputDir, OUTPUT_TIMES_FILENAME).printWriter()
    out.println(times.toReadableString())
    out.close()
}



private data class Times(val parseTime: Double,
                         val preprocessTime: Double,
                         val classificationTime: Double) {
    fun toReadableString(): String {
        return "parse time: $parseTime seconds\n" +
                "preprocess time: $preprocessTime seconds\n" +
                "classification time: $classificationTime seconds"
    }
}
