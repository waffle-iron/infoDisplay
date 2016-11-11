/*
 * Copyright (C) 2016  Florian Warzecha <flowa2000@gmail.com>
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

package org.telegram.bot.messages;

import org.telegram.bot.Config;
import org.telegram.bot.Main;
import org.telegram.bot.database.DatabaseManager;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.logging.BotLogger;

import static org.telegram.bot.Main.getFilteredUsername;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 09 of November of 2016
 *
 * Get messages in a users foreign language.
 */
public class Message {

    public static final String LOGTAG = "MESSAGE";

    public String getStartMessage(User user) {
        StringBuilder startMessage = new StringBuilder();

        try {
            if (DatabaseManager.getInstance().getUserLanguage(user.getId()).equals(Config.Languages.ENGLISH)) {
                startMessage.append(English.START_COMMAND_1);
            } else if (DatabaseManager.getInstance().getUserLanguage(user.getId()).equals(Config.Languages.GERMAN)) {
                startMessage.append(German.START_COMMAND_1);
            }

            return startMessage.toString();
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);

            startMessage.append(English.START_COMMAND_1);
            startMessage.append(getFilteredUsername(user));
            startMessage.append(English.START_COMMAND_2);

            startMessage.append(German.START_COMMAND_1);
            startMessage.append(getFilteredUsername(user));
            startMessage.append(German.START_COMMAND_2);
            try {
                if (DatabaseManager.getInstance().getUserLanguage(user.getId()).equals(Config.Languages.NONE)) {
                    startMessage.append("\n\n");
                    startMessage.append(English.SET_LANGUAGE_PREFERENCE);
                    startMessage.append("\n");
                    startMessage.append(German.SET_LANGUAGE_PREFERENCE);
                }
            } catch (Exception e1) {
                BotLogger.error(LOGTAG, e);
            }

            return startMessage.toString();
        }
    }
}
