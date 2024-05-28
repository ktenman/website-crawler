package ee.tenman.crawler.scheduler

import com.fasterxml.jackson.databind.ObjectMapper
import ee.tenman.crawler.config.rabbitmq.RabbitMQConstants
import jakarta.annotation.PostConstruct
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CrawlerTaskScheduler(
    private val rabbitTemplate: RabbitTemplate,
    private val objectMapper: ObjectMapper) {

    private val log: Logger = LogManager.getLogger(CrawlerTaskScheduler::class.java)

    @Scheduled(initialDelay = 10_000)
    fun scheduleInitialTasks() {
        scheduleCrawlerTasks()
    }

    @Scheduled(cron = "0 0 0/2 * * MON-THU,SAT,SUN") // Run every 2 hours on Monday to Thursday, Saturday, and Sunday
    fun scheduleWeekdayTasks() {
        scheduleCrawlerTasks()
    }

    @Scheduled(cron = "0 0 * * * FRI") // Run every hour on Friday
    fun scheduleFridayTasks() {
        scheduleCrawlerTasks()
    }

    private fun scheduleCrawlerTasks() {
        val websiteUrls = listOf("https://www.ft.com", "https://www.theguardian.com/europe")

        websiteUrls.forEach { url ->
            val task = CrawlerTask(
                timestamp = LocalDateTime.now().toString(),
                originator = "Kotlin App",
                websiteUrl = url
            )
            val taskJson = objectMapper.writeValueAsString(task)
            log.info("Sending crawler task to queue ${RabbitMQConstants.CRAWLER_TASKS_QUEUE} with message: $taskJson")
            rabbitTemplate.run { convertAndSend(RabbitMQConstants.CRAWLER_TASKS_QUEUE, taskJson) }
            log.info("Sent crawler task to queue ${RabbitMQConstants.CRAWLER_TASKS_QUEUE} with message: $taskJson")
        }
    }
}
