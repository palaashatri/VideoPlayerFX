package com.example.videoplayerfx.controller;

import com.example.videoplayerfx.util.PlatformUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import java.io.File;

/**
 * Controller for the media player UI, handling video playback and user interactions.
 */
public class MediaController {
    @FXML private ImageView videoView;
    @FXML private Button openFileButton;
    @FXML private Button playPauseButton;
    @FXML private Slider seekSlider;

    private MediaPlayerFactory factory;
    private EmbeddedMediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    /**
     * Initializes the media player and sets up event listeners.
     */
    @FXML
    public void initialize() {
        // Get the path to the native libraries
        String libPath = getClass().getResource("/" + PlatformUtils.getLibVlcPath()).getPath();
        // Set JNA library path to ensure libvlc and libvlccore are found
        System.setProperty("jna.library.path", libPath);

        // Configure LibVLC with platform-specific video output and plugin path
        String vout = PlatformUtils.isWindows() ? "--vout=direct3d11" : PlatformUtils.isMac() ? "--vout=metal" : "--vout=opengl";
        String pluginPath = libPath + "/plugins";
        try {
            factory = new MediaPlayerFactory(new String[]{vout, "--plugin-path=" + pluginPath});
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

        // Add event listener for media player events
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

            @Override
            public void playing(MediaPlayer mediaPlayer) {
                isPlaying = true;
                playPauseButton.setText("Pause");
            }

            @Override
            public void paused(MediaPlayer mediaPlayer) {
                isPlaying = false;
                playPauseButton.setText("Play");
            }

            @Override
            public void stopped(MediaPlayer mediaPlayer) {
                isPlaying = false;
                playPauseButton.setText("Play");
            }
        });
    }

    /**
     * Opens a file chooser to select a video file and starts playback.
     */
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
            if (mediaPlayer.status().isPlaying()) {
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

    /**
     * Toggles between play and pause based on the current state.
     */
    @FXML
    private void togglePlayPause() {
        if (mediaPlayer.status().isPlaying()) {
            mediaPlayer.controls().pause();
        } else {
            mediaPlayer.controls().play();
        }
    }

    /**
     * Releases media player resources when the application is closed.
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (factory != null) {
            factory.release();
        }
    }
}