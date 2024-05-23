package ee.tenman.crawler.extractor

data class KeywordCountWithSite(
    val term: String,
    val incidence: Int,
    val site: String,
    val timestamp: String
)