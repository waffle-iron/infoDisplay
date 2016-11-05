package liketechnik.InfoDisplay;

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * @author florian
 * @version 1.0
 * @date 22 of Oktober of 2016
 */
public final class Config {
    public static final class Keys {
        public static final String DISPLAY_FILES_KEY = "displayFiles";
        public static final String DISPLAY_FILE_DURATION_KEY = "displayDuration";
        public static final String DISPLAY_FILE_TYPE_KEY = "type";
        public static final String DISPLAY_FILE_DESCRIPTION = "description";

        public static final String USER_ACTIVE = "userActive";
        public static final String USER_REGISTERED = "userRegistered";
        public static final String USER_WANTS_REGISTRATION = "userWantsregistration";

        public static final String USER_COMMAND_STATE = "userState";
        public final static String QUESTION = "question";

        public static final String CHAT_ID = "chatID";

        public static final String SELECTED_QUESTION = "selectedQuestion";
        public static final String CURRENT_PICTURE_TITLE = "currentPictureTitle";
    }

    public static final class Bot {
        public static final String DISPLAY_USER = "displaybot";
        public static final String DISPLAY_TOKEN = "292304229:AAE_JG4HhoJnYIbpfxwglf_oIU5V814gqmo";

        public static final Integer ADMIN_CHAT_ID = 195494451;
        public static final Integer ADMIN_USER_ID = 195494451;

        public static final String DISPLAY_FILE_TYPE_IMAGE = "image";
        public static final String HAS_PHOTO = "hasPhoto";
        public static final String HAS_NO_PHOTO = "hasNoPhoto";

        public final static String ASK_COMMAND_WRITE_QUESTION = "askCommand";
        public final static String NO_COMMAND = "none";
        public final static String ANSWER_COMMAND_CHOOSE_NUMBER = "answerCommandChooseNumber";
        public final static String ANSWER_COMMAND_WRITE_ANSWER = "answerCommandWriteAnswer";
        public final static String PIN_PICTURE_COMMAND_SEND_DESCRIPTION =
                "pinPictureCommandSendDescription";
        public static final String PIN_PICTURE_COMMAND_SEND_TITLE =
                "pinPictureCommandSendTitle";
        public final static String PIN_PICTURE_COMMAND_SEND_PICTURE = "pinPictureCommandSendPicture";
        public static final String PIN_PICTURE_COMMAND_SEND_DURATION = "pinPictureCommandSendDuration";
    }

    public static final class Window {
        public static final String windowTitle = "InfoDisplay";
        public static final int posX = 100;
        public static final int posY = 100;
        public static final int width = 600;
        public static final int height = 400;
    }

    public static final class Paths {
        public static final String USER_HOME = java.lang.System.getProperty("user.home");
        public static final Path APP_HOME = FileSystems.getDefault().getPath(USER_HOME + "/.infoDisplay");

        public static final Path DISPLAY_FILES_CONFIG_FILE = FileSystems.getDefault().getPath(APP_HOME + "/displayFiles.conf");
        public static final Path DISPLAY_FILES = FileSystems.getDefault().getPath(APP_HOME + "/displayFiles");

        public static final Path BOT_DATABASE = FileSystems.getDefault().getPath(APP_HOME + "/bot_database");
        public static final Path USER_DATABASE = FileSystems.getDefault().getPath(BOT_DATABASE + "/users");
        public static final Path QUESTION_DATABASE = FileSystems.getDefault().getPath(BOT_DATABASE + "/questions");
    }

}
