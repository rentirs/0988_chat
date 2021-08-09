package ru.eduprof.sdo.android.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8188);
            System.out.println("Успешно подключен");
            DataInputStream in = new DataInputStream(socket.getInputStream()); //Поток ввода
            DataOutputStream out = new DataOutputStream(socket.getOutputStream()); //Поток вывода
            String response = in.readUTF(); //Читаем ответ от сервера
            System.out.println(response);
            Scanner scanner = new Scanner(System.in);
            while(true) {
                String consoleText = scanner.nextLine(); //Ждём сообщение от пользователя
                out.writeUTF(consoleText); //Отправляем сообщение на сервер
                response = in.readUTF(); //Читаем ответ от сервера
                System.out.println("Ответ от сервера: " + response); //Печатаем ответ от сервера
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
