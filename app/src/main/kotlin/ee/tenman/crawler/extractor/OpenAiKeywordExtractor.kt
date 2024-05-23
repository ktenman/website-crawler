package ee.tenman.crawler.extractor

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ee.tenman.crawler.openai.OpenAiClient
import ee.tenman.crawler.openai.OpenAiRequest
import ee.tenman.crawler.openai.OpenAiResponse
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class OpenAiKeywordExtractor(
    private val openAiClient: OpenAiClient,
    private val gson: Gson,
) : KeywordExtractor {

    private val log: Logger = LoggerFactory.getLogger(OpenAiKeywordExtractor::class.java)

    override fun extractKeywordData(htmlCode: String, siteName: String): List<KeywordCountWithSite> {
        val prompt = generatePrompt(htmlCode)
        val response = askQuestion(prompt)
        if (response.isEmpty) {
            log.debug("Answer '{}' was not recognized as correct by Dolphin Service.", prompt)
            throw RuntimeException("Dolphin Service returned no response")
        }

        val cleanResponse = cleanResponse(response.get())
        val keywordCounts = deserializeKeywordCounts(cleanResponse, gson)
        return keywordCounts.map { KeywordCountWithSite(it.keyword, it.count, siteName, Instant.now().toString()) }
    }

    private fun cleanResponse(response: String): String {
        return response
            .replace(Regex("""\`\`\`(json)?\n"""), "")
            .replace(Regex("""\n\`\`\`"""), "")
            .trimIndent()
    }

    private fun generatePrompt(htmlCode: String): String {
        val cleanedHtml = cleanHtml(htmlCode)
        return """
        Please extract from source HTML only count the occurrences of the following keywords in the headings and subheadings: "election", "war", "economy"
        Provide the output only in JSON array format, for example:
        [
            {
                <keyword>: <count>,
            },
            {
                <keyword>: <count>,
            },
            ...
        ]
        Replace 'keyword' with the actual keyword, 'count' with the number of occurrences
        Source HTML: $cleanedHtml
        """.trimIndent()
    }

    private fun cleanHtml(htmlCode: String): String {
        val doc = Jsoup.parse(htmlCode)
        val cleanBody = doc.body().html().replace(Regex("""<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>"""), "")
        val cleanedHtml = Jsoup.clean(cleanBody, "", Safelist.relaxed())
        return cleanedHtml
    }

    private fun askQuestion(question: String?): Optional<String> {
        try {
            val request: OpenAiRequest = OpenAiRequest.createWithUserMessage(question)
            val openAiResponse: OpenAiResponse = openAiClient.createCompletion(request)
            return openAiResponse.getAnswer()
        } catch (e: Exception) {
            log.error("Failed to ask question: {}", question, e)
            return Optional.empty()
        }
    }

    fun deserializeKeywordCounts(cleanResponse: String, gson: Gson): List<KeywordCount> {
        val type = object : TypeToken<List<Map<String, Int>>>() {}.type
        return gson.fromJson<List<Map<String, Int>>>(cleanResponse, type)
            .map { entry ->
                val keyword = entry.keys.first()
                val count = entry.values.first()
                KeywordCount(keyword, count)
            }
    }

}