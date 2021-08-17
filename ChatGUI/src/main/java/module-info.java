module com.example.chatgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.chatgui to javafx.fxml;
    exports com.example.chatgui;
}