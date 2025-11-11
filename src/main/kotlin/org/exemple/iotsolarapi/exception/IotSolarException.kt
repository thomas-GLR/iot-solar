package org.exemple.iotsolarapi.exception

import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDeviceName

class IotSolarException(msg: String) : RuntimeException(msg) {

    companion object {
        fun userAlreadyExists() : IotSolarException = IotSolarException("L'utilisateur existe déjà")
        fun readingDeviceNameNotExist(readingDeviceName: ReadingDeviceName): IotSolarException = IotSolarException("La sonde avec le nom $readingDeviceName n'existe pas")
        fun parameterNotFound(name: String): IotSolarException = IotSolarException("Le paramètre '$name' n'a pas été trouvé")
        fun espParametersNotFound(): IotSolarException = IotSolarException("Les paramètres pour l'ESP n'ont pas été trouvés")
        fun notAllEspParameters(): IotSolarException = IotSolarException("Tous les paramètres de l'ESP n'ont pas été renseignés")
    }
}