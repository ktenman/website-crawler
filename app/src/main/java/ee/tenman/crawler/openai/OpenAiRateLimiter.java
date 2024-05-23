package ee.tenman.crawler.openai;

import java.time.Clock;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OpenAiRateLimiter {
	private static final int MAX_REQUESTS_PER_MINUTE = 2;
	private final int maxRequests;
	private final long timePeriodInMillis;
	private final Clock clock;
	private long startTime;
	private int requestCount;
	private final ScheduledExecutorService scheduler;

	public OpenAiRateLimiter() {
		this(MAX_REQUESTS_PER_MINUTE, 60_000, Clock.systemUTC());
	}

	public OpenAiRateLimiter(final int maxRequests, final long timePeriodInMillis, final Clock clock) {
		this.maxRequests = maxRequests;
		this.timePeriodInMillis = timePeriodInMillis;
		this.clock = clock;
		this.startTime = clock.millis();
		this.scheduler = Executors.newScheduledThreadPool(1);
	}

	public synchronized void check(final Runnable task) {
		final long currentTime = this.clock.millis();
		if (currentTime > this.startTime + this.timePeriodInMillis) {
			this.startTime = currentTime;
			this.requestCount = 0;
		}
		if (this.requestCount < this.maxRequests) {
			this.requestCount++;
			task.run();
		} else {
			this.scheduler.schedule(task, 1, TimeUnit.SECONDS);
		}
	}
}