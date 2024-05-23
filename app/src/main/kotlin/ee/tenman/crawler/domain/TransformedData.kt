package ee.tenman.crawler.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
data class TransformedData(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val term: String,
    val incidence: Int,
    val site: String,
    val timestamp: LocalDateTime
) {
    constructor() : this(0, "", 0, "", LocalDateTime.now())
}