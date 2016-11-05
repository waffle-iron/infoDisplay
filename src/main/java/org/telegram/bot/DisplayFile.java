package org.telegram.bot;
/**
 * @author florian
 * @version 1.0
 * @date 24 of September of 2016
 */
public class DisplayFile {

    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;

    private int displayDuration;
    private String type;
    private String fileName;

    public DisplayFile(int displayDuration, String type, String fileName) throws IllegalArgumentException {
        if (type.equals(Config.Bot.DISPLAY_FILE_TYPE_IMAGE)) {
            throw new IllegalArgumentException("No known type: " + type);
        }

        this.displayDuration = displayDuration;
        this.type = type;
        this.fileName = fileName;
    }

    public int getDisplayDuration() {
        return displayDuration;
    }

    public void setDisplayDuration(int displayDuration) {
        this.displayDuration = displayDuration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) throws IllegalArgumentException {
        if (type.equals(Config.Bot.DISPLAY_FILE_TYPE_IMAGE)) {
            throw new IllegalArgumentException("No known type: " + type);
        }
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setName(String fileName) {
        this.fileName = fileName;
    }
}
