package org.exemple.iotsolarapi.parameters.service

import org.exemple.iotsolarapi.exception.IotSolarException
import org.exemple.iotsolarapi.parameters.dao.model.Parameter
import org.exemple.iotsolarapi.parameters.dao.model.ParameterName
import org.exemple.iotsolarapi.parameters.dao.repository.ParameterRepository
import org.exemple.iotsolarapi.parameters.interfaces.dto.EspParameterDto
import org.exemple.iotsolarapi.parameters.interfaces.dto.ParameterDto
import org.springframework.stereotype.Service

@Service
class ParameterService(
    private val parameterRepository: ParameterRepository,
    private val parameterDtoFactory: ParameterDtoFactory
) {
    fun getParameter(name: String): ParameterDto {
        val parameter = parameterRepository.findByName(name).orElseThrow {
            IotSolarException.parameterNotFound(name)
        }

        return parameterDtoFactory.parameterDto(parameter)
    }

    fun getEspParameter(): EspParameterDto {
        val espParameters = parameterRepository.findAllByNameStartingWithIgnoreCase("ESP")

        if (espParameters.isEmpty()) {
            throw IotSolarException.espParametersNotFound()
        }

        val espParametersVo = extractEspParametersAndVerifyTheirExistence(espParameters)

        return EspParameterDto(
            espIp = parameterDtoFactory.parameterDto(espParametersVo.espIp!!),
            espPort = parameterDtoFactory.parameterDto(espParametersVo.espPort!!),
            espProtocol = parameterDtoFactory.parameterDto(espParametersVo.espProtocol!!)
        )
    }

    fun createParameter(parameterDto: ParameterDto) {
        val parameterOpt = parameterRepository.findByName(parameterDto.name)

        if (parameterOpt.isEmpty) {
            val newParameter = Parameter(
                name = parameterDto.name,
                value = parameterDto.value,
            )

            parameterRepository.save(newParameter)
        }
    }

    fun updateParameter(parameterDto: ParameterDto) {
        val parameter = parameterRepository.findByName(parameterDto.name).orElseThrow {
            IotSolarException.parameterNotFound(parameterDto.name)
        }

        parameter.value = parameterDto.value
    }

    fun updateEspParameter(espParameterDto: EspParameterDto) {
        val espParameters = parameterRepository.findByNameIn(setOf(
            espParameterDto.espIp.name,
            espParameterDto.espPort.name,
            espParameterDto.espProtocol.name))

        val espParametersVo = extractEspParametersAndVerifyTheirExistence(espParameters)

        espParametersVo.espIp!!.value = espParameterDto.espIp.value
        espParametersVo.espPort!!.value = espParameterDto.espPort.value
        espParametersVo.espProtocol!!.value = espParameterDto.espProtocol.value

        parameterRepository.saveAll(listOf(espParametersVo.espIp, espParametersVo.espPort, espParametersVo.espProtocol))
    }

    private fun extractEspParametersAndVerifyTheirExistence(espParameters: List<Parameter>): EspParametersVo {
        val espIp = espParameters.find { espParameter ->
            espParameter.name == ParameterName.ESP_IP.parameterName
        }
        val espPort = espParameters.find { espParameter ->
            espParameter.name == ParameterName.ESP_PORT.parameterName
        }
        val espProtocol = espParameters.find { espParameter ->
            espParameter.name == ParameterName.ESP_PROTOCOL.parameterName
        }

        if (espIp == null || espPort == null || espProtocol == null) {
            throw IotSolarException.notAllEspParameters()
        }

        return EspParametersVo(
            espIp,
            espPort,
            espProtocol
        )
    }
}