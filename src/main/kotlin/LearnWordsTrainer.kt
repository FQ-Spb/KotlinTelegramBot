package org.example

import java.io.File

class Statistic(
    val learnedCount: Int,
    val learnedPercent: Int,
    val total: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer {

    private var question: Question? = null
    private val dictionary = loadDictionary()

    private fun loadDictionary(): MutableList<Word> {
        val wordsFile = File("words.txt")
        val dictionary = mutableListOf<Word>()
        var line: List<String>
        var word: Word

        wordsFile.readLines().forEach {
            line = it.split("|")
            word = Word(
                original = line[0],
                translate = line[1],
                correctAnswerCount = line[2].toIntOrNull() ?: 0
            )
            dictionary.add(word)
        }
        return dictionary
    }

    private fun saveDictionary(dictionary: MutableList<Word>) {
        val wordsFile = File("words.txt")
        val listOfWords: MutableList<String> = mutableListOf()

        dictionary.forEach { listOfWords.add(it.toString()) }
        wordsFile.writeText(listOfWords.joinToString("\n"))
    }

    fun getStatistic(): Statistic {
        val total = dictionary.size
        val learnedCount = dictionary.filter { it.correctAnswerCount >= NUMBER_OF_CORRECT_ANSWERS_FOR_REMEMBER }.size
        val learnedPercent = (learnedCount.toDouble() / total * CONST_FOR_PERCENT_CALCULATING).toInt()
        return Statistic(learnedCount, learnedPercent, total)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList: List<Word> =
            dictionary.filter { it.correctAnswerCount < NUMBER_OF_CORRECT_ANSWERS_FOR_REMEMBER }
        if (notLearnedList.isEmpty()) return null
        val questionWords = if (notLearnedList.size < NUMBER_OF_ANSWER_OPTIONS) {
            val reserveList = (notLearnedList+dictionary.shuffled().take(NUMBER_OF_ANSWER_OPTIONS)).distinct().shuffled().take(NUMBER_OF_ANSWER_OPTIONS)
            reserveList
        } else notLearnedList.shuffled().take(NUMBER_OF_ANSWER_OPTIONS)
        val correctAnswer = questionWords.random()
        question = Question(variants = questionWords, correctAnswer = correctAnswer)

        return question
    }

    fun checkAnswer(userAnswerId: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)
            if (correctAnswerId == userAnswerId) {
                it.correctAnswer.correctAnswerCount++
                saveDictionary(dictionary)
                true
            } else false
        } ?: false
    }
}

