package org.example;

import org.example.collectionManager.CollectionManager;
import org.example.commandManager.*;
import org.example.managers.DumpManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

public class ServerMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int port;

        // Запрашиваем у пользователя номер порта для запуска сервера
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите порт: ");
            try {
                port = Integer.parseInt(scanner.nextLine());
                if (port > 0 && port <= 65535) {
                    break;
                } else {
                    System.out.println("Порт должен быть в диапазоне от 1 до 65535. Попробуйте ещё раз.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Попробуйте ещё раз.");
            }
        }

        // Инициализируем логирование после ввода порта
        System.setProperty("java.util.logging.config.file", System.getenv("logprop"));
        final Logger logger = Logger.getLogger(ServerMain.class.getName());

        Class.forName("org.postgresql.Driver");
        Properties info = new Properties();
        var commandManager = new CommandManager();

        // Загружаем свойства подключения к базе данных из переменной окружения
        info.load(new FileInputStream(System.getenv("cfg")));
        System.out.println("Пользователь БД: " + info.get("user"));

        // Инициализируем DumpManager с информацией о подключении к базе данных и путём к файлу логирования
        DumpManager dumpManager = new DumpManager(info, System.getenv("logprop"));

        // Создаём CollectionManager и загружаем данные из базы данных
        CollectionManager collectionManager = new CollectionManager(dumpManager);
        dumpManager.initDataBase();
        collectionManager.setCollection(dumpManager.readFromDataBase());

        // Регистрируем команды в CommandManager
        commandManager.addCommand("show", new Show(collectionManager, commandManager, logger));
        commandManager.addCommand("help", new Help(commandManager, logger));
        commandManager.addCommand("info", new Info(collectionManager, commandManager, logger));
        commandManager.addCommand("clear", new Clear(collectionManager, commandManager, logger));
        commandManager.addCommand("add", new Add(collectionManager, commandManager, logger));
        commandManager.addCommand("remove_by_id", new Remove_by_id(collectionManager, commandManager, logger));
        commandManager.addCommand("update", new Update(collectionManager, commandManager, logger));
        commandManager.addCommand("add_if_max", new Add_if_max(collectionManager, commandManager, logger));
        commandManager.addCommand("remove_greater", new Remove_greater(collectionManager, commandManager, logger));
        commandManager.addCommand("save", new Save(collectionManager, commandManager, logger));
        commandManager.addCommand("filter_by_number_of_wheels", new FilterByNumberOfWheelsServer(collectionManager, commandManager, logger));
        commandManager.addCommand("print_field_ascending_fuel_type", new PrintFieldAscendingFuelTypeServer(collectionManager, commandManager, logger));
        commandManager.addCommand("remove_lower", new Remove_lower(collectionManager, commandManager, logger));
        commandManager.addCommand("sum_of_number_of_wheels", new SumOfNumberOfWheelsServer(collectionManager, commandManager, logger));
        commandManager.addCommand("users", new Users(collectionManager, commandManager, logger));

        // Запускаем сервер на указанном порту
        Server server = new Server(port, commandManager, System.getenv("logprop"), dumpManager);
        server.start();
    }
}
