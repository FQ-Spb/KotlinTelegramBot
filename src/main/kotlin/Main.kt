package org.example

const val NUMBER_OF_CORRECT_ANSWERS_FOR_REMEMBER = 3
const val CONST_FOR_PERCENT_CALCULATING = 100
const val NUMBER_OF_ANSWER_OPTIONS = 4
const val NUMBER_FOR_EXIT = 0

data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int = 0,
) {
    override fun toString(): String {
        return "$original|$translate|$correctAnswerCount"
    }
}

fun Question.asConsoleString() {
    println(this.correctAnswer.original)
    this.variants.forEachIndexed { index, word -> println("${index + 1}. ${word.translate}") }
    val maxLength = this.variants.maxOf { it.translate.length }
    println("${"-".repeat(maxLength)}\n$NUMBER_FOR_EXIT. Меню\nВаш ответ: ")
}

fun main() {
    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Файл со словами пуст или некорректен")
        return
    }

    while (true) {
        println("1 - Учить слова\n2 - Статистика\n0 - Выход")
        when (readln().toIntOrNull()) {
            1 -> {
                println("\nВыбран пункт \"Учить слова\".")
                val question = trainer.getNextQuestion()
                if (question == null) {
                    println("Все слова выучены.\n")
                    continue
                } else {
                    var userAnswerInput: Int?
                    var isInputValid: Boolean
                    do {
                        question.asConsoleString()
                        userAnswerInput = readln().toIntOrNull()
                        isInputValid = userAnswerInput != null && userAnswerInput in (1..4)

                        if (!isInputValid) {
                            println("Неверный ввод. Введите число от 1 до 4 либо 0 для выхода в меню.\n")
                        }
                    } while (!isInputValid)

                    if (userAnswerInput == 0) continue
                    if (trainer.checkAnswer(userAnswerInput?.minus(1))) {
                        println("Правильно!")
                    } else println("Не правильно! ${question.correctAnswer.original} - это ${question.correctAnswer.translate}")
                }
            }

            2 -> {
                println("Ваша статистика:")
                val statistics = trainer.getStatistic()
                println("Выучено ${statistics.learnedCount} из ${statistics.total} слов | ${statistics.learnedPercent}%\n")
            }

            0 -> {
                println("Приложение остановлено.")
                return
            }

            else -> println("Введите число 1, 2 или 0")
        }
    }
}



