package ee.tenman.crawler.config.rabbitmq

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    private val log: Logger = LoggerFactory.getLogger(RabbitMQConfig::class.java)

    @Bean
    fun rabbitAdmin(connectionFactory: ConnectionFactory): RabbitAdmin {
        return RabbitAdmin(connectionFactory)
    }

    @Bean
    fun webSites(rabbitAdmin: RabbitAdmin): Queue {
        return declareQueueWithExistingDurability(
            rabbitAdmin,
            RabbitMQConstants.WEBSITES
        )
    }

    @Bean
    fun crawlerTasksQueue(rabbitAdmin: RabbitAdmin): Queue {
        return declareQueueWithExistingDurability(
            rabbitAdmin,
            RabbitMQConstants.CRAWLER_TASKS_QUEUE
        )
    }

    private fun declareQueueWithExistingDurability(
        rabbitAdmin: RabbitAdmin,
        queueName: String
    ): Queue {
        return try {
            val queueProperties = rabbitAdmin.getQueueProperties(queueName)
            val durable = queueProperties["durable"] as? Boolean ?: false
            val queueBuilder = if (durable) {
                QueueBuilder.durable(queueName)
            } else {
                QueueBuilder.nonDurable(queueName)
            }
            val queue = queueBuilder.build()
            rabbitAdmin.declareQueue(queue)
            queue
        } catch (e: Exception) {
            log.warn("Failed to declare queue '$queueName' with existing durability. Falling back to non-durable.")
            val fallbackQueue = QueueBuilder.nonDurable(queueName).build()
            rabbitAdmin.declareQueue(fallbackQueue)
            fallbackQueue
        }
    }
    
}