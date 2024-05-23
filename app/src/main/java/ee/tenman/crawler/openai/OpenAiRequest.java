package ee.tenman.crawler.openai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class OpenAiRequest {
	private static final String DEFAULT_MODEL = "gpt-4";

	private static final String ROLE = "role";
	private static final String USER = "user";
	private static final String CONTENT = "content";

	private final String model;
	private final List<Map<String, Object>> messages;
	@JsonProperty("max_tokens")
	private int maxTokens = 300;

	private OpenAiRequest(final String model, final List<Map<String, Object>> messages) {
		this.model = model;
		this.messages = messages;
	}

	public String getModel() {
		return this.model;
	}

	public static OpenAiRequest createWithUserMessage(final String messageContent) {
		final Map<String, Object> userMessage = new HashMap<>();
		userMessage.put(ROLE, USER);
		userMessage.put(CONTENT, messageContent);
		return new OpenAiRequest(Model.GPT_4_O.getValue(), Collections.singletonList(userMessage));
	}

	public static OpenAiRequest createWithMessageAndImage(final String messageContent, final byte[] imageBytes) {
		final List<Map<String, Object>> messages = new ArrayList<>();

		final Map<String, String> text = Map.of("type", "text", "text", messageContent);

		final Map<String, Object> image = Map.of("type", "image_url", "image_url",
				Map.of("url", "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes)));

		messages.add(Map.of(ROLE, USER, CONTENT, List.of(text, image)));

		return new OpenAiRequest(Model.GPT_4_VISION_PREVIEW.getValue(), messages);
	}

	public List<Map<String, Object>> getMessages() {
		return this.messages;
	}

	@Override
	public String toString() {
		return "OpenAiRequest{" + "model=" + this.model + ", messages=" + this.messages + ", maxTokens="
				+ this.maxTokens + '}';
	}

	public enum Model {
		GPT_4(DEFAULT_MODEL), GPT_4_VISION_PREVIEW("gpt-4-vision-preview"), GPT_4_O("gpt-4o");

		private final String value;

		Model(final String value) {
			this.value = value;
		}

		public String getValue() {
			return this.value;
		}
	}
}
