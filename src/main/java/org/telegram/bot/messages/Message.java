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

import liketechnik.InfoDisplay.Config;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.XMLBuilderParameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.resolver.CatalogResolver;
import org.apache.commons.configuration2.resolver.DefaultEntityResolver;
import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine;
import org.telegram.bot.database.DatabaseManager;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.logging.BotLogger;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

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

    public static final Path location = FileSystems.getDefault().getPath(
            Message.class.getResource(Message.class.getSimpleName() + ".class").toString());
    public static final Path dtd = FileSystems.getDefault().getPath(location.getParent() + "/language.xsd");

    public static String getStartMessage(User user, boolean userKnown) {

        final String startMessageQuarry = "command_message[@command='start_command']/";

        StringBuilder startMessage = new StringBuilder();

        FileBasedConfigurationBuilder<XMLConfiguration> builder = null;
        XMLConfiguration config = null;

        XMLBuilderParameters params = new Parameters().xml();
        params.setBasePath(location.toString());
        params.setSchemaValidation(true);
        params.setExpressionEngine(new XPathExpressionEngine());

        try {
            builder =
                    new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
                    .configure(params.setFileName(location.getParent().toString() + "/" +
                            DatabaseManager.getInstance().getUserLanguage(user.getId()) + ".xml"));
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
            if (e.getClass() == IllegalArgumentException.class) {
                builder =
                        new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
                                .configure(params.setFileName(location.getParent().toString() + "/english.xml"));
            }
        }

        try {
            config = builder.getConfiguration();
        } catch (ConfigurationException e) {
            BotLogger.error(LOGTAG, e);
            System.exit(2);
        }


         if (userKnown) {
             startMessage.append(config.getString(startMessageQuarry + "case[@case='userKnown']/part[@position=1]")
                     .replaceAll("/n>", "\n"));
             startMessage.append(" ");
             startMessage.append(getFilteredUsername(user));
             startMessage.append(config.getString(startMessageQuarry + "case[@case='userKnown']/part[@position=2]")
                     .replaceAll("/n>", "\n"));
        } else {
             startMessage.append(config.getString(startMessageQuarry + "case[@case='userUnknown']/part[@position=1]")
                     .replaceAll("/n>", "\n"));
             startMessage.append(" ");
             startMessage.append(getFilteredUsername(user));
             startMessage.append(config.getString(startMessageQuarry + "case[@case='userUnknown']/part[@position=2]")
                     .replaceAll("/n>", "\n"));
        }

        return startMessage.toString();
    }
}
