package ee.tenman.crawler.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "openai")
class OpenAiProperties {
    var enabled: Boolean = false
    var token: String = ""
}