package ru.myapp.reminderbot.db.entity

import org.hibernate.Hibernate
import ru.myapp.reminderbot.enums.ReminderType
import ru.myapp.reminderbot.enums.SERVICE_TZ
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.persistence.*

@Entity
@Table(name = "reminder")
class ReminderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    lateinit var type: ReminderType

    @Column(name = "message", nullable = false)
    lateinit var message: String

    @Column(name = "chat_id", nullable = false)
    lateinit var chatId: String

    @Column(name = "dispatch_time", nullable = false)
    lateinit var dispatchTime: Instant

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ReminderEntity

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        val typeString = when (type) {
            ReminderType.ONCE -> "Разовое"
            ReminderType.DAILY -> "Ежедневное"
            ReminderType.MONTHLY -> "Ежемесячное"
        }
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                .withZone(ZoneId.of(SERVICE_TZ))

        return "$id. $typeString напоминание, время отправки - ${formatter.format(dispatchTime)}, текст - \"$message\""
    }
}
