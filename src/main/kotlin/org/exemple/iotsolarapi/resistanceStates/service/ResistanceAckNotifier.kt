package org.exemple.iotsolarapi.resistanceStates.service

import org.exemple.iotsolarapi.resistanceStates.interfaces.dto.ResistanceStateDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.Collections
import java.util.concurrent.CopyOnWriteArrayList

@Service
class ResistanceAckNotifier {

	private val logger: Logger = LoggerFactory.getLogger(ResistanceAckNotifier::class.java)

	private val emitters: MutableList<SseEmitter> = Collections.synchronizedList(mutableListOf())

	fun addEmitter(emitter: SseEmitter) {
		logger.info("Adding new SSE emitter. Total emitters: ${emitters.size + 1}")
		emitters.add(emitter)
		emitter.onCompletion { emitters.remove(emitter) }
		emitter.onTimeout {
			emitter.complete()
			emitters.remove(emitter)
		}
		emitter.onError { emitters.remove(emitter) }
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

	@Scheduled(fixedRate = 15_000)
	fun sendHeartbeat() {
		if (emitters.isNotEmpty()) {
			logger.info("Sending heartbeat to emitters. Total emitters: ${emitters.size}")
			val deadEmitters = mutableListOf<SseEmitter>()
			emitters.forEach { emitter ->
				try {
					// SSE comments (starting with ':') keep the connection alive
					// without triggering client-side message listeners
					emitter.send(SseEmitter.event().comment("ping"))
				} catch (e: Exception) {
					deadEmitters.add(emitter)
				}
			}

			logger.info("Removing ${deadEmitters.size} dead emitters. Remaining emitters: ${emitters.size - deadEmitters.size}")

			emitters.removeAll(deadEmitters)
		}
	}
}