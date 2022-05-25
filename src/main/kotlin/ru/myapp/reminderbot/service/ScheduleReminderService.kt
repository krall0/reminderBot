package ru.myapp.reminderbot.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import ru.myapp.reminderbot.db.entity.ReminderEntity
import ru.myapp.reminderbot.db.repository.ReminderRepository
import ru.myapp.reminderbot.enums.ReminderType
import ru.myapp.reminderbot.enums.SERVICE_TZ
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class ScheduleReminderService(
    private val reminderHandlerFactory: ReminderHandlerFactory,
    private val reminderRepository: ReminderRepository
) {

    private val log = KotlinLogging.logger { }

    fun schedule(reminderType: ReminderType, messageText: String, chatId: String): String {
        log.info("Schedule reminder with type=$reminderType")
        return try {
            val params = messageText.split(" ", limit = 4)
            if (params.size < 4) {
                throw RuntimeException("invalid message format")
            }
            val reminderEntity = ReminderEntity().apply {
                this.type = reminderType
                this.message = params[3]
                this.dispatchTime = toInstant(params[1] + params[2])
                this.chatId = chatId
            }
            val saved = reminderRepository.save(reminderEntity)
            getHandler(reminderType).getScheduledResponse(saved)
        } catch (e: Throwable) {
            log.error("Error while scheduling reminder: ${e.message}", e)
            getHandler(reminderType).getErrorResponse()
        }
    }

    fun getReminders(chatId: String): String {
        log.info("Get reminders with chatId=$chatId")
        val reminders = reminderRepository.findAllByChatId(chatId)
        return if (reminders.isEmpty()) {
            "Напоминаний нет"
        } else {
            reminders.joinToString("\n\n")
        }
    }

    fun deleteReminder(messageText: String, chatId: String): String {
        return try {
            val params = messageText.split(" ")
            if (params.size != 2) {
                throw RuntimeException("invalid message format")
            }
            val reminderId = params[1].toLong()
            reminderRepository.findByIdAndChatId(reminderId, chatId)?.let {
                reminderRepository.deleteById(reminderId)
                "Напоминание $reminderId удалено"
            } ?: "Напоминание не найдено"
        } catch (e: Throwable) {
            log.error("Error while deleting reminder: ${e.message}", e)
            "Чтобы удалить напоминание отправь\n/delete id" +
                    "\n\nid - номер напоминания"
        }
    }

    private fun toInstant(data: String): Instant =
        LocalDateTime.parse(
            data, DateTimeFormatter.ofPattern("dd.MM.yyyyHH:mm")
        ).atZone(ZoneId.of(SERVICE_TZ)).toInstant()

    private fun getHandler(type: ReminderType) =
        reminderHandlerFactory.getHandler(type)
}
