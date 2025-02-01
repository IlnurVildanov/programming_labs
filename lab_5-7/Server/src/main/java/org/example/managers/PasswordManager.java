package org.example.managers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class PasswordManager {
    private static final Logger logger = Logger.getLogger(PasswordManager.class.getName());

    /**
     * Хэширует пароль с использованием алгоритма SHA-384.
     *
     * @param pswd исходный пароль
     * @return хэшированный пароль в виде шестнадцатеричной строки
     */
    public static String hashPassword(String pswd) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] inputBytes = pswd.getBytes();
            byte[] hashedBytes = digest.digest(inputBytes);

            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                stringBuilder.append(String.format("%02x", b));
            }
            logger.info("Пароль успешно хэширован.");
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.severe("Алгоритм хэширования SHA-384 не найден.");
            throw new RuntimeException(e);
        }
    }
}
