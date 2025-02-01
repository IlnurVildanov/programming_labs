package org.example.inputManager;

import org.example.model.Coordinates;
import org.example.model.FuelType;
import org.example.model.Vehicle;
import org.example.model.VehicleType;

import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Класс Ask предоставляет методы для ввода данных о транспортном средстве с консоли.
 */
public class Ask {
    private static final Logger logger = Logger.getLogger(Ask.class.getName());

    /**
     * Исключение, используемое для выхода из цикла ввода.
     */
    public static class AskBreak extends Exception {
    }

    /**
     * Запрашивает данные о транспортном средстве с консоли.
     *
     * @param console консольный интерфейс
     * @param id      идентификатор транспортного средства
     * @return объект транспортного средства
     * @throws AskBreak если пользователь завершает ввод
     */
    public static Vehicle askVehicle(Console console, long id) throws AskBreak {
        try {
            String name = askName(console);
            Coordinates coordinates = askCoordinates(console);
            java.time.LocalDateTime creationDate = java.time.LocalDateTime.now();
            double enginePower = askEnginePower(console);
            int numberOfWheels = askNumberOfWheels(console);
            VehicleType type = askVehicleType(console);
            FuelType fuelType = askFuelType(console);
            logger.info("Транспортное средство успешно создано.");
            return new Vehicle(id, name, coordinates, creationDate, enginePower, numberOfWheels, type, fuelType);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            logger.log(Level.SEVERE, "Ошибка при вводе транспортного средства.", e);
            return null;
        }
    }

    private static String askName(Console console) throws AskBreak {
        try {
            String name;
            while (true) {
                console.print("name: ");
                name = console.readln().trim();
                if (name.equals("exit")) throw new AskBreak();
                if (!name.isEmpty()) {
                    break;
                }
            }
            return name;
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения имени.");
            logger.log(Level.SEVERE, "Ошибка при вводе имени.", e);
            throw new AskBreak();
        }
    }

    private static Coordinates askCoordinates(Console console) throws AskBreak {
        try {
            long x;
            double y;

            while (true) {
                console.print("coordinates.x: ");
                var lineX = console.readln().trim();
                if (lineX.equals("exit")) throw new AskBreak();
                if (!lineX.isEmpty()) {
                    try {
                        x = Long.parseLong(lineX);
                        break;
                    } catch (NumberFormatException e) {
                        console.printError("Некорректный формат для x. Попробуйте снова.");
                    }
                }
            }

            while (true) {
                console.print("coordinates.y: ");
                var lineY = console.readln().trim();
                if (lineY.equals("exit")) throw new AskBreak();
                if (!lineY.isEmpty()) {
                    try {
                        y = Double.parseDouble(lineY);
                        break;
                    } catch (NumberFormatException e) {
                        console.printError("Некорректный формат для y. Попробуйте снова.");
                    }
                }
            }

            return new Coordinates(x, y);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения координат.");
            logger.log(Level.SEVERE, "Ошибка при вводе координат.", e);
            throw new AskBreak();
        }
    }

    private static double askEnginePower(Console console) throws AskBreak {
        try {
            double enginePower;
            while (true) {
                console.print("enginePower: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        enginePower = Double.parseDouble(line);
                        if (enginePower > 0) {
                            break;
                        } else {
                            console.printError("Значение должно быть больше 0.");
                        }
                    } catch (NumberFormatException e) {
                        console.printError("Некорректный формат. Попробуйте снова.");
                    }
                }
            }
            return enginePower;
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения мощности двигателя.");
            logger.log(Level.SEVERE, "Ошибка при вводе мощности двигателя.", e);
            throw new AskBreak();
        }
    }

    private static int askNumberOfWheels(Console console) throws AskBreak {
        try {
            int numberOfWheels;
            while (true) {
                console.print("numberOfWheels: ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        numberOfWheels = Integer.parseInt(line);
                        if (numberOfWheels > 0) {
                            break;
                        } else {
                            console.printError("Значение должно быть больше 0.");
                        }
                    } catch (NumberFormatException e) {
                        console.printError("Некорректный формат. Попробуйте снова.");
                    }
                }
            }
            return numberOfWheels;
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения количества колёс.");
            logger.log(Level.SEVERE, "Ошибка при вводе количества колёс.", e);
            throw new AskBreak();
        }
    }

    private static VehicleType askVehicleType(Console console) throws AskBreak {
        try {
            VehicleType type;
            while (true) {
                console.print("VehicleType (" + VehicleType.names() + "): ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        type = VehicleType.valueOf(line.toUpperCase());
                        break;
                    } catch (IllegalArgumentException e) {
                        console.printError("Некорректный тип транспортного средства. Попробуйте снова.");
                    }
                } else {
                    console.printError("Тип транспортного средства не может быть пустым.");
                }
            }
            return type;
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения типа транспортного средства.");
            logger.log(Level.SEVERE, "Ошибка при вводе типа транспортного средства.", e);
            throw new AskBreak();
        }
    }

    private static FuelType askFuelType(Console console) throws AskBreak {
        try {
            FuelType fuelType;
            while (true) {
                console.print("FuelType (" + FuelType.names() + "): ");
                var line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        fuelType = FuelType.valueOf(line.toUpperCase());
                        break;
                    } catch (IllegalArgumentException e) {
                        console.printError("Некорректный тип топлива. Попробуйте снова.");
                    }
                } else {
                    console.printError("Тип топлива не может быть пустым.");
                }
            }
            return fuelType;
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения типа топлива.");
            logger.log(Level.SEVERE, "Ошибка при вводе типа топлива.", e);
            throw new AskBreak();
        }
    }
}
