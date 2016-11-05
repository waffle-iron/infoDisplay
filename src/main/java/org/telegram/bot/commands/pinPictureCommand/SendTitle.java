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

import java.nio.file.FileAlreadyExistsException;

/**
 * @author florian
 * @version 1.0
 * @date 01 of November 2016
 *
 * This class handles an incoming message containing the title for a new picture.
 */
public class SendTitle extends BotCommand {

    public static final String LOGTAG = "PINPICTURECOMMAND_SENDTITLE";

    /**
     * Set the identifier for the command and a short description.
     */
    public SendTitle() {
        super("send_title", "Evaluates the answer after a user executed /pin_picture.");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        SendMessage answer = new SendMessage();

        try {

            DatabaseManager databaseManager = DatabaseManager.getInstance();

            StringBuilder messageBuilder = new StringBuilder();

            String message = arguments[0];

            String displayFileName;
            displayFileName = message + ".jpg";

            messageBuilder.append("Sende mir nun bitte eine (kurze) Beschreibung / Überschrift für das Bild.");

            try {
                databaseManager.createConfigurationFile(databaseManager.getDatabaseDisplayFilePath(displayFileName));
                databaseManager.setCurrentPictureTitle(user.getId(), displayFileName);
                databaseManager.setUserCommandState(user.getId(), Config.Bot.PIN_PICTURE_COMMAND_SEND_DESCRIPTION);
            } catch (FileAlreadyExistsException e) {
                messageBuilder = new StringBuilder();
                messageBuilder.append("Dieser Name wurde schon für ein Bild gewählt, bitte sende mir einen anderen.");
            }

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