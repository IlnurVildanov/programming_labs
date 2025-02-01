package org.example.launcher;

import org.example.commandManager.CommandManager;
import org.example.managers.UserStatusManager;
import org.example.response.Response;
import org.example.response.STATUS;

import java.util.logging.Logger;

public class LaunchCommand {
    private static final Logger logger = Logger.getLogger(LaunchCommand.class.getName());
    private final CommandManager commandManager;

    /**
     * Конструктор принимает менеджер команд для выполнения пользовательских команд.
     *
     * @param commandManager экземпляр CommandManager
     */
    public LaunchCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
        logger.info("LaunchCommand инициализирован.");
    }

    /**
     * Разбирает пользовательский ввод и выполняет соответствующую команду.
     *
     * @param user_input        ввод пользователя
     * @param object            объект, передаваемый команде (если требуется)
     * @param userStatusManager статус пользователя
     * @return результат выполнения команды
     */
    public Response commandParser(String user_input, Object object, UserStatusManager userStatusManager) {
        String[] parsedResult = (user_input.trim() + " ").split(" ", 2);
        String commandName = parsedResult[0].trim();
        String commandArgs = parsedResult[1].trim();
        logger.info("Получена команда: " + commandName + " с аргументами: " + commandArgs);
        return doCommand(commandName, commandArgs, object, userStatusManager);
    }

    /**
     * Выполняет команду с заданными аргументами и объектом.
     *
     * @param commandName       имя команды
     * @param commandArgs       аргументы команды
     * @param object            объект для команды
     * @param userStatusManager статус пользователя
     * @return результат выполнения команды
     */
    public Response doCommand(String commandName, String commandArgs, Object object, UserStatusManager userStatusManager) {
        if (commandName.isEmpty()) {
            logger.warning("Пустая команда.");
            return new Response(STATUS.ERROR, "Вы ничего не ввели");
        }
        var command = commandManager.getCommands().get(commandName);
        if (command == null) {
            logger.warning("Команда не найдена: " + commandName);
            return new Response(STATUS.ERROR, "Такой команды не существует");
        }
        logger.info("Выполнение команды: " + commandName);
        return command.execution(commandArgs, object, userStatusManager);
    }
}
