package org.exemple.iotsolarapi.resistanceStates.service

import org.exemple.iotsolarapi.parameters.interfaces.dto.EspParameterDto
import org.exemple.iotsolarapi.parameters.service.ParameterService
import org.exemple.iotsolarapi.resistanceStates.dao.model.ResistanceState
import org.exemple.iotsolarapi.resistanceStates.dao.repository.ResistanceStateRepository
import org.exemple.iotsolarapi.resistanceStates.interfaces.dto.ResistanceStateDto
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import kotlin.jvm.optionals.getOrElse

@Service
class ResistanceStateService(
    private val resistanceStateRepository: ResistanceStateRepository,
    private val resistanceStateDtoFactory: ResistanceStateDtoFactory,
    private val parameterService: ParameterService
) {
    fun getLastResistanceState(): ResistanceStateDto {
        val lastResistanceStateOpt = resistanceStateRepository.findTopByOrderByLastUpdateDesc()

        val lastResistanceState = lastResistanceStateOpt.getOrElse {
            ResistanceState(
                id = null,
                lastUpdate = now(),
                currentState = false
            )
        }

        return resistanceStateDtoFactory.resistanceStateDto(lastResistanceState)
    }

    fun getAllResistanceState(): List<ResistanceStateDto> {
        val resistanceStates = resistanceStateRepository.findAll()

        return resistanceStateDtoFactory.resistanceStatesDtos(resistanceStates)
    }

    fun createAndSendRequestToEsp32(resistanceStateDto: ResistanceStateDto): ResistanceStateDto {
        val espParameterDto = parameterService.getEspParameter()

        val resistanceState = ResistanceState(
            id = null,
            lastUpdate = now(),
            currentState = resistanceStateDto.currentState
        )

        // TODO envoyer la requête à l'ESP via MQTT ?

        val newResistanceState = resistanceStateRepository.save(resistanceState)

        return resistanceStateDtoFactory.resistanceStateDto(newResistanceState)
    }

    fun sendRequestToEsp32(espParameterDto: EspParameterDto, currentState: Boolean) {

    }
}