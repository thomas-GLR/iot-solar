package org.exemple.iotsolarapi.temperatures.interfaces

import org.exemple.iotsolarapi.temperatures.interfaces.dto.AggregationType
import org.exemple.iotsolarapi.temperatures.interfaces.dto.TemperatureDto
import org.exemple.iotsolarapi.temperatures.service.TemperatureService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/temperatures")
class TemperatureController(
    val temperatureService: TemperatureService
) {
    @GetMapping
    fun getAllTemperatures(
        @RequestParam("aggregation_type", required = false) aggregationType: AggregationType?,
        @RequestParam("start_date", required = false) startDate: LocalDateTime?,
        @RequestParam("end_date", required = false) endDate: LocalDateTime?
    ): List<TemperatureDto> {
        return temperatureService.getAllTemperaturesForStartDateAndEndDate(aggregationType, startDate, endDate);
    }
}