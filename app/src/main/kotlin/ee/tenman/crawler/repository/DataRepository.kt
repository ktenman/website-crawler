package ee.tenman.crawler.repository

import ee.tenman.crawler.domain.TransformedData
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface DataRepository : JpaRepository<TransformedData, Long> {
    fun findByTimestampBetween(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<TransformedData>
}