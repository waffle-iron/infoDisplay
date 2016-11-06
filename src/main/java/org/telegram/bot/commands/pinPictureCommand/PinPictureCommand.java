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
 * along with this program.  If not, see <https://github.com/rubenlagus/TelegramBots>.
 *
 * infoDisplay uses TelegramBots Java API <https://github.com/rubenlagus/TelegramBots> by Ruben Bermudez.
 * TelegramBots API is licensed under GNU General Public License version 3 <https://github.com/rubenlagus/TelegramBots>.
 *
 * infoDisplay uses parts of the Apache Commons project <https://commons.apache.org/>.
 * Apache commons is licensed under the Apache License Version 2.0 <http://www.apache.org/licenses/>.
 */

package org.telegram.bot.commands.pinPictureCommand;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.telegram.bot.Config;
import org.telegram.bot.commands.SendOnErrorOccurred;
import org.telegram.bot.database.DatabaseManager;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import static org.telegram.bot.Main.sendOnErrorOccurred;

/**
 * @author liketechnik
 * @version 1.0
 * @date 27 of Oktober of 2016
 */
public class PinPictureCommand extends BotCommand {

    public static final String LOGTAG = "PINPICTURECOMMAND";

    public PinPictureCommand() {
        super("pin_picture", "Lade ein Bild an das virtuelle Brett hoch.");
    }

    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        SendMessage answerMessage = new SendMessage();

        try {
            DatabaseManager databaseManager = DatabaseManager.getInstance();

            StringBuilder messageBuilder = new StringBuilder();

            if (!databaseManager.getUserRegistrationState(user.getId())) {
                messageBuilder.append("Du bist nicht berechtigt Bilder hochzuladen.").append("\n");
                messageBuilder.append("Benutze /register um als berechtigte Person eingetragen " +
                        "zu werden.").append("\n").append("/help");

                answerMessage.setText(messageBuilder.toString());

                try {
                    absSender.sendMessage(answerMessage);
                } catch (Exception e) {
                    BotLogger.error(LOGTAG, e);
                }

                return;
            }

            databaseManager.setUserCommandState(user.getId(), Config.Bot.PIN_PICTURE_COMMAND_SEND_TITLE);

            messageBuilder.append("Bitte sende mir den Namen deines Bildes.");

            answerMessage.setText(messageBuilder.toString());
            answerMessage.setChatId(user.getId().toString());
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);

            new SendOnErrorOccurred().execute(absSender, user, chat, new String[]{LOGTAG});

            return;
        }

        try {
            absSender.sendMessage(answerMessage);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }

}
