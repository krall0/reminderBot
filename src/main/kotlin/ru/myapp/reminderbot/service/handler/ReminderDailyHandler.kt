package ru.myapp.reminderbot.service.handler

import org.springframework.stereotype.Service
import ru.myapp.reminderbot.enums.ReminderType
import ru.myapp.reminderbot.enums.SERVICE_TZ
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Service
class ReminderDailyHandler : ReminderHandler {

    override fun canHandle(type: ReminderType): Boolean =
        type == ReminderType.DAILY

    override fun getErrorResponse(): String =
        "Чтобы создать ежедневное напоминание отправь\n/daily time text" +
                "\n\ntime - дата/время первого напоминания в формате dd.mm.yyyy hh:mm" +
                "\n\ntext - текст напоминания"

    override fun needDeleteReminderAfterExecute(): Boolean = false

    override fun calculateNextDispatchTime(currentDispatchTime: Instant): Instant =
        ZonedDateTime.ofInstant(currentDispatchTime, ZoneId.of(SERVICE_TZ))
            .plus(1, ChronoUnit.DAYS)
            .toInstant()
}
