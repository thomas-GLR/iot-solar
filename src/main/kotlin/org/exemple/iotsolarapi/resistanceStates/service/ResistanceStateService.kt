package org.exemple.iotsolarapi.resistanceStates.service

import kotlinx.coroutines.runBlocking
import org.exemple.iotsolarapi.exception.IotSolarException
import org.exemple.iotsolarapi.mqtt.service.MqttClientHelper
import org.exemple.iotsolarapi.parameters.service.ParameterService
import org.exemple.iotsolarapi.resistanceStates.dao.model.ResistanceState
import org.exemple.iotsolarapi.resistanceStates.dao.repository.ResistanceStateRepository
import org.exemple.iotsolarapi.resistanceStates.interfaces.dto.CreateResistanceStateDto
import org.exemple.iotsolarapi.resistanceStates.interfaces.dto.ResistanceStateDto
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.time.LocalDateTime.now
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.jvm.optionals.getOrElse

@Service
class ResistanceStateService(
    private val resistanceStateRepository: ResistanceStateRepository,
    private val resistanceStateDtoFactory: ResistanceStateDtoFactory,
    private val mqttClientHelper: MqttClientHelper,
    private val resistanceAckNotifier: ResistanceAckNotifier
) {
    private val emitters = CopyOnWriteArrayList<SseEmitter>()

    private val TOPIC_RESISTANCE = "iotsolar/resistance"

    fun getLastResistanceState(): ResistanceStateDto {
        val lastResistanceStateOpt = resistanceStateRepository.findTopByOrderByLastUpdateDesc()

        val lastResistanceState = lastResistanceStateOpt.getOrElse {
            ResistanceState(
                id = null,
                lastUpdate = now(),
                currentState = false,
                requestedState = false
            )
        }

        return resistanceStateDtoFactory.resistanceStateDto(lastResistanceState)
    }

    fun getAllResistanceState(): List<ResistanceStateDto> {
        val resistanceStates = resistanceStateRepository.findAll()

        return resistanceStateDtoFactory.resistanceStatesDtos(resistanceStates)
    }

    fun updateResistanceStateFromEsp32Ack() {
        val resistanceStateOpt = resistanceStateRepository.findTopByOrderByLastUpdateDesc()

        if (resistanceStateOpt.isPresent && resistanceStateOpt.get().currentState == null) {
            val resistanceState = resistanceStateOpt.get()
            resistanceState.currentState = resistanceState.requestedState
            resistanceState.lastUpdate = now()
            resistanceStateRepository.save(resistanceState)

            // Notify all SSE clients about the update
            val resistanceStateDto = resistanceStateDtoFactory.resistanceStateDto(resistanceState)
            resistanceAckNotifier.notify(resistanceStateDto)
        }
    }

    suspend fun createAndSendRequestToEsp32(createResistanceStateDto: CreateResistanceStateDto): ResistanceStateDto {
        val resistanceState = ResistanceState(
            id = null,
            lastUpdate = now(),
            currentState = null, // The value is null until we receive the acknowledgment from the ESP32, which will update this state accordingly.
            requestedState = createResistanceStateDto.currentState
        )

        val currentState = if (createResistanceStateDto.currentState) "1" else "0"

        val result = mqttClientHelper.publishSuspend(TOPIC_RESISTANCE, currentState, 2, false)

        if (result.isFailure) {
            val exception = result.exceptionOrNull()

            val message = exception?.message ?: "Une erreur est survenue."

            throw IotSolarException.noResponseFromEspForResistanceState(message)
        }

        val newResistanceState = resistanceStateRepository.save(resistanceState)

        return resistanceStateDtoFactory.resistanceStateDto(newResistanceState)
    }
}