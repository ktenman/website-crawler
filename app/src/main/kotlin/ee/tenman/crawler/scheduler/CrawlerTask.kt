package ee.tenman.crawler.scheduler

data class CrawlerTask(
    val timestamp: String,
    val originator: String,
    val websiteUrl: String
)