package org.example

import java.io.File

const val NUMBER_OF_CORRECT_ANSWERS_FOR_REMEMBER = 3
const val CONST_FOR_PERCENT_CALCULATING = 100
const val NUMBER_OF_ANSWER_OPTIONS = 4
const val NUMBER_FOR_EXIT = 0
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
                    val questionWords = notLearnedList.shuffled().take(NUMBER_OF_ANSWER_OPTIONS)
                    val correctAnswer = questionWords.random()
                    val correctAnswerId = questionWords.indexOf(correctAnswer) + 1
                    var userAnswerInput: Int?
                    var isInputValid: Boolean

                    do {
                        println(correctAnswer.original)
                        questionWords.forEachIndexed { index, word -> println("${index + 1}. ${word.translate}") }
                        val maxLength = questionWords.maxOf { it.translate.length }
                        println("${"-".repeat(maxLength + 3)}\n$NUMBER_FOR_EXIT. Меню")
                        print("Ваш ответ: ")
                        userAnswerInput = readln().toIntOrNull()
                        isInputValid = userAnswerInput != null && userAnswerInput in (1..4)
                        if (!isInputValid) {
                            println("Неверный ввод. Введите число от 1 до 4 либо 0 для выхода в меню.\n")
                        }
                    } while (!isInputValid)

                    if (userAnswerInput == 0) continue
                    if (userAnswerInput == correctAnswerId) {
                        println("Правильно!")
                        correctAnswer.correctAnswerCount++
                        saveDictionary(dictionary)
                    } else println("Не правильно! ${correctAnswer.original} - это ${correctAnswer.translate}")
                }
            }

            2 -> {
                println("Ваша статистика:")
                val learnedCount =
                    dictionary.filter { it.correctAnswerCount >= NUMBER_OF_CORRECT_ANSWERS_FOR_REMEMBER }
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
) {
    override fun toString(): String {
        return "$original|$translate|$correctAnswerCount"
    }
}

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

fun saveDictionary(dictionary: MutableList<Word>) {
    val wordsFile = File("words.txt")
    val listOfWords: MutableList<String> = mutableListOf()

    dictionary.forEach { listOfWords.add(it.toString()) }
    wordsFile.writeText(listOfWords.joinToString("\n"))
}