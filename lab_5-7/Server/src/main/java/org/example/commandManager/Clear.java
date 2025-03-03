package org.example.commandManager;

import org.example.collectionManager.CollectionManager;
import org.example.managers.UserStatusManager;
import org.example.model.Vehicle;
import org.example.response.*;
import org.example.response.Response;

import java.util.logging.Logger;
import java.util.Iterator;

import static org.example.collectionManager.IDManager.RemoveId;

/**
 * Clear - очищает коллекцию
 */
public class Clear extends Command{
    private final CollectionManager collectionManager;
    private final Logger logger;
    public Clear(CollectionManager collectionManager, CommandManager commandManager, Logger logger){
        super("clear", "очистить коллекцию");
        commandManager.addCommandList(getName(), getDescription());
        this.collectionManager = collectionManager;
        this.logger = logger;
    }
    /**
     * Выполнение команды
     * @return Успешность выполнения команды и сообщение об успешности.
     */
    @Override
    public Response execution(String args, Object object, UserStatusManager userStatusManager){
        if (!userStatusManager.getStatus()){
            return new Response(STATUS.OK, "Войдите в аккаунт!");
        }
        if ((args == null || args.isEmpty())){
            logger.info(userStatusManager.getUserName() + " -> " + super.getName());
            if (!collectionManager.getCollection().isEmpty()) {
                Iterator<Vehicle> iterator = collectionManager.getCollection().iterator();
                while (iterator.hasNext()) {
                    Vehicle vehicle = iterator.next();
                    if (vehicle.getUserName().equals(userStatusManager.getUserName())){
                        RemoveId((int) vehicle.getId());
                        iterator.remove();
                    }
                }
                return new Response(STATUS.OK,"Коллекция успешно очищена");
            }
            else {return new Response(STATUS.OK, "Коллекция уже очищена(");}
        }
        else {
            logger.warning(userStatusManager.getUserName() + " -> " + "Неправильное количество аргументов!");
            return new Response(STATUS.ERROR,
                    "Неправильное количество аргументов!");
        }
    }
}
