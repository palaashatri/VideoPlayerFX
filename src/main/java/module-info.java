module com.example.videoplayerfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.videoplayerfx to javafx.fxml;
    exports com.example.videoplayerfx;
}