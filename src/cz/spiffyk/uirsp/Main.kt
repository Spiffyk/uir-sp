package cz.spiffyk.uirsp

import cz.spiffyk.uirsp.classification.ClassificationResult
import cz.spiffyk.uirsp.preprocessing.BagOfWordsPreprocessor
import cz.spiffyk.uirsp.preprocessing.NGramPreprocessor
import cz.spiffyk.uirsp.preprocessing.PreprocessResult
import cz.spiffyk.uirsp.preprocessing.TfIdfPreprocessor
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

        print("Parsing...")
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
                BagOfWordsPreprocessor.generate(tweets)
            ArgsDto.PreprocessorType.N_GRAM ->
                NGramPreprocessor.generate(tweets, 3)
            ArgsDto.PreprocessorType.TF_IDF ->
                TfIdfPreprocessor.generate(tweets)
        }

private fun classify(preprocessResult: PreprocessResult, classifierType: ArgsDto.ClassifierType): ClassificationResult =
        when(classifierType) {
            ArgsDto.ClassifierType.K_MEANS -> TODO()
            ArgsDto.ClassifierType.K_NN -> TODO()
        }
