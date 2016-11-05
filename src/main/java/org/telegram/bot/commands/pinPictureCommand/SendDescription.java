package org.telegram.bot.commands.pinPictureCommand;

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
//TODOC add javadoc /documentation
/**
 * @author florian
 * @version 1.0
 * @date 01 of November 2016
 */
public class SendDescription extends BotCommand {

    public static final String LOGTAG = "PINPICTURECOMMAND_SENDDESCRIPTION";

    public SendDescription() {
        super("send_description", "Evaluates the answer after a user executed /pin_picture (and SendTitle).");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        SendMessage answer = new SendMessage();

        try {

            DatabaseManager databaseManager = DatabaseManager.getInstance();

            StringBuilder messageBuilder = new StringBuilder();

            String message = arguments[0];
            String displayFileName = databaseManager.getCurrentPictureTitle(user.getId());

            databaseManager.setDisplayFileDescription(displayFileName, message);

            messageBuilder.append("Sende mir nun die Anzeigedauer des Bildes.");

            databaseManager.setUserCommandState(user.getId(), Config.Bot.PIN_PICTURE_COMMAND_SEND_DURATION);

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