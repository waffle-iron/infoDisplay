package org.telegram.bot.commands.askCommand;

import org.telegram.bot.commands.SendOnErrorOccurred;
import org.telegram.bot.database.DatabaseManager;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import static org.telegram.bot.Main.getSpecialFilteredUsername;

/**
 * @author florian
 * @version 1.0
 * @date 01 of November of 2016
 *
 * This command sends a question to the administrator.
 */
public class WriteQuestion extends BotCommand {

    public static final String LOGTAG = "ASKCOMMAND_WRITEQUESTION";

    /**
     * Set the identifier and a short description of the command.
     */
    public WriteQuestion() {
        super("write_question", "Receive the question from the user, after /ask was executed by him.");
    }

    /**
     * Evaluate the message send by a user.
     * This is done by sending the question of a user to the administrator .
     * @param absSender
     * @param user
     * @param chat
     * @param arguments
     */
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        SendMessage question = new SendMessage();
        SendMessage answer = new SendMessage();

        try {
            String message = arguments[0];

            StringBuilder questionBuilder = new StringBuilder().append(message);
            questionBuilder.append("\n").append("From user ").append(getSpecialFilteredUsername(user))
                    .append(" .");

            DatabaseManager.getInstance().setUserCommandState(user.getId(),
                    org.telegram.bot.Config.Bot.NO_COMMAND);

            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Deine Frage wurde weitergeleitet.");

            question.setChatId(org.telegram.bot.Config.Bot.ADMIN_CHAT_ID.toString());
            question.setText(questionBuilder.toString());
            answer.setChatId(chat.getId().toString());
            answer.setText(messageBuilder.toString());

            DatabaseManager.getInstance().createQuestion(questionBuilder.toString(), chat.getId().longValue());
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);

            new SendOnErrorOccurred().execute(absSender, user, chat, new String[]{LOGTAG});

            return;
        }

        try {
            absSender.sendMessage(question);
            absSender.sendMessage(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}
