package ru.myapp.reminderbot.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import ru.myapp.reminderbot.bot.ReminderDemoBot
import ru.myapp.reminderbot.db.entity.ReminderEntity
import ru.myapp.reminderbot.db.repository.ReminderRepository
import ru.myapp.reminderbot.enums.ReminderType
import java.time.Instant

@Service
class ExecuteReminderService(
    @Value("\${application.reminders-batch-size}") private val batchSize: Int,
    private val reminderRepository: ReminderRepository,
    private val reminderDemoBot: ReminderDemoBot,
    private val reminderHandlerFactory: ReminderHandlerFactory
) {

    fun getRemindersForSend(): List<ReminderEntity> =
        reminderRepository.findByDispatchTimeLessThanEqual(Instant.now(), Pageable.ofSize(batchSize))

    fun executeReminder(reminder: ReminderEntity) {
        if (reminderDemoBot.sendMessage(reminder.chatId, reminder.message)) {
            val handler = getHandler(reminder.type)
            if (handler.needDeleteReminderAfterExecute()) {
                reminderRepository.deleteById(reminder.id!!)
            } else {
                reminder.dispatchTime = handler.calculateNextDispatchTime(reminder.dispatchTime)
            }
        }
    }

    private fun getHandler(type: ReminderType) =
        reminderHandlerFactory.getHandler(type)
}
