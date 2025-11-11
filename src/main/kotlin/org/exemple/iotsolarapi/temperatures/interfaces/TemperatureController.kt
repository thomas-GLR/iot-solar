package org.exemple.iotsolarapi.temperatures.interfaces

import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDeviceName
import org.exemple.iotsolarapi.temperatures.interfaces.dto.AggregationType
import org.exemple.iotsolarapi.temperatures.interfaces.dto.CreateTemperatureDto
import org.exemple.iotsolarapi.temperatures.interfaces.dto.TemperatureDto
import org.exemple.iotsolarapi.temperatures.service.TemperatureService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/temperatures")
class TemperatureController(
    val temperatureService: TemperatureService
) {
    @GetMapping
    fun getAllTemperaturesForStartDateAndEndDate(
        @RequestParam("aggregation_type", required = false) aggregationType: AggregationType?,
        @RequestParam("start_date", required = false) startDate: LocalDateTime?,
        @RequestParam("end_date", required = false) endDate: LocalDateTime?
    ): List<TemperatureDto> {
        return temperatureService.getAllTemperaturesForStartDateAndEndDate(aggregationType, startDate, endDate);
    }

    @GetMapping("/detail")
    fun getTemperaturesDetail(
        @RequestParam("first_date") firstDate: LocalDateTime,
        @RequestParam("end_date") endDate: LocalDateTime,
        @RequestParam("reading_device_name") readingDeviceName: ReadingDeviceName
    ): List<TemperatureDto> {
        return temperatureService.getTemperaturesDetail(firstDate, endDate, readingDeviceName);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTemperature(@RequestBody createTemperatureDto: CreateTemperatureDto) {
        temperatureService.createTemperature(createTemperatureDto);
    }

    @GetMapping("/last-temperatures")
    fun getLastTemperatures(): List<TemperatureDto> {
        return temperatureService.getLastTemperatures()
    }
}