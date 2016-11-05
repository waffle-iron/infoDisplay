package org.telegram.bot.commands;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.telegram.bot.Config;
import org.telegram.bot.database.DatabaseManager;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.IOException;

import static org.telegram.bot.Main.getFilteredUsername;
import static org.telegram.bot.Main.sendOnErrorOccurred;

/**
 * @author florian
 * @version 1.0
 * @date 23 of Oktober of 2016
 */
public class StopCommand extends BotCommand {

    public static final String LOGTAG = "STOPCOMMAND";

    public StopCommand() {
        super("stop", "Mit diesem Befehl kannst du den Bot anhalten.");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        SendMessage answer = new SendMessage();

        try {
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            StringBuilder messageBuilder = new StringBuilder();

            String userName = getFilteredUsername(user);

            try {
                databaseManager.setUserState(user.getId(), false);
            } catch (ConfigurationException | IOException e) {
                BotLogger.error(LOGTAG, "Error saving new user state for user: " + user.getId(), e);
            }

            try {
                databaseManager.setUserCommandState(user.getId(), Config.Bot.NO_COMMAND);
            } catch (ConfigurationException e) {
                BotLogger.error(LOGTAG, e);
            }

            messageBuilder.append("Tschüss ").append(userName).append(".\n").append("War 'ne schöne Zeit mit dir.");

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
