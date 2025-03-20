package org.example

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val LEARN_WORDS_BUTTON = "clicked_button_Learn_Words"
const val STATISTICS_BUTTON = "clicked_button_Statistics"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

class TelegramBotService(
    val apiKey: String,
) {
    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "https://api.telegram.org/bot$apiKey/getUpdates?offset=$updateId"
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(chatId: Long, message: String): String {
        var textToSend = message
        var partOfMessage: String
        var response: String
        val responses = mutableListOf<String>()

        if (textToSend.isEmpty()) return "Отправка невозможна.Недопустимая длина сообщения."
        while (textToSend.isNotEmpty()) {
            partOfMessage = textToSend.take(MAX_LENGTH_OF_MESSAGE)
            response = sendMessagePart(chatId = chatId, message = partOfMessage)
            responses.add(response)
            textToSend = textToSend.drop(MAX_LENGTH_OF_MESSAGE)
        }
        return responses.joinToString("\n")
    }

    private fun sendMessagePart(message: String, chatId: Long): String {
        val encodedText = URLEncoder.encode(message, "UTF-8")
        val urlSendMessage =
            "$API_URL$apiKey/sendMessage?chat_id=$chatId&text=$encodedText"
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendMenu(chatId: Long): String {
        val urlSendMessage = "$API_URL$apiKey/sendMessage"
        val menuJson = """
            {
            "chat_id":$chatId,
            "text":"Основное меню",
            "reply_markup":{
                   "inline_keyboard": [
                        [
                            {
                                "text":"Изучить слова",
                                "callback_data":"$LEARN_WORDS_BUTTON"
                            },
                            {
                                "text":"Статистика",
                                "callback_data":"$STATISTICS_BUTTON"
                            }
                        ]
                    ]
                }
            }
        """.trimIndent()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(menuJson))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    fun sendQuestion(chatId: Long, question: Question): String {
        val variantsString = question.variants.mapIndexed { index, word ->
            """
            {
            "text":"${word.translate}",
            "callback_data":"${CALLBACK_DATA_ANSWER_PREFIX + index}"
            }
        """.trimIndent()
        }

        val url = "$API_URL$apiKey/sendMessage"
        val questionJson = """
        {
            "chat_id":"$chatId",
            "text":"${question.correctAnswer.original}",
            "reply_markup":{
                "inline_keyboard":[
                    [
                        ${variantsString.take(2).joinToString(",")}
                    ],
                    [
                       ${variantsString.drop(2).take(2).joinToString(",")}
                    ]
                ]
            }
        }
        """.trimIndent()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(questionJson))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}

