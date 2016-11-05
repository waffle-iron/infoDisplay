package org.telegram.bot.commands.askCommand;

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
 * @author florian
 * @version 1.0
 * @date 24 of Oktober of 2016
 *
 * This command lets users ask questions to the administrator of this bot.
 */
public class AskCommand extends BotCommand {

    public final static String LOGTAG = "ASKCOMMAND";

    /**
     * Set the identifier and a short description of this bot.
     */
    public AskCommand() {
        super("ask", "Stelle eine Frage an den Administrator dieses Bots.");
    }

    /**
     * Evaluate the message send by a user.
     * This is done by telling the user to send the question it has.
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

            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Sende mir bitte deine Frage, ich leite sie an den Administrator weiter.");

            databaseManager.setUserCommandState(user.getId(), Config.Bot.ASK_COMMAND_WRITE_QUESTION);

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
