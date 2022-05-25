package ru.myapp.reminderbot.service.handler

import org.springframework.stereotype.Service
import ru.myapp.reminderbot.enums.ReminderType
import ru.myapp.reminderbot.enums.SERVICE_TZ
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Service
class ReminderMonthlyHandler : ReminderHandler {

    override fun canHandle(type: ReminderType): Boolean =
        type == ReminderType.MONTHLY

    override fun getErrorResponse(): String =
        "Чтобы создать ежемесячное напоминание отправь\n/monthly time text" +
                "\n\ntime - дата/время первого напоминания в формате dd.mm.yyyy hh:mm" +
                "\n\ntext - текст напоминания"

    override fun needDeleteReminderAfterExecute(): Boolean = false

    override fun calculateNextDispatchTime(currentDispatchTime: Instant): Instant =
        ZonedDateTime.ofInstant(currentDispatchTime, ZoneId.of(SERVICE_TZ))
            .plus(1, ChronoUnit.MONTHS)
            .toInstant()
}
