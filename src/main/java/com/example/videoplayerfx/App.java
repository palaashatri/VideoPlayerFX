package com.example.videoplayerfx;

import com.example.videoplayerfx.util.PlatformUtils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        scene.getStylesheets().add(PlatformUtils.getPlatformStylesheet());
        primaryStage.setTitle("Video Player");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Ensure the native libraries are loaded before launching the application
        String libVlcPath = "target/classes/libvlc";
        System.setProperty("jna.library.path", libVlcPath);
        System.setProperty("VLC_PLUGIN_PATH", libVlcPath + "/plugins");
        launch(args);
    }
}