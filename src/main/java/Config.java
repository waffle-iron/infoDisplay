/*
 * Copyright (C) 2016  liketechnik <flowa2000@gmail.com>
 *
 * This file is part of infoDisplay.
 *
 * infoDisplay is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * infoDisplay is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * infoDisplay uses TelegramBots Java API <https://github.com/rubenlagus/TelegramBots> by Ruben Bermudez.
 * TelegramBots API is licensed under GNU General Public License version 3 <https://www.gnu.org/licenses/gpl-3.0.de.html>.
 *
 * infoDisplay uses parts of the Apache Commons project <https://commons.apache.org/>.
 * Apache commons is licensed under the Apache License Version 2.0 <http://www.apache.org/licenses/>.
 *
 * infoDisplay uses vlcj library <http://capricasoftware.co.uk/#/projects/vlcj>.
 * vlcj is licensed under GNU General Public License version 3 <https://www.gnu.org/licenses/gpl-3.0.de.html>.
 *
 * Thanks to all the people who contributed to the projects that make this
 * program possible.
 */

//packageStatement*

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * @author Florian Warzecha
 * @version 1.0.1
 * @date 22 of October of 2016
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
        public static final String USER_LANGUAGE = "userLanguage";

        public final static String QUESTION = "question";

        public static final String CHAT_ID = "chatID";

        public static final String SELECTED_QUESTION = "selectedQuestion";
        public static final String CURRENT_PICTURE_TITLE = "currentPictureTitle";
        public static final String CURRENT_PICTURE_DESCRIPTION = "currentPictureDescription";
        public static final String CURRENT_PICTURE_DURATION = "currentPictureDuration";

        public static final String BOT_USERNAME_KEY = "botUsername";
        public static final String BOT_TOKEN_KEY = "botToken";

        public static final String BOT_ADMIN_USER_ID_KEY = "botAdminUserId";
        public static final String BOT_ADMIN_CHAT_ID_KEY = "botAdminChatId";
    }

    public static final class Bot {

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

        public static final Path BOT_CONFIG_FILE = FileSystems.getDefault().getPath(APP_HOME + "/bot.conf");
    }

    public static final class Languages {
        public static final String ENGLISH = "en";
        public static final String GERMAN = "ger";

        public static final String NONE = "none";
    }

    public static final class registerCommandIfClauses {
        public static final String alreadyRegisterd = "alreadyRegistered";
        public static final String registrationRequestSent = "registrationRequestSent";
        public static final String sendRegistrationRequest = "sendRegistrationRequest";
        public static final String toAdmin = "toAdmin";
    }
}
