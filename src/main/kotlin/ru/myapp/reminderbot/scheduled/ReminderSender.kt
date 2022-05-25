package ru.myapp.reminderbot.scheduled

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.myapp.reminderbot.service.ExecuteReminderService

@Component
class ReminderSender(
    private val executeReminderService: ExecuteReminderService
) {

    @Scheduled(fixedRate = 60000)
    @Transactional
    fun executeReminders() {
        executeReminderService.getRemindersForSend()
            .forEach {executeReminderService.executeReminder(it)}
    }
}
