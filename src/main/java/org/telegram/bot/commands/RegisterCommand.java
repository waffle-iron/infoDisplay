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

package org.telegram.bot.commands;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.output.StringBuilderWriter;
import org.telegram.bot.Config;
import org.telegram.bot.database.DatabaseManager;
import org.telegram.bot.messages.Message;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.IOException;

import static org.telegram.bot.Main.getSpecialFilteredUsername;

/**
 * @author Florian Warzecha
 * @version 1.0.1
 * @date 24 of October of 2016
 *
 * This commands gets executed if a user sends '/register'.
 */
public class RegisterCommand extends BotCommand {

    public static final String LOGTAG = "REGISTERCOMMAND";

    /**
     * Set the identifier and short description.
     */
    public RegisterCommand() {
        super("register", "Register as a valid user of this bot for using it properly.");
    }

    /**
     * Check if the user is registered, asked for registration yet or is now asking for registration, save his status
     * and tell him about it.
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
            databaseManager.setUserState(user.getId(), true);

            String message;

            if (databaseManager.getUserRegistrationState(user.getId())) {
                message = Message.getRegisterMessage(user, Config.registerCommandIfClauses.alreadyRegisterd);
            } else {
                if (databaseManager.getUserWantsRegistrationState(user.getId())) {
                    message = Message.getRegisterMessage(user, Config.registerCommandIfClauses.registrationRequestSent);
                } else {
                    databaseManager.setUserWantsRegistrationState(user.getId(), true);

                    message = Message.getRegisterMessage(user, Config.registerCommandIfClauses.sendRegistrationRequest);

                    SendMessage request = new SendMessage();
                    request.setChatId(databaseManager.getAdminChatId().toString());
                    request.setText(Message.getRegisterMessage(user, Config.registerCommandIfClauses.toAdmin));

                    absSender.sendMessage(request);
                }
            }

            answer.setChatId(chat.getId().toString());
            answer.setText(message);
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
