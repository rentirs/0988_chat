package ru.eduprof.sdo.android.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        {
            try {
                ServerSocket serverSocket = new ServerSocket(8188); // Создаём серверный сокет
                System.out.println("Сервер запущен");
                while (true){  // Бесконечный цикл для ожидания подключения клиентов
                    Socket socket = serverSocket.accept(); // Ожидаем подключения клиента
                    System.out.println("Клиент подключился");
                    DataInputStream in = new DataInputStream(socket.getInputStream()); // Поток ввода
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // Поток вывода
                    out.writeUTF("Добро пожаловать на сервер");
                    while (true) {
                        String request = in.readUTF(); // Ждём сообщение от пользователя
                        out.writeUTF(request.toUpperCase()); // Отправляем сообщение пользователю
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
