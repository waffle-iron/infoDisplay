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
 * TelegramBots API is licensed under GNU General Public License version 3 <https://github.com/rubenlagus/TelegramBots>.
 *
 * infoDisplay uses parts of the Apache Commons project <https://commons.apache.org/>.
 * Apache commons is licensed under the Apache License Version 2.0 <http://www.apache.org/licenses/>.
 */

package org.telegram.bot.commands.pinPictureCommand;

import liketechnik.InfoDisplay.Config;
import org.apache.commons.io.FileUtils;
import org.telegram.bot.commands.SendOnErrorOccurred;
import org.telegram.bot.database.DatabaseManager;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * @author liketechnik
 * @version 1.0.1
 * @date 01 of November 2016
 *
 * This command gets executed if a user sent '/pin_picture' and followed the process (sending title, description and
 * duration).
 */
public class SendPicture extends BotCommand {

    public static final String LOGTAG = "PINPICTURECOMMAND_SENDPICTURE";

    /**
     * Set the identifier and a short description for the command.
     */
    public SendPicture() {
        super("send_picture", "Saves the photo send by a user after he executed /pin_picture (and SendTitle, SendDescription and SendDuration).");
    }

    /**
     * Evaluate the message of a user.
     * This is done by downloading the photo and adding the file to the list of files that are displayed.
     * @param absSender
     * @param user
     * @param chat
     * @param arguments
     */
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        SendMessage answer = new SendMessage();

        try {

            DatabaseManager databaseManager = DatabaseManager.getInstance();

            StringBuilder messageBuilder = new StringBuilder();

            if (arguments[0].equals(Config.Bot.HAS_PHOTO)) {
                String displayFileName = databaseManager.getCurrentPictureTitle(user.getId());

                GetFile getFileRequest = new GetFile();
                getFileRequest.setFileId(arguments[1]);

                File file = absSender.getFile(getFileRequest);
                URL fileUrl = new URL(
                        "https://api.telegram.org/file/bot" +
                                DatabaseManager.getInstance().getBotToken() + "/" + file.getFilePath());
                Path image = FileSystems.getDefault().getPath(Config.Paths.DISPLAY_FILES + "/" + displayFileName);

                FileUtils.copyURLToFile(fileUrl, image.toFile(), 10000, 10000);

                databaseManager.setDisplayFileType(displayFileName, Config.Bot.DISPLAY_FILE_TYPE_IMAGE);
                databaseManager.createDisplayFile(displayFileName);

                messageBuilder.append("Du hast ein neues Bild an das Brett hochgeladen.").append("\n").append("/help");

                databaseManager.setUserCommandState(user.getId(), Config.Bot.NO_COMMAND);
            } else {
                messageBuilder.append("Sende mir bitte ein Bild.");
            }

            answer.setChatId(chat.getId().toString());
            answer.setText(messageBuilder.toString());
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);

            new SendOnErrorOccurred().execute(absSender, user, chat, new String[]{LOGTAG});

            return;
        }

        try {
            absSender.sendMessage(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}