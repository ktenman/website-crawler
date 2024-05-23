package ee.tenman.crawler.extractor

interface KeywordExtractor {
    fun extractKeywordData(htmlCode: String, siteName: String): List<KeywordCountWithSite>
}