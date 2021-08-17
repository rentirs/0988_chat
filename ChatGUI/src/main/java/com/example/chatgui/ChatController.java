package com.example.chatgui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
    Label dateTime;

    @FXML
    TextArea onlineUsers;

    @FXML
    public void initialize() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> dateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. E HH:mm:ss")))),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    @FXML
    private void send() {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            String text = textField.getText();
            out.writeUTF(text);
            textField.clear();
            textField.requestFocus();
            textArea.appendText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm ")) + " Вы: " + text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void connect() {
        try {
            btnConnect.setDisable(true);
            socket = new Socket("localhost", 8188);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ChatApp.changeTitle("Чат подключен");
            Thread thread = new Thread(() -> {
                while (true) {
                    String response = "";
                    ArrayList<String> userNames = new ArrayList<>();
                    try {
                        Object object = ois.readObject();
                        textField.setEditable(true);
                        textField.setFocusTraversable(true);
                        btnSend.setDisable(false);
                        if (object.getClass().equals(userNames.getClass())) {
                            userNames = ((ArrayList<String>) object);
                            System.out.println(userNames);
                            onlineUsers.clear();
                            for (String userName : userNames) {
                                onlineUsers.appendText(userName + "\n");
                            }
                        } else if (object.getClass().equals(response.getClass())) {
                            response = object.toString();
                            textArea.appendText(response + "\n");
                        } else {
                            textArea.appendText("Произошла ошибка");
                        }
                    } catch (IOException | ClassNotFoundException e) {
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