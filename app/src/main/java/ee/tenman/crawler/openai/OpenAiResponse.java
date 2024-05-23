package ee.tenman.crawler.openai;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OpenAiResponse {
	private static final String CONTENT = "content";
	private static final String MESSAGE = "message";

	private List<Map<String, Object>> choices;

	public OpenAiResponse() {
	}

	public OpenAiResponse(final List<Map<String, Object>> choices) {
		this.choices = choices;
	}

	public List<Map<String, Object>> getChoices() {
		return this.choices;
	}

	public void setChoices(final List<Map<String, Object>> choices) {
		this.choices = choices;
	}

	public Optional<String> getAnswer() {
		if (this.choices == null || this.choices.isEmpty()) {
			return Optional.empty();
		}

		final Object messageObject = this.choices.get(0).get(MESSAGE);
		if (!(messageObject instanceof final Map<?, ?> messageMap)) {
			return Optional.empty();
		}

		final Object contentObject = messageMap.get(CONTENT);
		if (!(contentObject instanceof final String content)) {
			return Optional.empty();
		}

		return Optional.of(content);
	}

	@Override
	public String toString() {
		return "OpenAiResponse{" + "choices=" + this.choices + '}';
	}
}
