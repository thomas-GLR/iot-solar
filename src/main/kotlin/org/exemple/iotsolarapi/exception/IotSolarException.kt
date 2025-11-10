package org.exemple.iotsolarapi.exception

import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDeviceName

class IotSolarException(msg: String) : RuntimeException(msg) {

    companion object {
        fun userAlreadyExists() : IotSolarException = IotSolarException("L'utilisateur existe déjà")
        fun readingDeviceNameNotExist(readingDeviceName: ReadingDeviceName): IotSolarException = IotSolarException("La sonde avec le nom $readingDeviceName n'existe pas")
    }
}