package ru.myapp.reminderbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class ReminderbotApplication

fun main(args: Array<String>) {
	runApplication<ReminderbotApplication>(*args)
}
