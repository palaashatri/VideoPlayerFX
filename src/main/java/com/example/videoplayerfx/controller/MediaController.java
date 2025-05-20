package com.example.videoplayerfx.controller;

import com.example.videoplayerfx.util.PlatformUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import com.sun.jna.NativeLibrary;

import java.io.File;

public class MediaController {
    @FXML private ImageView videoView;
    @FXML private Button openFileButton;
    @FXML private Button playPauseButton;
    @FXML private Slider seekSlider;

    private MediaPlayerFactory factory;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    @FXML
    public void initialize() {
        // Configure JNA to find LibVLC libraries
        String libPath = getClass().getResource("/" + PlatformUtils.getLibVlcPath()).getPath();
        NativeLibrary.addSearchPath("vlc", libPath);
        NativeLibrary.addSearchPath("vlccore", libPath);

        // Configure LibVLC with platform-specific path and video output
        String vout = PlatformUtils.isWindows() ? "--vout=direct3d11" : PlatformUtils.isMac() ? "--vout=metal" : "--vout=opengl";
        try {
            factory = new MediaPlayerFactory(new String[]{vout, "--plugin-path=" + libPath + "/plugins"});
            mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();
            mediaPlayer.videoSurface().set(new ImageViewVideoSurface(videoView));
        } catch (Exception e) {
            System.err.println("Failed to initialize LibVLC: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Bind seek slider to media position
        seekSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (seekSlider.isValueChanging()) {
                mediaPlayer.controls().setPosition(newVal.floatValue() / 100);
            }
        });

        // Update slider during playback
        mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
                if (!seekSlider.isValueChanging()) {
                    seekSlider.setValue(newPosition * 100);
                }
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                playPauseButton.setText("Play");
                isPlaying = false;
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                System.err.println("Media playback error");
                playPauseButton.setText("Play");
                isPlaying = false;
            }
        });
    }

    @FXML
    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Video File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mkv", "*.avi", "*.mov", "*.wmv", "*.flv")
        );
        File file = fileChooser.showOpenDialog(videoView.getScene().getWindow());
        if (file != null) {
            // Stop current playback
            if (mediaPlayer.media().status().isPlaying()) {
                mediaPlayer.controls().stop();
            }
            // Reset UI
            seekSlider.setValue(0);
            playPauseButton.setText("Play");
            isPlaying = false;
            // Play new file
            try {
                mediaPlayer.media().play(file.getAbsolutePath());
                isPlaying = true;
                playPauseButton.setText("Pause");
            } catch (Exception e) {
                System.err.println("Error playing file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void togglePlayPause() {
        if (isPlaying) {
            mediaPlayer.controls().pause();
            playPauseButton.setText("Play");
        } else {
            mediaPlayer.controls().play();
            playPauseButton.setText("Pause");
        }
        isPlaying = !isPlaying;
    }
}