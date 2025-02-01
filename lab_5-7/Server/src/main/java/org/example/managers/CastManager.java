package org.example.managers;

import org.example.model.FuelType;
import org.example.model.VehicleType;

import java.util.logging.Logger;

public class CastManager {
    private static final Logger logger = Logger.getLogger(CastManager.class.getName());

    /**
     * Преобразует строковое представление в перечисление VehicleType.
     * Если совпадение не найдено, возвращает значение по умолчанию (DRONE).
     *
     * @param str строковое представление типа транспортного средства
     * @return соответствующий VehicleType
     */
    public static VehicleType castToVehicleType(String str) {
        for (VehicleType value : VehicleType.values()) {
            if (value.name().equalsIgnoreCase(str)) {
                logger.info("Преобразование строки '" + str + "' в VehicleType: " + value);
                return value;
            }
        }
        logger.warning("Не удалось преобразовать строку '" + str + "' в VehicleType. Используется значение по умолчанию DRONE.");
        return VehicleType.DRONE;
    }

    /**
     * Преобразует строковое представление в перечисление FuelType.
     * Если совпадение не найдено, возвращает значение по умолчанию (PLASMA).
     *
     * @param str строковое представление типа топлива
     * @return соответствующий FuelType
     */
    public static FuelType castToFuelType(String str) {
        for (FuelType value : FuelType.values()) {
            if (value.name().equalsIgnoreCase(str)) {
                logger.info("Преобразование строки '" + str + "' в FuelType: " + value);
                return value;
            }
        }
        logger.warning("Не удалось преобразовать строку '" + str + "' в FuelType. Используется значение по умолчанию PLASMA.");
        return FuelType.PLASMA;
    }
}
