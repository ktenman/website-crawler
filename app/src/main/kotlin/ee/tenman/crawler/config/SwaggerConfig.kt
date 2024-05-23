package ee.tenman.crawler.config

import io.swagger.v3.oas.models.OpenAPI
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun api(): OpenAPI {
        return OpenAPI()
            .info(
                io.swagger.v3.oas.models.info.Info()
                    .title("Crawler API")
                    .description("Crawler API with Spring Boot and Kotlin")
                    .version("1.0")
            )
        
    }

}