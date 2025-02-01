package org.example.managers;

import org.example.model.Coordinates;
import org.example.model.Vehicle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.collectionManager.IDManager.AddId;
import static org.example.managers.PasswordManager.hashPassword;

public class DumpManager {
    private static final Logger logger = Logger.getLogger(DumpManager.class.getName());
    private Connection connection;

    /**
     * Конструктор инициализирует подключение к базе данных.
     *
     * @param info     Свойства подключения к базе данных.
     * @param propPath Путь к файлу конфигурации логирования.
     * @throws IOException Если возникает ошибка ввода-вывода.
     */
    public DumpManager(Properties info, String propPath) throws IOException {
        System.setProperty("java.util.logging.config.file", propPath);
        start(info);
    }

    /**
     * Устанавливает соединение с базой данных с использованием предоставленных свойств.
     *
     * @param info Свойства подключения к базе данных.
     * @throws IOException Если возникает ошибка ввода-вывода.
     */
    public void start(Properties info) throws IOException {
        while (true) {
            try {
                String url = Files.readString(Paths.get(System.getenv("url")));
                connection = DriverManager.getConnection(url, info);
                logger.info("Соединение с базой данных установлено успешно.");
                break;
            } catch (SQLException | NullPointerException e) {
                logger.log(Level.SEVERE, "Ошибка подключения к базе данных: " + e.getMessage(), e);
                System.out.println("Введите 'Y', чтобы остановить сервер");
                Scanner scanner = new Scanner(System.in);
                if (scanner.nextLine().equalsIgnoreCase("Y")) {
                    logger.info("Сервер остановлен пользователем.");
                    System.exit(0);
                }
            }
        }
    }

