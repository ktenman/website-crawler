package ee.tenman.crawler.config

import com.google.gson.Gson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GsonConfig {
    
    @Bean
    fun gson() = Gson()
    
}