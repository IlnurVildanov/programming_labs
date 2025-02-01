package org.example.managers;

import java.util.logging.Logger;

public class UserStatusManager {
    private static final Logger logger = Logger.getLogger(UserStatusManager.class.getName());

    private boolean status;
    private String userName;

    /**
     * Конструктор для инициализации статуса пользователя.
     *
     * @param status   статус пользователя (вошёл в систему или нет)
     * @param userName имя пользователя
     */
    public UserStatusManager(boolean status, String userName) {
        this.status = status;
        this.userName = userName;
        logger.info("Создан UserStatusManager для пользователя: " + userName + " со статусом: " + status);
    }

    public void setStatus(boolean status) {
        this.status = status;
        logger.info("Статус пользователя " + userName + " изменён на: " + status);
    }

    public void setUserName(String userName) {
        logger.info("Имя пользователя изменено с " + this.userName + " на " + userName);
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public boolean getStatus() {
        return status;
    }
}
