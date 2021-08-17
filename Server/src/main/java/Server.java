import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Server {
    static ArrayList<User> users = new ArrayList<>();
    static ArrayList<String> userNames = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8188); // Создаём серверный сокет
            System.out.println("Сервер запущен");
            while (true) { // Бесконечный цикл для ожидания подключения клиентов
                Socket socket = serverSocket.accept(); // Ожидаем подключения клиента
                System.out.println("Клиент подключился");
                User currentUser = new User(socket);
                users.add(currentUser);
                DataInputStream in = new DataInputStream(currentUser.getSocket().getInputStream()); // Поток ввода
                ObjectOutputStream oos = new ObjectOutputStream(currentUser.getSocket().getOutputStream()); // Поток вывода
                currentUser.setOos(oos);
                Thread thread = new Thread(() -> {
                    try {
                        currentUser.getOos().writeObject("Добро пожаловать на сервер");
                        currentUser.getOos().writeObject("Введите ваше имя: ");
                        String userName = in.readUTF(); // Ожидаем имя от клиента
                        while (nameIsTaken(userName)) { // Проверка уникальности имени пользователя
                            currentUser.getOos().writeObject("Имя " + userName + " занято. Введите другое: ");
                            userName = in.readUTF();
                        }
                        currentUser.setUserName(userName);
                        userNames.add(currentUser.getUserName());
                        msgHandler(currentUser, (LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm ")) + currentUser.getUserName() + " присоединился к беседе"));
                        System.out.println("Отправляем список пользователей" + userNames);
                        while (true) {
                            String request = in.readUTF(); // Ждём сообщение от пользователя
                            System.out.println(currentUser.getUserName() + ": " + request);
                            if (request.startsWith("/m ")) { //Реализация возможности отправки личного сообщения.
                                commandHandler(request, currentUser);
                            } else {
                                msgHandler(currentUser, (LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm ")) + currentUser.getUserName() + ": " + request));
                            }
                        }
                    } catch (IOException e) {
                        users.remove(currentUser);
                        userNames.remove(currentUser.getUserName());
                        try {
                            msgHandler(currentUser, currentUser.getUserName() + " покинул чат");
                            msgHandler(currentUser, new ArrayList<>(userNames));
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

    private static void msgHandler(User currentUser, Object msg) throws IOException {
        for (User user : users) {
            user.getOos().writeObject(new ArrayList<>(userNames));
            if (users.indexOf(user) == users.indexOf(currentUser)) continue;
            user.getOos().writeObject(msg);
        }
    }

    private static void commandHandler(String request, User currentUser) {
        try {
            String[] command = request.split(" ");
            String toName = command[1];
            StringBuilder message = new StringBuilder();
            for (int i = 2; i < command.length; i++) {
                message.append(command[i]).append(" ");
            }
            if (!nameIsTaken(toName)) {
                currentUser.getOos().writeObject("В чате нет пользователя с именем: " + toName);
            } else if (toName.equals(currentUser.getUserName())) {
                currentUser.getOos().writeObject("Вы хотите отправить сообщение себе");
            } else {
                for (User user : users) {
                    if (user.getUserName().equals(toName)) {
                        user.getOos().writeObject(currentUser.getUserName() + ": " + message);
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
