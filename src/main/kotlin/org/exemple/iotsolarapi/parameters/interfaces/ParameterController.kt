package org.exemple.iotsolarapi.parameters.interfaces

import org.exemple.iotsolarapi.parameters.interfaces.dto.EspParameterDto
import org.exemple.iotsolarapi.parameters.interfaces.dto.ParameterDto
import org.exemple.iotsolarapi.parameters.service.ParameterService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/parameter")
class ParameterController(
    private val parameterService: ParameterService,
) {

    @GetMapping
    fun getParameter(@RequestParam("name") name: String): ParameterDto {
        return parameterService.getParameter(name)
    }

    @GetMapping(path = ["/esp"])
    fun getEspParameters(): EspParameterDto {
        return parameterService.getEspParameter()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createParameter(@RequestBody parameterDto: ParameterDto) {
        parameterService.createParameter(parameterDto)
    }

    @PutMapping
    fun updateParameter(@RequestBody parameterDto: ParameterDto) {
        parameterService.updateParameter(parameterDto)
    }

    @PutMapping(path = ["/esp"])
    fun updateEspParameters(@RequestBody espParameterDto: EspParameterDto) {
        parameterService.updateEspParameter(espParameterDto)
    }
}