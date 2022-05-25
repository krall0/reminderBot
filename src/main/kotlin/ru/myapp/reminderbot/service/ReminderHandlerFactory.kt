package ru.myapp.reminderbot.service

import org.springframework.stereotype.Service
import ru.myapp.reminderbot.enums.ReminderType
import ru.myapp.reminderbot.service.handler.ReminderHandler

@Service
class ReminderHandlerFactory(private val handlers: List<ReminderHandler>) {

    fun getHandler(type: ReminderType): ReminderHandler =
        handlers.first { it.canHandle(type) }
}
