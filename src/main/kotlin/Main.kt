package org.example

import java.io.File


fun main() {
    val dictionary = loadDictionary()

    while (true) {
        println("1 - Учить слова")
        println("2 - Статистика")
        println("0 - Выход")
        when (readln().toIntOrNull()) {
            1 -> println("Выбран пункт \"Учить слова\".")
            2 -> println("Выбран пункт \"Статистика\"")
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