    /**
     * Читает данные о транспортных средствах из базы данных и возвращает их в виде TreeSet.
     *
     * @return Набор транспортных средств из базы данных.
     */
    public TreeSet<Vehicle> readFromDataBase() {
        TreeSet<Vehicle> vehicles = new TreeSet<>();
        String sql = "SELECT * FROM Vehicle";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                Vehicle vehicle = new Vehicle();
                Coordinates coordinates = new Coordinates();
                vehicle.setId(rs.getLong("id"));
                AddId((int) rs.getLong("id"));
                vehicle.setName(rs.getString("name"));
                coordinates.setX(rs.getLong("coordinates_x"));
                coordinates.setY(rs.getFloat("coordinates_y"));
                vehicle.setCoordinates(coordinates);
                vehicle.setCreationDate(rs.getTimestamp("creationDate").toLocalDateTime());
                vehicle.setNumberOfWheels(rs.getInt("numberOfWheels"));
                vehicle.setEnginePower(rs.getDouble("enginePower"));
                vehicle.setType(CastManager.castToVehicleType(rs.getString("vType")));
                vehicle.setFuelType(CastManager.castToFuelType(rs.getString("fuelType")));
                vehicle.setUserName(rs.getString("userName"));
                vehicles.add(vehicle);
            }
            logger.info("Данные о транспортных средствах успешно загружены из базы данных.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при чтении из таблицы Vehicle.", e);
        }
        return vehicles;
    }

    /**
     * Инициализирует базу данных, создавая необходимые таблицы.
     */
    public void initDataBase() {
        createUserTable();
        createTableVehicle();
    }

    /**
     * Создаёт таблицу Coordinates в базе данных, если она не существует.
     */
    public void createTableCoordinates() {
        String sql = """
                CREATE TABLE IF NOT EXISTS Coordinates (
                    x bigint NOT NULL,
                    y float NOT NULL,
                    PRIMARY KEY (x, y)
                );
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
            logger.info("Таблица Coordinates создана или уже существует.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при создании таблицы Coordinates.", e);
        }
    }

    /**
     * Создаёт таблицу Vehicle в базе данных, если она не существует.
     */
    public void createTableVehicle() {
        createTableCoordinates();
        createIdSeq();

        String sql = """
                CREATE TABLE IF NOT EXISTS Vehicle (
                    id bigint NOT NULL DEFAULT nextval('ID_SEQ'),
                    name TEXT NOT NULL,
                    coordinates_x bigint NOT NULL,
                    coordinates_y float NOT NULL,
                    FOREIGN KEY(coordinates_x, coordinates_y) REFERENCES Coordinates(x, y),
                    creationDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    enginePower float CHECK (enginePower > 0),
                    numberOfWheels int NOT NULL CHECK (numberOfWheels > 0),
                    vType TEXT NOT NULL,
                    fuelType TEXT NOT NULL,
                    userName TEXT NOT NULL,
                    FOREIGN KEY (userName) REFERENCES users(userName)
                );
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
            logger.info("Таблица Vehicle создана или уже существует.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при создании таблицы Vehicle.", e);
        }
    }

    /**
     * Создаёт таблицу USERS в базе данных, если она не существует.
     */
    public void createUserTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS USERS (
                    userName TEXT PRIMARY KEY,
                    password TEXT
                );
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
            logger.info("Таблица USERS создана или уже существует.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при создании таблицы USERS.", e);
        }
    }

    /**
     * Создаёт последовательность ID_SEQ в базе данных, если она не существует.
     */
    public void createIdSeq() {
        String sql = "CREATE SEQUENCE IF NOT EXISTS ID_SEQ START WITH 1 INCREMENT BY 1;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
            logger.info("Последовательность ID_SEQ создана или уже существует.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при создании последовательности ID_SEQ.", e);
        }
    }

    /**
     * Проверяет, существует ли пользователь с заданным именем в базе данных.
     *
     * @param user_name Имя пользователя для проверки.
     * @return true, если пользователь существует, иначе false.
     */
    public boolean checkUser(String user_name) {
        boolean exists = false;
        String sql = "SELECT COUNT(*) AS count FROM users WHERE userName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user_name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    exists = resultSet.getInt("count") > 0;
                }
            }
            logger.info("Проверка существования пользователя: " + user_name);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при проверке существования пользователя.", e);
        }
        return exists;
    }

    /**
     * Регистрирует нового пользователя в базе данных.
     *
     * @param userName Имя пользователя.
     * @param pswd     Хэшированный пароль пользователя.
     */
    public void registerUser(String userName, String pswd) {
        String sql = "INSERT INTO users (userName, password) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, pswd);
            preparedStatement.executeUpdate();
            logger.info("Пользователь зарегистрирован успешно: " + userName);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при регистрации пользователя.", e);
        }
    }

    /**
     * Проверяет соответствие введённого пароля паролю пользователя в базе данных.
     *
     * @param userName Имя пользователя.
     * @param pswd     Введённый пароль.
     * @return true, если пароль совпадает, иначе false.
     */
    public boolean checkPassword(String userName, String pswd) {
        String sql = "SELECT password FROM Users WHERE userName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, userName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String hashedPassword = resultSet.getString("password");
                    String hashedInputPassword = hashPassword(pswd);
                    boolean match = hashedInputPassword.equals(hashedPassword);
                    logger.info("Проверка пароля для пользователя " + userName + ": " + (match ? "совпадает" : "не совпадает"));
                    return match;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при проверке пароля.", e);
        }
        return false;
    }

    /**
     * Получает список всех пользователей из базы данных.
     *
     * @return Список имён пользователей.
     */
    public ArrayList<String> getUsers() {
        ArrayList<String> users = new ArrayList<>();
        String sql = "SELECT userName FROM Users";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                users.add(resultSet.getString("userName"));
            }
            if (users.isEmpty()) {
                users.add("There are no users yet...");
            }
            logger.info("Список пользователей успешно получен.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при получении списка пользователей.", e);
        }
        return users;
    }

    /**
     * Сохраняет набор транспортных средств в базу данных для указанного пользователя.
     *
     * @param vehicles Набор транспортных средств.
     * @param userName Имя пользователя.
     */
    public void saveToDataBase(TreeSet<Vehicle> vehicles, String userName) {
        String deleteSql = "DELETE FROM vehicle WHERE userName = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
            deleteStatement.setString(1, userName);
            deleteStatement.executeUpdate();
            logger.info("Старые записи транспортных средств для пользователя " + userName + " удалены.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при удалении транспортных средств пользователя.", e);
        }

        String insertCoordinatesSql = "INSERT INTO Coordinates(x, y) VALUES (?, ?) ON CONFLICT (x, y) DO NOTHING";
        String insertVehicleSql = """
                INSERT INTO Vehicle(id, name, coordinates_x, coordinates_y, creationDate, enginePower, numberOfWheels, vType, fuelType, userName)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement coordStmt = connection.prepareStatement(insertCoordinatesSql);
                 PreparedStatement vehicleStmt = connection.prepareStatement(insertVehicleSql)) {

                for (Vehicle vehicle : vehicles) {
                    if (vehicle.getUserName().equals(userName) && !vehicle.getUserName().isEmpty()) {
                        Coordinates coords = vehicle.getCoordinates();

                        // Вставка координат
                        coordStmt.setLong(1, coords.getX());
                        coordStmt.setDouble(2, coords.getY());
                        coordStmt.executeUpdate();

                        // Вставка транспортного средства
                        vehicleStmt.setLong(1, vehicle.getId());
                        vehicleStmt.setString(2, vehicle.getName());
                        vehicleStmt.setLong(3, coords.getX());
                        vehicleStmt.setDouble(4, coords.getY());
                        vehicleStmt.setTimestamp(5, Timestamp.valueOf(vehicle.getCreationDate()));
                        vehicleStmt.setDouble(6, vehicle.getEnginePower());
                        vehicleStmt.setInt(7, vehicle.getNumberOfWheels());
                        vehicleStmt.setString(8, vehicle.getType().toString());
                        vehicleStmt.setString(9, vehicle.getFuelType().toString());
                        vehicleStmt.setString(10, vehicle.getUserName());
                        vehicleStmt.executeUpdate();
                    }
                }
                connection.commit();
                logger.info("Транспортные средства для пользователя " + userName + " успешно сохранены в базе данных.");
            } catch (SQLException e) {
                connection.rollback();
                logger.log(Level.SEVERE, "Ошибка при сохранении транспортных средств, транзакция откатана.", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при настройке автоматической фиксации транзакции.", e);
        }
    }
}
