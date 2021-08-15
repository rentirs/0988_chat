package com.example.gui_chat;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatController {
    Socket socket;

    @FXML
    TextField textField;

    @FXML
    TextArea textArea;

    @FXML
    Button btnConnect;

    @FXML
    Button btnSend;

    @FXML
    private void send() {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            String text = textField.getText();
            out.writeUTF(text);
            textField.clear();
            textField.requestFocus();
            textArea.appendText("Вы: " + text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @FXML
    private void connect() {
        try {
            btnConnect.setDisable(true);
            socket = new Socket("152.70.168.210", 8188);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            ChatApp.changeTitle("Чат подключен");
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        String response = in.readUTF();
                        textArea.appendText(response + "\n");
                        textField.setEditable(true);
                        textField.setFocusTraversable(true);
                        btnSend.setDisable(false);
                    } catch (IOException e) {
                        textArea.appendText("ОШИБКА! Потеряно подключение! Подключитесь снова");
                        btnConnect.setDisable(false);
                        ChatApp.changeTitle("Чат отключен");
                        e.printStackTrace();
                        break;
                    }
                }
            });
            thread.start();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно подключиться к серверу", ButtonType.OK);
            btnConnect.setDisable(false);
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}