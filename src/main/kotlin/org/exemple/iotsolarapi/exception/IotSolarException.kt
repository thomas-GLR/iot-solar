package org.exemple.iotsolarapi.exception

class IotSolarException(msg: String) : RuntimeException(msg) {

    companion object {
        fun userAlreadyExists() : IotSolarException = IotSolarException("L'utilisateur existe déjà")
    }
}