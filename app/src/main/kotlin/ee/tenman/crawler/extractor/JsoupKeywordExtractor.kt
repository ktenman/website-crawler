package ee.tenman.crawler.extractor

import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class JsoupKeywordExtractor : KeywordExtractor {
    override fun extractKeywordData(htmlCode: String, siteName: String): List<KeywordCountWithSite> {
        val keywords = listOf("election", "war", "economy")
        val document = Jsoup.parse(htmlCode)
        val headingsAndSubheadings = document.select("h1, h2, h3, h4, h5, h6")
        val keywordCounts = keywords.associateWith { keyword ->
            headingsAndSubheadings.text().split(" ").count { it.equals(keyword, ignoreCase = true) }
        }
        return keywordCounts
            .map { (keyword, count) -> KeywordCountWithSite(keyword, count, siteName, Instant.now().toString()) }
            .toList()
    }
}