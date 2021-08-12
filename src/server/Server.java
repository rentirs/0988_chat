package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    static ArrayList<User> users = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8188); // Создаём серверный сокет
            System.out.println("Сервер запущен");
            while (true) { // Бесконечный цикл для ожидания родключения клиентов
                Socket socket = serverSocket.accept(); // Ожидаем подключения клиента
                System.out.println("Клиент подключился");
                User currentUser = new User(socket);
                users.add(currentUser);
                DataInputStream in = new DataInputStream(currentUser.getSocket().getInputStream()); // Поток ввода
                DataOutputStream out = new DataOutputStream(currentUser.getSocket().getOutputStream()); // Поток вывода
                Thread thread = new Thread(() -> {
                    try {
                        out.writeUTF("Добро пожаловать на сервер");
                        out.writeUTF("Введите ваше имя: ");
                        String userName = in.readUTF(); // Ожидаем имя от клиента
                        while (nameIsTaken(userName)) { // Проверка уникальности имени пользователя
                            out.writeUTF("Имя " + userName + " занято. Введите другое: ");
                            userName = in.readUTF();
                        }
                        currentUser.setUserName(userName);
                        msgHandler(currentUser, " присоединился к беседе");
                        while (true) {
                            String request = in.readUTF(); // Ждём сообщение от пользователя
                            System.out.println(currentUser.getUserName() + ": " + request);

                            if (request.startsWith("/m")) { //Реализация возможности отправки личного сообщения.
                                commandHandler(request, currentUser, out);
                            } else {
                                msgHandler(currentUser, ": " + request);
                            }
                        }
                    } catch (IOException e) {
                        users.remove(currentUser);
                        try {
                            msgHandler(currentUser, " покинул чат");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void msgHandler(User currentUser, String msg) throws IOException {
        for (User user : users) {
            if(users.indexOf(user) == users.indexOf(currentUser)) continue;
            DataOutputStream out1 = new DataOutputStream(user.getSocket().getOutputStream());
            out1.writeUTF(currentUser.getUserName() + msg);
        }
    }

    private static void commandHandler(String request, User currentUser, DataOutputStream out) {
        try {
            String[] command = request.split(" ");
            String toName = command[1];
            StringBuilder message = new StringBuilder();
            for (int i = 2; i < command.length; i++) {
                message.append(command[i]).append(" ");
            }
            if (!nameIsTaken(toName)) {
                out.writeUTF("В чате нет пользователя с именем: " + toName);
            } else if (toName.equals(currentUser.getUserName())) {
                out.writeUTF("Вы хотите отправить сообщение себе");
            } else {
                for (User user : users) {
                    if (user.getUserName().equals(toName)) {
                        DataOutputStream out1;
                        out1 = new DataOutputStream(user.getSocket().getOutputStream());
                        out1.writeUTF(currentUser.getUserName() + ": " + message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean nameIsTaken(String userName) {
        for (User user : users) {
            if (userName.equals(user.getUserName())) {
                return true;
            }
        }
        return false;
    }
}