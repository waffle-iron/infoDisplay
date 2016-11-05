package org.telegram.bot.commands.answerCommand;

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

import javax.xml.crypto.Data;

import static org.telegram.bot.Main.sendOnErrorOccurred;

/**
 * @author florian
 * @version 1.0
 * @date 25 of October of 2016
 *
 * This command gets executed if  a user sends '/answer' to the bot and lets the administrator answer asked questions.
 */
public class AnswerCommand extends BotCommand {

    public final String LOGTAG = "ANSWERCOMMAND";

    /**
     * Set the identifier for the command and a short description.
     */
    public AnswerCommand() {
        super("answer", "Antworte auf Fragen der Nutzer.");
    }

    /**
     * Evaluate a message send by a user.
     * This is done by sending the user all questions that has not been answered yet and asking him to choose one.
     * @param absSender
     * @param user
     * @param chat
     * @param arguments
     */
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        StringBuilder messageBuilder = new StringBuilder();
        SendMessage answer = new SendMessage();

        try {
            if (!user.getId().equals(Config.Bot.ADMIN_USER_ID)) {
                return;
            }

            DatabaseManager databaseManager = DatabaseManager.getInstance();

            messageBuilder.append("Folgende Nachrichten sind zu beantworten: ").append("\n");

            int questions = 1;

            for (String question : databaseManager.getQuestions()) {
                messageBuilder.append(questions).append(". ").append(question).append("\n");
                questions++;
            }

            messageBuilder.append("\n");
            messageBuilder.append("Welche Frage möchtest du beantworten (Nummer)?");

            answer.setChatId(chat.getId().toString());
            answer.setText(messageBuilder.toString());

            databaseManager.setUserCommandState(user.getId(), Config.Bot.ANSWER_COMMAND_CHOOSE_NUMBER);
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
