package org.telegram.bot.commands;

import org.apache.commons.configuration2.ex.ConfigurationException;
import org.telegram.bot.database.DatabaseManager;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.IOException;

import static org.telegram.bot.Main.sendOnErrorOccurred;

/**
 * @author florian
 * @version 1.0
 * @date 24 of October of 2016
 *
 * This command sends a message back to the user, containing the username of me / the administrator.
 */
public class AdministratorCommand extends BotCommand {

    public static String LOGTAG = "ADMINISTRATORCOMMAND";

    /**
     * Set the identifier for the command and a short description.
     */
    public AdministratorCommand() {
        super("administrator", "Lass dir den Benutzername vom Administrator / Ersteller dieses Bots anzeigen.");
    }

    /**
     * Evaluate the message send by a user.
     * This is done by just sending back the administrator's username.
     * @param absSender
     * @param user
     * @param chat
     * @param arguments
     */
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        SendMessage answer = new SendMessage();

        try {
            DatabaseManager.getInstance().setUserState(user.getId(), true);

            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Ersteller / Administrator dieses Bots ist @liketechnik2000.");
            messageBuilder.append("\n").append("/help");

            answer.setChatId(chat.getId().toString());
            answer.setText(messageBuilder.toString());
        // catch every error that could occur, log it and inform the user about the occurrence of an error.
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);

            new SendOnErrorOccurred().execute(absSender, user, chat, new String[]{LOGTAG});

            return;
        }
        // Send the message
        try {
            absSender.sendMessage(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}
