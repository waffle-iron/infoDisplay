package org.telegram.bot.commands.answerCommand;

import liketechnik.InfoDisplay.Config;
import org.telegram.bot.commands.SendOnErrorOccurred;
import org.telegram.bot.database.DatabaseManager;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * @author florian
 * @version 1.0
 * @date 01 of November 2016
 *
 * This command handles the answer of a chosen question.
 */
public class WriteAnswer extends BotCommand {

    public static final String LOGTAG = "ANSWERCOMMAND_WRITEANSWER";

    /**
     * Set the identifier and a short description of the command.
     */
    public WriteAnswer() {
        super("write_answer", "This command evaluates the answer after a user executed /answer (and ChooseNumber).");
    }

    /**
     * Evaluate the message send by a user.
     * This is done by taking the message that contains the answer and send it to the user who asked the question.
     * @param absSender
     * @param user
     * @param chat
     * @param arguments
     */
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        SendMessage answer = new SendMessage();
        SendMessage confirmation = new SendMessage();

        String message = arguments[0];

        try {

            DatabaseManager databaseManager = DatabaseManager.getInstance();

            StringBuilder messageBuilder = new StringBuilder();

            int selectedQuestion = databaseManager.getSelectedQuestion(user.getId());

            messageBuilder.append("Deine Frage war: ").append("\n");
            messageBuilder.append(databaseManager.getQuestion(selectedQuestion - 1)).append("\n\n");
            messageBuilder.append("Die Antwort: ").append("\n");
            messageBuilder.append(message);

            StringBuilder confirmationBuilder = new StringBuilder();
            confirmationBuilder.append("Antwort erfolgreich gesendet.").append("\n").append("/help");

            answer.setChatId(databaseManager.getQuestionChatID(selectedQuestion - 1).toString());
            answer.setText(messageBuilder.toString());

            confirmation.setChatId(Config.Bot.ADMIN_CHAT_ID.toString());
            confirmation.setText(confirmationBuilder.toString());

            databaseManager.deleteQuestion(selectedQuestion - 1);
            databaseManager.setSelectedQuestion(user.getId(), -1);


        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);

            new SendOnErrorOccurred().execute(absSender, user, chat, new String[]{LOGTAG});

            return;
        }

        try {
            absSender.sendMessage(confirmation);
            absSender.sendMessage(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}