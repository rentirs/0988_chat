package ru.eduprof.sdo.android;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(8188); // Создаём серверный сокет
        System.out.println("Сервер запущен");
        while (true) {  // Бесконечный цикл для ожидания подключения клиентов
            Socket socket = serverSocket.accept(); // Ожидаем подключения клиента
            System.out.println("Клиент подключился");
            new Thread(new ChatHandler(socket)).start();
        }
    }
}

