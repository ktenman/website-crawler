package ee.tenman.crawler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableFeignClients
@EnableRetry
@EnableScheduling
@ConfigurationPropertiesScan
class CrawlerApplication

fun main(args: Array<String>) {
	runApplication<CrawlerApplication>(*args)
}
