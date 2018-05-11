package cz.spiffyk.uirsp

import cz.spiffyk.uirsp.classification.ClassificationResult
import cz.spiffyk.uirsp.classification.classifiers.KMeansClassifier
import cz.spiffyk.uirsp.preprocess.preprocessors.BagOfWordsPreprocessor
import cz.spiffyk.uirsp.preprocess.preprocessors.NGramPreprocessor
import cz.spiffyk.uirsp.preprocess.PreprocessResult
import cz.spiffyk.uirsp.preprocess.preprocessors.TfIdfPreprocessor
import cz.spiffyk.uirsp.tweet.Tweet
import cz.spiffyk.uirsp.tweet.TweetsCsvParser
import cz.spiffyk.uirsp.util.ArgsDto
import kotlin.system.exitProcess

const val STATUS_HELP: Int = 255
const val STATUS_INVALID_ARGS: Int = 1

/**
 * Program entry point.
 *
 * @param rawArgs Command line arguments (parsed by [ArgsDto])
 */
fun main(rawArgs: Array<String>) {
    try {
        val args = ArgsDto.of(rawArgs)

        if (args === null) {
            println(ArgsDto.HELP_TEXT)
            exitProcess(STATUS_HELP)
        }

        print("Parsing '${args.inputFile.absolutePath}'...")
        val parseStart = System.currentTimeMillis()
        val tweets = TweetsCsvParser.parseFile(args.inputFile)
        val parseEnd = System.currentTimeMillis()
        println("done (${tweets.size} tweets in ${parseEnd - parseStart} ms)")

        print("Preprocessing...")
        val preprocessStart = System.currentTimeMillis()
        val preprocessResult: PreprocessResult = preprocess(tweets, args.preprocessorType)
        val preprocessEnd = System.currentTimeMillis()

        println("done. (in ${preprocessEnd - preprocessStart} ms)")

        print("Classifying...")
        val classificationStart = System.currentTimeMillis()
        val classificationResult: ClassificationResult = classify(preprocessResult, args.classifierType)
        val classificationEnd = System.currentTimeMillis()
        println("done. (in ${classificationEnd - classificationStart} ms)")

        TODO("Post-process")

        TODO("Save results")

    } catch (e: ArgsDto.InvalidArgsException) {
        println("${e.javaClass.simpleName}: ${e.message}")
        println(ArgsDto.HELP_TEXT)
        exitProcess(STATUS_INVALID_ARGS)
    }
}

private fun preprocess(tweets: List<Tweet>, preprocessorType: ArgsDto.PreprocessorType): PreprocessResult =
        when(preprocessorType) {
            ArgsDto.PreprocessorType.BAG_OF_WORDS ->
                BagOfWordsPreprocessor.preprocess(tweets)
            ArgsDto.PreprocessorType.N_GRAM ->
                NGramPreprocessor.preprocess(tweets, 3)
            ArgsDto.PreprocessorType.TF_IDF ->
                TfIdfPreprocessor.preprocess(tweets)
        }

private fun classify(preprocessResult: PreprocessResult, classifierType: ArgsDto.ClassifierType): ClassificationResult =
        when(classifierType) {
            ArgsDto.ClassifierType.K_MEANS -> KMeansClassifier.classify(preprocessResult)
            ArgsDto.ClassifierType.K_NN -> TODO("K-NN is not yet implemented")
        }
