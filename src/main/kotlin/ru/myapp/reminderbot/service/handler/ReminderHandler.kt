package ru.myapp.reminderbot.service.handler

import ru.myapp.reminderbot.db.entity.ReminderEntity
import ru.myapp.reminderbot.enums.ReminderType
import java.time.Instant

interface ReminderHandler {

    fun canHandle(type: ReminderType): Boolean

    fun getErrorResponse(): String

    fun getScheduledResponse(scheduledReminder: ReminderEntity): String =
        "Напоминание создано\n$scheduledReminder"

    fun needDeleteReminderAfterExecute(): Boolean

    fun calculateNextDispatchTime(currentDispatchTime: Instant): Instant
}
