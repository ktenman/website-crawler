package ee.tenman.crawler.openai;

import ee.tenman.crawler.config.OpenAiProperties;
import feign.RequestInterceptor;
import jakarta.annotation.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.TimeUnit;

@FeignClient(name = OpenAiClient.CLIENT_NAME, url = OpenAiClient.CLIENT_URL, configuration = OpenAiClient.Configuration.class)
public interface OpenAiClient {

	String CLIENT_NAME = "openAiClient";
	String CLIENT_URL = "https://api.openai.com";

	@PostMapping("/v1/chat/completions")
	OpenAiResponse createCompletion(@RequestBody OpenAiRequest request);

	class Configuration {
		private final OpenAiRateLimiter rateLimiter = new OpenAiRateLimiter();

		@Resource
		private OpenAiProperties openAiProperties;

		@Bean
		public RequestInterceptor requestInterceptor() {
			return template -> this.rateLimiter.check(() -> {
				template.header("Authorization", "Bearer " + this.openAiProperties.getToken());
				try {
					TimeUnit.SECONDS.sleep(15);
				} catch (final InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			});
		}
	}

}
