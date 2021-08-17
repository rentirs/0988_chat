package com.example.chatgui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatApp extends Application {
    private static Stage st;
    @Override
    public void start(Stage stage) throws IOException {
        st = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApp.class.getResource("chatView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Учебный чат");
        Image appIcon = new Image(ChatApp.class.getResourceAsStream("icon.png"));
        stage.getIcons().add(appIcon);
        stage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void changeTitle(String title) {
        st.setTitle(title);
    }
}