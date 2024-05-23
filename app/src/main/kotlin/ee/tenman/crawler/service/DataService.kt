package ee.tenman.crawler.service

import ee.tenman.crawler.domain.TransformedData
import ee.tenman.crawler.repository.DataRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DataService(private val dataRepository: DataRepository) {
    fun saveAllData(data: List<TransformedData>): List<TransformedData> {
        return dataRepository.saveAll(data)
    }

    fun getAverageIncidenceByDayAndTerm(startDate: LocalDate, endDate: LocalDate): Map<String, Map<LocalDate, Double>> {
        val data = dataRepository.findByTimestampBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay())
        return data.groupBy { it.term }
            .mapValues { (_, termData) ->
                termData.groupBy { it.timestamp.toLocalDate() }
                    .mapValues { (_, dayData) -> dayData.map { it.incidence }.average() }
            }
    }
}