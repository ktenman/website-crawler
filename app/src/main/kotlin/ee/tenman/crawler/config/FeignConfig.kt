package ee.tenman.crawler.config

import com.fasterxml.jackson.databind.ObjectMapper
import feign.codec.Decoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfig(private val objectMapper: ObjectMapper) {

    @Bean
    fun feignDecoder(): Decoder {
        return Decoder { response, type ->
            requireNotNull(response.body()) { "Response body is null" }
            val responseBody = response.body().asReader().readText()
            objectMapper.readValue(responseBody, objectMapper.typeFactory.constructType(type))
        }
    }
}