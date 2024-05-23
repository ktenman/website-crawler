package ee.tenman.crawler.queue

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Message @JsonCreator constructor(
    @JsonProperty("url") val url: String,
    @JsonProperty("body") val body: String
)