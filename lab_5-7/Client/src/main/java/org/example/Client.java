package org.example;

import org.example.inputManager.Ask;
import org.example.inputManager.StandartConsole;
import org.example.model.Vehicle;
import org.example.response.Response;
import org.example.response.STATUS;
import org.example.utills.PortAsker;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.inputManager.Ask.askVehicle;

public class Client {
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    ObjectOutputStream oos;
    ObjectInputStream ois;
    ArrayList<String> scriptHistory = new ArrayList<>();
    private final String host;
    private int port;

    /**
     * Конструктор клиента для подключения к серверу.
     *
     * @param host хост сервера
     * @param port порт сервера
     */
    public Client(String host, int port) {
        this.port = port;
        this.host = host;
        logger.info("Клиент инициализирован с хостом: " + host + " и портом: " + port);
    }

    /**
     * Запускает клиентское приложение и устанавливает соединение с сервером.
     */
    public void run() {
        while (true) {
            try {
                Socket socket = new Socket(host, port);
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
                logger.info("Установлено соединение с сервером.");
                start();
                if (!socket.isClosed()) {
                    break;
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.log(Level.SEVERE, "Не удалось подключиться к серверу. Пожалуйста, попробуйте позже.", e);
                System.out.print("Введите exit, чтобы завершить работу или любой другой символ, чтобы продолжить: ");
                Scanner scanner = new Scanner(System.in);
                if (scanner.hasNextLine()) {
                    if (scanner.nextLine().equals("exit")) {
                        System.out.println("Завершение работы");
                        System.exit(0);
                    }
                }
                System.out.print("Введите Y, чтобы сменить порт: ");
                this.port = PortAsker.getPort();
                logger.info("Изменён порт на: " + port);
            }
        }
    }

    /**
     * Запускает основной цикл взаимодействия с сервером.
     *
     * @param scanner         источник пользовательского ввода
     * @param standartConsole консоль для ввода данных
     * @throws IOException            если возникает ошибка ввода-вывода
     * @throws ClassNotFoundException если получен некорректный объект от сервера
     */
    public void launch(Scanner scanner, StandartConsole standartConsole) throws IOException, ClassNotFoundException {
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            logger.info("Получена команда от пользователя: " + s);
            if (s.trim().split(" ")[0].equals("exit")) {
                Response response = new Response(STATUS.SAVE);
                oos.writeObject(response);
                oos.flush();
                System.out.println(ois.readObject());
                break;
            }
            if (s.trim().split(" ")[0].equals("register")) {
                handleRegister();
                continue;
            }
            if (s.trim().split(" ")[0].equals("login")) {
                handleLogin();
                continue;
            }
            if (s.trim().split(" ")[0].equals("logout")) {
                handleLogout();
                continue;
            }
            if (s.trim().split(" ")[0].equals("execute_script")) {
                scriptMode((s.trim() + " ").split(" ", 2)[1].trim());
                continue;
            }
            Response response1 = new Response(STATUS.COMMAND, s);
            try {
                oos.writeObject(response1);
                oos.flush();
                Response response2 = (Response) ois.readObject();
                if (response2.getStatus().equals(STATUS.NEED_OBJECT)) {
                    try {
                        Vehicle vehicle = askVehicle(standartConsole, 999999999);
                        if (!response2.getObject().equals("")) {
                            vehicle.setId((Integer) response2.getObject());
                        }
                        Response response3 = new Response(STATUS.COMMAND, s, vehicle);
                        oos.writeObject(response3);
                        oos.flush();
                        System.out.println(ois.readObject());
                    } catch (Ask.AskBreak e) {
                        System.out.println("Завершение работы");
                        System.exit(1);
                    }
                } else {
                    if (!response2.getMessage().equals("Такой команды не существует")) {
                        System.out.println(response2);
                    }
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Ошибка при отправке запроса на сервер.", e);
                run();
            }
        }
    }

    /**
     * Обрабатывает регистрацию нового пользователя.
     *
     * @throws IOException            если возникает ошибка ввода-вывода
     * @throws ClassNotFoundException если получен некорректный объект от сервера
     */
    private void handleRegister() throws IOException, ClassNotFoundException {
        Scanner scanner_register = new Scanner(System.in);
        while (true) {
            System.out.print("Enter name -> ");
            if (scanner_register.hasNextLine()) {
                String user_name = scanner_register.nextLine();
                Response checkUserRequest = new Response(STATUS.USERCHECK, "checkUser", user_name);
                oos.writeObject(checkUserRequest);
                oos.flush();
                Response checkUserResponse = (Response) ois.readObject();
                if ((boolean) checkUserResponse.getObject()) {
                    System.out.println("This name is already exists!");
                    System.out.println("Enter 'Y' to try to login with another name or use command 'login'");
                    String inputCheck = scanner_register.nextLine();
                    if (!(inputCheck.equalsIgnoreCase("Y"))) {
                        break;
                    }
                } else {
                    while (true) {
                        System.out.print("Enter password -> ");
                        String pass1 = enterPassword();
                        System.out.print("Accept password -> ");
                        String pass2 = enterPassword();
                        if (!pass1.isEmpty() && pass1.equals(pass2)) {
                            Response registerUserRequest = new Response(STATUS.USERCHECK, "registerUser", user_name + " " + pass1);
                            oos.writeObject(registerUserRequest);
                            oos.flush();
                            System.out.println(ois.readObject());
                            break;
                        } else {
                            System.out.println("Password mismatch");
                            System.out.println("Enter 'Y' to try again");
                            String inputCheck = scanner_register.nextLine();
                            if (!(inputCheck.equalsIgnoreCase("Y"))) break;
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * Обрабатывает вход пользователя в систему.
     *
     * @throws IOException            если возникает ошибка ввода-вывода
     * @throws ClassNotFoundException если получен некорректный объект от сервера
     */
    private void handleLogin() throws IOException, ClassNotFoundException {
        Scanner scanner_login = new Scanner(System.in);
        while (true) {
            System.out.print("Enter name to login -> ");
            if (scanner_login.hasNextLine()) {
                String user_name = scanner_login.nextLine();
                Response checkUserRequest = new Response(STATUS.USERCHECK, "checkUser", user_name);
                oos.writeObject(checkUserRequest);
                oos.flush();
                Response checkUserResponse = (Response) ois.readObject();
                if ((boolean) checkUserResponse.getObject()) {
                    while (true) {
                        System.out.print("Enter password -> ");
                        String pswd1 = enterPassword();
                        Response checkPasswordRequest = new Response(STATUS.USERCHECK, "checkPassword", user_name + " " + pswd1);
                        oos.writeObject(checkPasswordRequest);
                        oos.flush();
                        Response checkPasswordResponse = (Response) ois.readObject();
                        if ((boolean) checkPasswordResponse.getObject()) {
                            System.out.println("Вы успешно вошли в аккаунт!");
                            break;
                        } else {
                            System.out.println("Enter 'Y' if you want to try again");
                            String inputCheck = scanner_login.nextLine();
                            if (!(inputCheck.equalsIgnoreCase("Y"))) break;
                        }
                    }
                    break;
                } else {
                    System.out.println("Error: Can't find such user");
                    System.out.println("Enter 'Y' if you want to try again, or try to register");
                    String inputCheck = scanner_login.nextLine();
                    if (!(inputCheck.equalsIgnoreCase("Y"))) break;
                }
            }
        }
    }

    /**
     * Обрабатывает выход пользователя из системы.
     *
     * @throws IOException если возникает ошибка ввода-вывода
     */
    private void handleLogout() throws IOException {
        Scanner scanner_logout = new Scanner(System.in);
        System.out.println("Enter Y if you really want to logout");
        String inputCheck = scanner_logout.nextLine();
        if (inputCheck.equalsIgnoreCase("Y")) {
            oos.writeObject(new Response(STATUS.USERCHECK, "logout"));
            oos.flush();
            System.out.println("You are logged out");
        }
    }

    /**
     * Запускает клиент в режиме выполнения скрипта.
     *
     * @param args путь к файлу скрипта
     * @throws IOException            если возникает ошибка ввода-вывода
     * @throws ClassNotFoundException если получен некорректный объект от сервера
     */
    public void scriptMode(String args) throws IOException, ClassNotFoundException {
        if (!new File(args).exists()) {
            System.out.println(new Response(STATUS.ERROR, "Файл не существует!"));
            start();
        }
        if (!Files.isReadable(Paths.get(args))) {
            System.out.println(new Response(STATUS.ERROR, "Нет прав для чтения файла!"));
            start();
        }
        try (Scanner scriptScanner = new Scanner(new File(args))) {
            StandartConsole standartConsole = new StandartConsole();
            standartConsole.selectFileScanner(scriptScanner);
            if (scriptHistory.contains(args)) {
                System.out.println("Обнаружена рекурсия в скрипте!");
                start();
            } else {
                scriptHistory.add(args);
                launch(scriptScanner, standartConsole);
                scriptHistory.clear();
                start();
            }
        }
    }

    /**
     * Запускает основной цикл работы клиента.
     *
     * @throws IOException            если возникает ошибка ввода-вывода
     * @throws ClassNotFoundException если получен некорректный объект от сервера
     */
    public void start() throws IOException, ClassNotFoundException {
        StandartConsole standartConsole = new StandartConsole();
        Scanner scanner = new Scanner(System.in);
        launch(scanner, standartConsole);
    }

    /**
     * Считывает пароль, введённый пользователем.
     *
     * @return введённый пароль
     */
    public static String enterPassword() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
