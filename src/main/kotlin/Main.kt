package org.example

import java.io.File

const val NUMBER_OF_CORRECT_ANSWERS_FOR_REMEMBER = 3
const val CONST_FOR_PERCENT_CALCULATING = 100
fun main() {
    val dictionary = loadDictionary()

    while (true) {
        println("1 - Учить слова")
        println("2 - Статистика")
        println("0 - Выход")
        when (readln().toIntOrNull()) {
            1 -> {
                println("\nВыбран пункт \"Учить слова\".")
                val notLearnedList =
                    dictionary.filter { it.correctAnswerCount < NUMBER_OF_CORRECT_ANSWERS_FOR_REMEMBER }
                if (notLearnedList.isEmpty()) {
                    println("Все слова выучены.\n")
                    continue
                } else {
                    val questionWords = notLearnedList.take(4)
                    questionWords.shuffled()
                    val correctAnswer = questionWords.random().original
                    println(correctAnswer)
                    questionWords.forEachIndexed { index, word -> println("${index + 1}. ${word.translate}") }
                    print("Ваш ответ: ")
                    readln()
                }
            }

            2 -> {
                println("Ваша статистика:")
                val learnedCount = dictionary.filter { it.correctAnswerCount >= NUMBER_OF_CORRECT_ANSWERS_FOR_REMEMBER }
                val learnedPercent =
                    (learnedCount.size.toDouble() / dictionary.size * CONST_FOR_PERCENT_CALCULATING).toInt()
                println("Выучено ${learnedCount.size} из ${dictionary.size} слов | $learnedPercent%\n")
            }

            0 -> {
                println("Приложение остановлено.")
                return
            }

            else -> println("Введите число 1, 2 или 0")
        }
    }
}

data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int = 0,
)

fun loadDictionary(): MutableList<Word> {
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