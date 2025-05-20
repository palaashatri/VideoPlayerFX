module com.example.videoplayerfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires uk.co.caprica.vlcj;
    requires uk.co.caprica.vlcj.javafx;

    opens com.example.videoplayerfx to javafx.fxml;
    opens com.example.videoplayerfx.controller to javafx.fxml;

    exports com.example.videoplayerfx;
}