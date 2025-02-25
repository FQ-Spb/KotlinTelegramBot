package org.example

import java.io.File


fun main() {
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
    dictionary.forEach { println(it) }
}

data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int? = 0,
)