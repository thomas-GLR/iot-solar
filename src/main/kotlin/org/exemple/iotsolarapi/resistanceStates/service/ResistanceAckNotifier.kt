package org.exemple.iotsolarapi.resistanceStates.service

import org.exemple.iotsolarapi.resistanceStates.interfaces.dto.ResistanceStateDto
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.CopyOnWriteArrayList

@Service
class ResistanceAckNotifier {
    private val emitters = CopyOnWriteArrayList<SseEmitter>()

    fun addEmitter(emitter: SseEmitter) {
        emitters.add(emitter)
        emitter.onCompletion { emitters.remove(emitter) }
        emitter.onTimeout { emitters.remove(emitter) }
    }

    fun notify(event: ResistanceStateDto) {
        val deadEmitters = mutableListOf<SseEmitter>()
        emitters.forEach { emitter ->
            try {
                emitter.send(
                    SseEmitter.event()
                        .name("resistance-ack")
                        .data(event)
                )
            } catch (e: Exception) {
                deadEmitters.add(emitter)
            }
        }
        emitters.removeAll(deadEmitters)
    }
}