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
import kotlin.system.exitProcess

const val STATUS_HELP: Int = 255
const val STATUS_INVALID_ARGS: Int = 1

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

        println("Parameters: $args\n\n")

        print("Parsing...")
        val parseStart = System.currentTimeMillis()
        val tweets = TweetsCsvParser.parseFile(args.inputFile)
        val parseEnd = System.currentTimeMillis()
        println(" done (${tweets.size} tweets in ${String.format("%.3f", (parseEnd - parseStart) * 0.001)} s)")

        print("Preprocessing...")
        val preprocessStart = System.currentTimeMillis()
        val preprocessResult: PreprocessResult = preprocess(tweets, args.preprocessorType)
        val preprocessEnd = System.currentTimeMillis()

        println(" done (in ${String.format("%.3f", (preprocessEnd - preprocessStart) * 0.001)} s)")

        print("Classifying...")
        val classificationStart = System.currentTimeMillis()
        val classificationResult: ClassificationResult = classify(
                preprocessResult,
                args.classifierType,
                args.teacherRatio,
                args.k)
        val classificationEnd = System.currentTimeMillis()
        println(" done (in ${String.format("%.3f", (classificationEnd - classificationStart) * 0.001)} s)")

        print("\n\n")

        val stats = StatsCalculator.calculate(classificationResult)
        stats.topics.forEach {
            println("${it.key}:\n" +
                    "\tprecision: ${it.value.precision}\n" +
                    "\trecall: ${it.value.recall}\n" +
                    "\tf-measure: ${it.value.fMeasure}\n")
        }

        println("=== MEAN STATS ===\n" +
                "\tprecision: ${stats.mean.precision}\n" +
                "\trecall: ${stats.mean.recall}\n" +
                "\tf-measure: ${stats.mean.fMeasure}\n")
    } catch (e: Arguments.InvalidArgsException) {
        println("${e.javaClass.simpleName}: ${e.message}")
        println(Arguments.HELP_TEXT)
        exitProcess(STATUS_INVALID_ARGS)
    }
}

private fun preprocess(tweets: List<Tweet>, preprocessorType: Arguments.PreprocessorType): PreprocessResult =
        when(preprocessorType) {
            Arguments.PreprocessorType.BAG_OF_WORDS ->
                BagOfWordsPreprocessor.preprocess(tweets)
            Arguments.PreprocessorType.N_GRAM ->
                NGramPreprocessor.preprocess(tweets, 3)
            Arguments.PreprocessorType.TF_IDF ->
                TfIdfPreprocessor.preprocess(tweets)
        }

private fun classify(preprocessResult: PreprocessResult,
                     classifierType: Arguments.ClassifierType,
                     teacherRatio: Double,
                     k: Int): ClassificationResult =
        when(classifierType) {
            Arguments.ClassifierType.K_MEANS -> KMeansClassifier.classify(preprocessResult, teacherRatio)
            Arguments.ClassifierType.K_NN -> KNNClassifier.classify(preprocessResult, teacherRatio, k)
        }
