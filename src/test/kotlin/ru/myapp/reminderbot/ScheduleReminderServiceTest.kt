package ru.myapp.reminderbot

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.myapp.reminderbot.db.entity.ReminderEntity
import ru.myapp.reminderbot.db.repository.ReminderRepository
import ru.myapp.reminderbot.enums.ReminderType
import ru.myapp.reminderbot.enums.SERVICE_TZ
import ru.myapp.reminderbot.scheduled.ReminderSender
import ru.myapp.reminderbot.service.ScheduleReminderService
import java.time.ZoneId
import java.time.ZonedDateTime

@SpringBootTest
class ScheduleReminderServiceTest(@Autowired private val reminderRepository: ReminderRepository) {

    private val reminderDate = ZonedDateTime.of(
        2022, 5, 25, 12, 0, 0, 0, ZoneId.of(SERVICE_TZ)
        ).toInstant()
    private val reminderText = "text"
    private val reminderChatId = "1"

    @SpykBean
    private lateinit var scheduleReminderService: ScheduleReminderService

    @MockkBean
    private lateinit var reminderSender: ReminderSender

    @BeforeEach
    fun initMocks() {
        every { reminderSender.executeReminders() } returns Unit
    }

    @ParameterizedTest
    @EnumSource(ReminderType::class)
    fun `schedule reminder and delete`(reminderType: ReminderType) {

        val reminderId = testScheduleReminder(reminderType, " 25.05.2022 12:00 $reminderText")

        testDeleteReminder(reminderId)
    }

    private fun testScheduleReminder(reminderType: ReminderType, message: String): Long {
        scheduleReminderService.schedule(reminderType, message, reminderChatId)

        val scheduled = reminderRepository.findAllByChatId(reminderChatId).first()

        val expected = ReminderEntity().apply {
            this.chatId = reminderChatId
            this.message = reminderText
            this.type = reminderType
            this.dispatchTime = reminderDate
        }

        assertThat(scheduled).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expected)
        return scheduled.id!!
    }

    private fun testDeleteReminder(reminderId: Long) {
        scheduleReminderService.deleteReminder(" $reminderId", reminderChatId)

        assertThat(reminderRepository.findAllByChatId(reminderChatId)).isEmpty()
    }
}
