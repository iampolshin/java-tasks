package ru.mail.polis.homework.exception;

import java.net.ConnectException;

/**
 * Задание: Нужно создать свою мини библиотеку, с удаленным роботом и пультом управления.
 * Каждый класс оценивается отдельно
 * <p>
 * Пункт управления роботами. Через него управляются все роботы
 * <p>
 * 4 тугрика
 */
public class RobotRemoteControl {

    private RobotConnectionManager connectionManager;
    private Robot robot;

    /**
     * Метод должен открыть соединение и отправить робота в указанную точку. При неудаче - повторить действие еще 2 раза,
     * Если это не удалось, то прокинуть эту ошибку на уровень выше.
     * Попытка считается успешной, если соединение открылось и вызвался метод moveRobotTo без исключений.
     */
    public void moveTo(int robotId, int toX, int toY) throws ConnectException {
        connectionManager = new ConnectionManager();
        for (int retries = 0; ; retries++) {
            try {
                connectionManager.getConnection(robotId).moveRobotTo(toX, toY);
                robot = new Robot(robotId, toX, toY);
                break;
            } catch (ConnectException e) {
                if (retries > 1) {
                    throw e;
                }
            }
        }
    }

    public Robot getRobot() {
        return robot;
    }
}
