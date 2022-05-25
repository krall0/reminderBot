package ru.myapp.reminderbot.bot

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import ru.myapp.reminderbot.enums.ReminderType
import ru.myapp.reminderbot.enums.SERVICE_TZ
import ru.myapp.reminderbot.service.ScheduleReminderService

@Component
class ReminderDemoBot(
    @Value("\${telegram.bot-name}") private val botName: String,
    @Value("\${telegram.token}") private val token: String,
    private val scheduleReminderService: ScheduleReminderService
) : TelegramLongPollingBot() {

    private val log = KotlinLogging.logger { }

    override fun getBotUsername(): String = botName

    override fun getBotToken(): String = token

    override fun onUpdateReceived(update: Update) {
        log.info("Receive: $update")
        if (update.hasMessage()) {
            val message = update.message
            if (message.entities.any { it.type == "bot_command" }) {
                handleCommand(message)
            }
        }
    }

    fun sendMessage(chatId: String, messageText: String): Boolean {
        return try {
            execute(SendMessage(chatId, messageText))
            true
        } catch (e: Throwable) {
            log.error("Error while sending reminder: ${e.message}", e)
            false
        }
    }

    private fun handleCommand(message: Message) {
        val command = message.entities.first { it.type == "bot_command" }
        val chatId = message.chatId.toString()
        when (command.text) {
            "/start" -> "Привет!\nЯ бот для создания напоминаний." +
                    "\nЯ работаю в часовом поясе $SERVICE_TZ"
            "/once", "/daily", "/monthly" -> scheduleReminderService.schedule(
                toReminderType(command.text),
                message.text.substring(command.length),
                chatId
            )
            "/list" -> scheduleReminderService.getReminders(chatId)
            "/delete" -> scheduleReminderService.deleteReminder(message.text.substring(command.length), chatId)
            else -> null
        }?.let { sendMessage(chatId, it) }
    }

    private fun toReminderType(command: String): ReminderType =
        when (command) {
            "/once" -> ReminderType.ONCE
            "/daily" -> ReminderType.DAILY
            "/monthly" -> ReminderType.MONTHLY
            else -> throw RuntimeException("Unknown command")
        }
}
