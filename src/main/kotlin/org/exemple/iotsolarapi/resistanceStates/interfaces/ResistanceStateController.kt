package org.exemple.iotsolarapi.resistanceStates.interfaces

import org.exemple.iotsolarapi.resistanceStates.interfaces.dto.ResistanceStateDto
import org.exemple.iotsolarapi.resistanceStates.service.ResistanceStateService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/resistance")
class ResistanceStateController(
    val resistanceStateService: ResistanceStateService
) {
    @GetMapping
    fun getLastResistanceState(): ResistanceStateDto {
        return resistanceStateService.getLastResistanceState()
    }

//    @GetMapping
//    fun getAllResistanceStates(): List<ResistanceStateDto> {
//        return resistanceStateService.getAllResistanceState()
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAndSendRequestToEsp32(@RequestBody resistanceStateDto: ResistanceStateDto): ResistanceStateDto {
        return resistanceStateService.createAndSendRequestToEsp32(resistanceStateDto);
    }
}