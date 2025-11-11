package org.exemple.iotsolarapi.parameters.service

import org.exemple.iotsolarapi.parameters.dao.model.Parameter
import org.exemple.iotsolarapi.parameters.interfaces.dto.ParameterDto
import org.springframework.stereotype.Service

@Service
class ParameterDtoFactory {
    fun parameterDto(parameter: Parameter): ParameterDto {
        return ParameterDto(
            id = parameter.id,
            name = parameter.name,
            value = parameter.value
        )
    }
}