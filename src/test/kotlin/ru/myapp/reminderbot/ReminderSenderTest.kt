package ru.myapp.reminderbot

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.mockk.every
import io.mockk.verify
import org.awaitility.kotlin.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import ru.myapp.reminderbot.bot.ReminderDemoBot
import ru.myapp.reminderbot.db.entity.ReminderEntity
import ru.myapp.reminderbot.db.repository.ReminderRepository
import ru.myapp.reminderbot.enums.ReminderType
import ru.myapp.reminderbot.enums.SERVICE_TZ
import ru.myapp.reminderbot.scheduled.ReminderSender
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@SpringBootTest
class ReminderSenderTest(@Autowired private val reminderRepository: ReminderRepository) {

    private val reminderDate = ZonedDateTime.now(ZoneId.of(SERVICE_TZ)).toInstant()
    private val reminderText = "text"
    private val reminderChatId = "2"
    private val onceReminderId = 1L
    private val dailyReminderId = 2L
    private val monthlyReminderId = 3L

    @SpykBean
    private lateinit var reminderSender: ReminderSender

    @MockkBean
    private lateinit var reminderDemoBot: ReminderDemoBot

    @BeforeEach
    fun initRepository() {
        reminderRepository.save(ReminderEntity().apply {
            type = ReminderType.ONCE
            message = reminderText
            chatId = reminderChatId
            dispatchTime = reminderDate
        })
        reminderRepository.save(ReminderEntity().apply {
            type = ReminderType.DAILY
            message = reminderText
            chatId = reminderChatId
            dispatchTime = reminderDate
        })
        reminderRepository.save(ReminderEntity().apply {
            type = ReminderType.MONTHLY
            message = reminderText
            chatId = reminderChatId
            dispatchTime = reminderDate
        })
    }

    @Test
    fun `execute reminders`() {

        every { reminderDemoBot.sendMessage(any(), any()) } returns true

        reminderSender.executeReminders()

        waitForExecutedOnceReminder()
        waitForExecutedDailyReminder()
        waitForExecutedMonthlyReminder()

        verify(exactly = 3) { reminderDemoBot.sendMessage(any(), any()) }
    }

    private fun waitForExecutedOnceReminder() {
        await withAlias ("The once reminder must be delete after execute") untilNull {
            reminderRepository.findByIdOrNull(onceReminderId)
        }
    }

    private fun waitForExecutedDailyReminder() {
        await withAlias ("The daily reminder dispatchTime must be change") untilCallTo {
            reminderRepository.findByIdOrNull(dailyReminderId)
        } matches {
            it?.dispatchTime!!.truncatedTo(ChronoUnit.SECONDS) == ZonedDateTime.ofInstant(
                reminderDate,
                ZoneId.of(SERVICE_TZ)
            )
                .plus(1, ChronoUnit.DAYS)
                .toInstant().truncatedTo(ChronoUnit.SECONDS)
        }
    }

    private fun waitForExecutedMonthlyReminder() {
        await withAlias ("The monthly reminder dispatchTime must be change") untilCallTo {
            reminderRepository.findByIdOrNull(monthlyReminderId)
        } matches {
            it?.dispatchTime!!.truncatedTo(ChronoUnit.SECONDS) == ZonedDateTime.ofInstant(
                reminderDate,
                ZoneId.of(SERVICE_TZ)
            )
                .plus(1, ChronoUnit.MONTHS)
                .toInstant().truncatedTo(ChronoUnit.SECONDS)
        }
    }
}
