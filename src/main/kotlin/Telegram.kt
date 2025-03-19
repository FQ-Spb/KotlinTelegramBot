package org.example

const val MAX_LENGTH_OF_MESSAGE = 4096
const val API_URL = "https://api.telegram.org/bot"

fun main(args: Array<String>) {

    val botToken = args[0]
    val englishLearnBot = TelegramBotService(apiKey = botToken)
    var updateId = 0
    val updateIdRegex = "\"update_id\":(.+?),".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex = "\"chat\":\\{\"id\":(.+?),".toRegex()
    val dataRegex = "\"data\":\"(.+?)\"".toRegex()
    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(2000)
        val updates: String = englishLearnBot.getUpdates(updateId)
        println(updates)
        val matchChatIdRegex: MatchResult? = chatIdRegex.find(updates)
        val groupsChatId: MatchGroupCollection? = matchChatIdRegex?.groups
        val chatId: Long = groupsChatId?.get(1)?.value?.toLong() ?: 0

        val matchIdRegex: MatchResult? = updateIdRegex.find(updates)
        val groupsId = matchIdRegex?.groups
        updateId = groupsId?.get(1)?.value?.toIntOrNull()?.plus(1) ?: continue

        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value

        val matchDataRegex = dataRegex.find(updates)
        val groupsData = matchDataRegex?.groups
        val data = groupsData?.get(1)?.value

        if (text.equals("hello", ignoreCase = true)) englishLearnBot.sendMessage(
            chatId,
            "Hello"
        )
        if (text.equals("/start", ignoreCase = true)) englishLearnBot.sendMenu(chatId)

        if (data == STATISTICS_BUTTON) englishLearnBot.sendMessage(chatId, trainer.getStatistic())

        if (data == LEARN_WORDS_BUTTON) englishLearnBot.checkNextQuestionAndSend(chatId = chatId,trainer = trainer)
    }
}



