package ee.tenman.crawler.controller

import ee.tenman.crawler.service.DataService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/data")
class DataController(private val dataService: DataService) {
    @Operation(summary = "Get average incidence by day and term")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "OK", content = [
            Content(mediaType = "application/json", schema = Schema(
                type = "object",
                description = "Map of terms to their average incidence by day",
                example = "{" +
                        "  \"term1\": {" +
                        "    \"2023-05-01\": 10.5," +
                        "    \"2023-05-02\": 8.2" +
                        "  }," +
                        "  \"term2\": {" +
                        "    \"2023-05-01\": 5.8," +
                        "    \"2023-05-02\": 7.1" +
                        "  }" +
                        "}"
            ))
        ])
    ])
    @GetMapping("/average-incidence")
    fun getAverageIncidenceByDayAndTerm(
        @Parameter(
            description = "Start date",
            schema = Schema(
                type = "string",
                format = "date",
                example = "2023-05-01"
            )
        )
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @Parameter(
            description = "End date",
            schema = Schema(
                type = "string",
                format = "date",
                example = "2025-05-31"
            )
        )
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): Map<String, Map<LocalDate, Double>> {
        return dataService.getAverageIncidenceByDayAndTerm(startDate, endDate)
    }
}