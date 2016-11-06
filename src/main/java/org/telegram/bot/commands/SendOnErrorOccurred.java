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

package org.telegram.bot.commands;

import org.telegram.bot.Config;
import org.telegram.bot.DisplayBot;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * @author liketechnik
 * @version 1.0
 * @date 01 of November of 2016
 *
 * This command gets executed if an error occurs in one of the other commands.
 */
public class SendOnErrorOccurred extends BotCommand {

    public static final String LOGTAG = "SENDONERROROCCURRED";

    /**
     * Send the identifier and a short description.
     */
    public SendOnErrorOccurred() {
        super("send_error_occurred", "This command gets executed when an error happens while executing a command.");
    }

    /**
     * Inform the user that an error occurred and set his command status to {@link Config.Bot#NO_COMMAND}.
     * @param absSender
     * @param user
     * @param chat
     * @param LOGTAG
     */
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] LOGTAG) {

        StringBuilder messageBuilder = new StringBuilder();
        SendMessage answer = new SendMessage();

        messageBuilder.append("Es ist ein interner Fehler aufgetreten, bitte informiere den Administrator dieses " +
                "Bots darüber.");

        answer.setChatId(chat.getId().toString());
        answer.setText(messageBuilder.toString());

        try {
            absSender.sendMessage(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG[0], e);
        }

        new CancelCommand(new DisplayBot().getICommandRegistry()).execute(absSender, user, chat, new String[]{});
    }
}
