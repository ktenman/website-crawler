package ee.tenman.crawler.extractor

import ee.tenman.crawler.config.OpenAiProperties
import org.springframework.stereotype.Service

@Service
class KeywordExtractorService(
    openAiProperties: OpenAiProperties,
    openAiKeywordExtractor: OpenAiKeywordExtractor,
    jsoupKeywordExtractor: JsoupKeywordExtractor
) {
    private val keywordExtractor: KeywordExtractor = when {
        openAiProperties.enabled -> openAiKeywordExtractor
        else -> jsoupKeywordExtractor
    }

    fun extractKeywordData(htmlCode: String, siteName: String): List<KeywordCountWithSite> {
        return keywordExtractor.extractKeywordData(htmlCode, siteName)
    }
}