package ru.eduprof.sdo.android;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatHandler implements Runnable {
    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;

    public ChatHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("Добро пожаловать на сервер\n");
            while (true) {
                String request = in.readUTF();
                System.out.println("Принято: " + request);
                out.writeUTF(request.toUpperCase());
                System.out.println("Отправлено: " + request.toUpperCase());
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
