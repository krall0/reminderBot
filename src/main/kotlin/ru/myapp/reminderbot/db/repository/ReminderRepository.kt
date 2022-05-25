package ru.myapp.reminderbot.db.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import ru.myapp.reminderbot.db.entity.ReminderEntity
import java.time.Instant
import javax.persistence.LockModeType

interface ReminderRepository : JpaRepository<ReminderEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByDispatchTimeLessThanEqual(dispatchTime: Instant, pageable: Pageable): List<ReminderEntity>

    fun findAllByChatId(chatId: String): List<ReminderEntity>

    fun findByIdAndChatId(id: Long, chatId: String): ReminderEntity?
}
