package org.exemple.iotsolarapi.resistanceStates.interfaces

import org.exemple.iotsolarapi.resistanceStates.interfaces.dto.CreateResistanceStateDto
import org.exemple.iotsolarapi.resistanceStates.interfaces.dto.ResistanceStateDto
import org.exemple.iotsolarapi.resistanceStates.service.ResistanceAckNotifier
import org.exemple.iotsolarapi.resistanceStates.service.ResistanceStateService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/resistance")
class ResistanceStateController(
    val resistanceStateService: ResistanceStateService,
    val resistanceAckNotifier: ResistanceAckNotifier
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
    suspend fun createAndSendRequestToEsp32(@RequestBody createResistanceStateDto: CreateResistanceStateDto): ResistanceStateDto {
        return resistanceStateService.createAndSendRequestToEsp32(createResistanceStateDto)
    }

    @GetMapping("/ack/stream")
    fun streamAck(): SseEmitter {
        val emitter = SseEmitter(10 * 60_000L) // timeout  10 * 60s = 10 minutes
        resistanceAckNotifier.addEmitter(emitter)
        return emitter
    }
}