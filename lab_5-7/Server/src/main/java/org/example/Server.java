package org.example;

import org.example.commandManager.CommandManager;
import org.example.launcher.LaunchCommand;
import org.example.managers.DumpManager;
import org.example.managers.PasswordManager;
import org.example.managers.UserStatusManager;
import org.example.response.Response;
import org.example.response.STATUS;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final int port;
    private final LaunchCommand launchCommand;
    private final Map<SocketChannel, ObjectOutputStream> clients;
    private final Map<SocketChannel, UserStatusManager> users;
    private final ForkJoinPool requestThreadPool;
    private final ExecutorService processingThreadPool;
    private final ForkJoinPool responseThreadPool;
    private final DumpManager dumpManager;

    public Server(int port, CommandManager commandManager, String propPath, DumpManager dumpManager) {
        this.port = port;
        this.launchCommand = new LaunchCommand(commandManager);
        this.dumpManager = dumpManager;
        this.clients = Collections.synchronizedMap(new HashMap<>());
        this.users = Collections.synchronizedMap(new HashMap<>());
        this.requestThreadPool = new ForkJoinPool();
        this.processingThreadPool = Executors.newFixedThreadPool(4);
        this.responseThreadPool = new ForkJoinPool();
        System.setProperty("java.util.logging.config.file", propPath);
    }

    public void start() throws IOException {
        logger.info("Запуск сервера");
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress("localhost", port));
        while (true) {
            logger.info("Ожидание подключения клиента");
            SocketChannel clientChannel = serverSocketChannel.accept();
            synchronized (clients) {
                ObjectOutputStream oos = new ObjectOutputStream(clientChannel.socket().getOutputStream());
                UserStatusManager userStatusManager = new UserStatusManager(false, "");
                clients.put(clientChannel, oos);
                users.put(clientChannel, userStatusManager);
            }
            requestThreadPool.execute(() -> handleClient(clientChannel));
        }
    }

    private void handleClient(SocketChannel clientChannel) {
        try {
            UserStatusManager userStatusManager = users.get(clientChannel);
            ObjectInputStream ois = new ObjectInputStream(clientChannel.socket().getInputStream());
            while (true) {
                try {
                    Response message = (Response) ois.readObject();
                    logger.info("Получен запрос от клиента: " + userStatusManager.getUserName());
                    if (message.getStatus().equals(STATUS.COMMAND)) {
                        if (message.getMessage().equals("save")) {
                            sendResponse(clientChannel, new Response(STATUS.ERROR, "Такой команды не существует \nЧтобы сохраниться выйдите из аккаунта!"));
                        } else {
                            processingThreadPool.execute(() -> {
                                Response commandResult = launchCommand.commandParser(message.getMessage(), message.getObject(), userStatusManager);
                                sendResponse(clientChannel, commandResult);
                            });
                        }
                    } else if (message.getStatus().equals(STATUS.USERCHECK)) {
                        processingThreadPool.execute(() -> handleUserCheck(clientChannel, message, userStatusManager));
                    } else {
                        Response commandResult = launchCommand.doCommand("save", "", "", userStatusManager);
                        sendResponse(clientChannel, commandResult);
                    }
                } catch (IOException e) {
                    logger.info("Клиент = " + userStatusManager.getUserName() + " отключился");
                    synchronized (clients) {
                        clients.remove(clientChannel);
                        userStatusManager.setUserName("");
                        userStatusManager.setStatus(false);
                        users.remove(clientChannel);
                    }
                    break;
                } catch (ClassNotFoundException e) {
                    logger.log(Level.SEVERE, "Ошибка при чтении объекта от клиента = " + userStatusManager.getUserName(), e);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка при обработке клиента: " + users.get(clientChannel).getUserName(), e);
        }
    }

    private void handleUserCheck(SocketChannel clientChannel, Response message, UserStatusManager userStatusManager) {
        try {
            Response commandResult;
            String data;
            String username;
            String password;
            switch (message.getMessage()) {
                case "checkUser":
                    boolean userExists = dumpManager.checkUser((String) message.getObject());
                    commandResult = new Response(STATUS.USERCHECK, "", userExists);
                    sendResponse(clientChannel, commandResult);
                    break;
                case "registerUser":
                    data = (String) message.getObject();
                    username = data.split(" ")[0];
                    password = data.split(" ")[1];
                    dumpManager.registerUser(username, PasswordManager.hashPassword(password));
                    logger.info("Пользователь = " + username + " успешно добавлен!");
                    commandResult = new Response(STATUS.USERCHECK, "Пользователь успешно добавлен!");
                    userStatusManager.setUserName(username);
                    userStatusManager.setStatus(true);
                    sendResponse(clientChannel, commandResult);
                    break;
                case "checkPassword":
                    data = (String) message.getObject();
                    username = data.split(" ")[0];
                    password = data.split(" ")[1];
                    boolean passwordMatch = dumpManager.checkPassword(username, password);
                    commandResult = new Response(STATUS.USERCHECK, "", passwordMatch);
                    if (passwordMatch) {
                        userStatusManager.setUserName(username);
                        userStatusManager.setStatus(true);
                        logger.info("Пользователь = " + userStatusManager.getUserName() + " успешно вошёл в систему!");
                    } else {
                        logger.info("Пароли не совпадают");
                    }
                    sendResponse(clientChannel, commandResult);
                    break;
                case "logout":
                    logger.info("Пользователь " + userStatusManager.getUserName() + " вышел из системы.");
                    userStatusManager.setUserName("");
                    userStatusManager.setStatus(false);
                    break;
                default:
                    logger.warning("Неизвестная команда проверки пользователя: " + message.getMessage());
                    break;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при обработке проверки пользователя: ", e);
        }
    }

    private void sendResponse(SocketChannel clientChannel, Response response) {
        responseThreadPool.execute(() -> {
            synchronized (clients) {
                ObjectOutputStream oos = clients.get(clientChannel);
                try {
                    logger.info("Отправка ответа клиенту: " + users.get(clientChannel).getUserName());
                    oos.writeObject(response);
                    oos.flush();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Ошибка при отправке ответа клиенту: " + users.get(clientChannel).getUserName(), e);
                }
            }
        });
    }
}
