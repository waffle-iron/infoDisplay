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

package org.telegram.bot.commands.pinPictureCommand;

import liketechnik.InfoDisplay.Config;
import org.telegram.bot.commands.SendOnErrorOccurred;
import org.telegram.bot.database.DatabaseManager;
import org.telegram.bot.messages.Message;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * @author Florian Warzecha
 * @version 1.0.1
 * @date 01 of November 2016
 *
 * This command gets executed if a user sent '/pin_picture' and proceeds (sending description and title of the picture).
 */
public class SendDuration extends BotCommand {

    public static final String LOGTAG = "PINPICTURECOMMAND_SENDDURATION";

    /**
     * Set identifier and a short description for the bot.
     */
    public SendDuration() {
        super("send_duration", "Evaluates the answer after a user executed /pin_picture " +
                "(and SendTitle + SendDescription).");
    }

    /**
     * Check if the sent duration is not too low and save it (if it is okay). Then tell the user to
     * send the picture.
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
            String message = arguments[0];

            String displayFileName = databaseManager.getCurrentPictureTitle(user.getId());

            int duration = 15;

            answer.setChatId(user.getId().toString());

            try {
                duration = Integer.parseInt(message);
                if (duration < 1) {
                    throw new NumberFormatException("Duration is too low.");
                }
            } catch (NumberFormatException e) {
                messageBuilder.append(Message.pinPictureCommand.getSendDurationMessage(user, false));
                answer.setText(messageBuilder.toString());

                try {
                    absSender.sendMessage(answer);
                } catch (TelegramApiException e1) {
                    BotLogger.error(LOGTAG, e1);
                }

                return;
            }

            messageBuilder.append(Message.pinPictureCommand.getSendDurationMessage(user, true));

            databaseManager.setCurrentPictureDuration(user.getId(), duration);
            databaseManager.setUserCommandState(user.getId(), Config.Bot.PIN_PICTURE_COMMAND_SEND_PICTURE);

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