package ee.tenman.crawler.queue

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ee.tenman.crawler.config.rabbitmq.RabbitMQConstants
import ee.tenman.crawler.domain.TransformedData
import ee.tenman.crawler.extractor.KeywordCountWithSite
import ee.tenman.crawler.extractor.KeywordExtractorService
import ee.tenman.crawler.service.DataService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class Consumer(
    private val keywordExtractorService: KeywordExtractorService,
    private val objectMapper: ObjectMapper,
    private val dataService: DataService
) {
    private val log: Logger = LogManager.getLogger(Consumer::class.java)

    @RabbitListener(queues = [RabbitMQConstants.WEBSITES])
    fun listen(message: String) {
        val parsedMessage = objectMapper.readValue<Message>(message)
        log.info("Received message: ${parsedMessage.url}")

        val extractKeywordData: List<KeywordCountWithSite> =
            keywordExtractorService.extractKeywordData(parsedMessage.body, parsedMessage.url)

        log.info("Extracted Keywords: $extractKeywordData")

        val transformedDataList = extractKeywordData.map { keywordCount ->
            TransformedData(
                term = keywordCount.term,
                incidence = keywordCount.incidence,
                site = keywordCount.site,
                timestamp = LocalDateTime.now()
            )
        }
        dataService.saveAllData(transformedDataList)
        
        log.info("Finished processing message: ${parsedMessage.url}")
    }
}