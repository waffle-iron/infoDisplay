package org.telegram.bot.commands;

import org.telegram.bot.DisplayBot;
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
 * @date 01 of November of 2016
 */
public class SendOnErrorOccurred extends BotCommand {

    public static final String LOGTAG = "SENDONERROROCCURRED";

    public SendOnErrorOccurred() {
        super("send_error_occurred", "This command gets executed when an error happens while executing a command.");
    }

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
