package org.exemple.iotsolarapi.parameters.service

import org.exemple.iotsolarapi.parameters.dao.model.Parameter

data class EspParametersVo(
    val espIp: Parameter? = null,
    val espPort: Parameter? = null,
    val espProtocol: Parameter? = null
)