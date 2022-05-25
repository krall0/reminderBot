package ru.myapp.reminderbot.service.handler

import org.springframework.stereotype.Service
import ru.myapp.reminderbot.enums.ReminderType
import java.time.Instant

@Service
class ReminderOnceHandler : ReminderHandler {

    override fun canHandle(type: ReminderType): Boolean =
        type == ReminderType.ONCE

    override fun getErrorResponse(): String =
        "Чтобы создать разовое напоминание отправь\n/once time text" +
                "\n\ntime - дата/время напоминания в формате dd.mm.yyyy hh:mm" +
                "\n\ntext - текст напоминания"

    override fun needDeleteReminderAfterExecute(): Boolean = true

    override fun calculateNextDispatchTime(currentDispatchTime: Instant): Instant =
        currentDispatchTime
}
