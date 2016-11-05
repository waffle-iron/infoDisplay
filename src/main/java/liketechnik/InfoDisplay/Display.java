package liketechnik.InfoDisplay;

import ch.qos.logback.classic.Level;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.embedded.DefaultAdaptiveRuntimeFullScreenStrategy;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author florian
 * @version 1.0
 * @date 24 of September of 2016
 */
public class Display {

    static liketechnik.InfoDisplay.DisplayFile[] displayFiles;

    private final JFrame frame;
    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;

    private final static ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(
            Display.class);

    public static void main (String args[]) {
        logger.setLevel(Level.DEBUG);

        logger.info("Starting initialization process...");

        if (Files.notExists(Config.Paths.APP_HOME)) {
            logger.debug("App directory does not exist yet, creating directory '" + Config.Paths.APP_HOME);

            try {
                Files.createDirectory(Config.Paths.APP_HOME);
            } catch (IOException e) {
                logger.error("Creating of application's home directory failed. The error was: {}", e);
                System.exit(2);
            }
        }

        logger.debug("Searching for native vlc library.");
        boolean nativeLibraryFound = new NativeDiscovery().discover();
        if (!nativeLibraryFound) {
            logger.error("No native vlc library found. Terminating.");
            System.exit(1);
        }
        else {
            logger.trace("Found native vlc library.");
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Display();
            }
        });
    }

    private Display() {
        logger.setLevel(Level.INFO);

        logger.debug("Initializing JFrame.");
        logger.trace("Creating new JFrame with title {}", Config.Window.windowTitle);
        frame = new JFrame(Config.Window.windowTitle);
        logger.trace("Setting window bounds.");
        frame.setBounds(Config.Window.posX, Config.Window.posY, Config.Window.width, Config.Window.height);
        logger.trace("Setting default close operation.");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        logger.trace("Adding window listener");
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logger.info("User closed window. Exiting now...");
                logger.trace("Freeing media player component resources.");
                mediaPlayerComponent.release();
                logger.trace("Calling System.exit");
                System.exit(0);
            }
        });
        logger.debug("Making media player window visible.");
        logger.trace("Creating media player component.");
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    mediaPlayerComponent.getMediaPlayer().toggleFullScreen();
                    logger.info("Toggled fullscreen mode to: {}", mediaPlayerComponent.getMediaPlayer().isFullScreen());
                }
            }
        };
        mediaPlayerComponent.getMediaPlayer().setFullScreenStrategy(new DefaultAdaptiveRuntimeFullScreenStrategy(frame));

        logger.trace("Adding media player component to window.");
        frame.setContentPane(mediaPlayerComponent);
        logger.trace("Calling set visible on the window.");
        frame.setVisible(true);

        logger.info("Initialization finished.");
        logger.info("Loading files to display...");


        displayFiles = loadDisplayFiles();

        logger.info("Finished loading files.");
        logger.info("Showing loaded files now...");

        new showFiles().start(mediaPlayerComponent);
    }

    static liketechnik.InfoDisplay.DisplayFile[] loadDisplayFiles() {
        Configuration displayFileConfig = null;
        Configuration config = null;
        String[] displayFilesToLoad;
        int processedFiles = 0;

        logger.setLevel(Level.INFO);

        logger.debug("(Re)loading display files");

        if (Files.notExists(Config.Paths.DISPLAY_FILES_CONFIG_FILE)) {
            logger.error("Did not find config file for displayFiles.");
        }

        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setPath(Config.Paths.DISPLAY_FILES_CONFIG_FILE.toString()));

        try {
            config = builder.getConfiguration();
        } catch (ConfigurationException e) {
            logger.error("Error while getting configuration for displayFiles. The error was: {}", e);
            System.exit(3);
        }

        if (!config.containsKey(Config.Keys.DISPLAY_FILES_KEY)) {
            logger.error("Config file does not contain any information about media.");
            System.exit(4);
        }

        displayFilesToLoad = config.getStringArray(Config.Keys.DISPLAY_FILES_KEY);

        Display.displayFiles = new liketechnik.InfoDisplay.DisplayFile[displayFilesToLoad.length];
        for (String displayFile : displayFilesToLoad) {
            if (Files.notExists(FileSystems.getDefault().getPath(Config.Paths.DISPLAY_FILES + "/" + displayFile + ".conf"))) {
                logger.error("Did not find config file for displayFile {}.conf in directory {}.", displayFile,
                        Config.Paths.DISPLAY_FILES.toString());
            }

            FileBasedConfigurationBuilder<FileBasedConfiguration> displayFileBuilder  =
                    new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                            .configure(params.properties()
                                    .setPath(Config.Paths.DISPLAY_FILES.toString() + "/" + displayFile + ".conf"));

            try {
                displayFileConfig = displayFileBuilder.getConfiguration();
            } catch (ConfigurationException e) {
                logger.error("Error while getting configuration for displayFile {}. The error was: {}", displayFile, e);
                System.exit(3);
            }

            if (!displayFileConfig.containsKey(Config.Keys.DISPLAY_FILE_DURATION_KEY) ||
                    !displayFileConfig.containsKey(Config.Keys.DISPLAY_FILE_TYPE_KEY)) {
                logger.error("Config file for displayFile {} seems corrupted.", displayFile);
                System.exit(4);
            }

            Display.displayFiles[processedFiles] = new liketechnik.InfoDisplay.DisplayFile(
                    displayFileConfig.getInt(Config.Keys.DISPLAY_FILE_DURATION_KEY),
                        displayFileConfig.getString(Config.Keys.DISPLAY_FILE_TYPE_KEY),
                    Config.Paths.DISPLAY_FILES.toString() + "/" + displayFile);

            processedFiles++;
        }

        //Display.displayFiles = new DisplayFile[2];
        //Display.displayFiles[0] = new DisplayFile(10, DisplayFile.TYPE_IMAGE, "IMAGE.JPG");
        //Display.displayFiles[1] = new DisplayFile(5, DisplayFile.TYPE_IMAGE, "IMAGE2.JPG");

        return Display.displayFiles;
    }
}

class showFiles extends Thread {

    EmbeddedMediaPlayerComponent mediaPlayerComponent;
    final static ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(
            showFiles.class);

    public void start(EmbeddedMediaPlayerComponent mediaPlayerComponent) {
        this.mediaPlayerComponent = mediaPlayerComponent;
        logger.setLevel(Level.INFO);

        super.start();
    }

    public void run() {
        while (true) {
            for (DisplayFile displayFile : Display.displayFiles ) {
                this.logger.debug("Adding media to window.");

                String location = displayFile.getFileName();

                this.mediaPlayerComponent.getMediaPlayer().playMedia(location, ":image-duration=-1");
                while (this.mediaPlayerComponent.getMediaPlayer().getTime() < displayFile.getDisplayDuration() * 1000) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        this.logger.error("Encountered interruption during sleep time of the thread. " +
                                "Exception was: {}", e);
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                this.logger.error("Encountered interruption during sleep time of the thread. " +
                        "Exception was: {}", e);
            }

            Display.displayFiles = Display.loadDisplayFiles();
        }
    }
}
