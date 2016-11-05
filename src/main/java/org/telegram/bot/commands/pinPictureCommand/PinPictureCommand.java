package org.telegram.bot.commands.pinPictureCommand;

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

import static org.telegram.bot.Main.sendOnErrorOccurred;
//TODOC add javadoc /documentation
/**
 * @author florian
 * @version 1.0
 * @date 27 of Oktober of 2016
 */
public class PinPictureCommand extends BotCommand {

    public static final String LOGTAG = "PINPICTURECOMMAND";

    public PinPictureCommand() {
        super("pin_picture", "Lade ein Bild an das virtuelle Brett hoch.");
    }

    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        SendMessage answerMessage = new SendMessage();

        try {
            DatabaseManager databaseManager = DatabaseManager.getInstance();

            StringBuilder messageBuilder = new StringBuilder();

            if (!databaseManager.getUserRegistrationState(user.getId())) {
                messageBuilder.append("Du bist nicht berechtigt Bilder hochzuladen.").append("\n");
                messageBuilder.append("Benutze /register um als berechtigte Person eingetragen " +
                        "zu werden.").append("\n").append("/help");

                answerMessage.setText(messageBuilder.toString());

                try {
                    absSender.sendMessage(answerMessage);
                } catch (Exception e) {
                    BotLogger.error(LOGTAG, e);
                }

                return;
            }

            databaseManager.setUserCommandState(user.getId(), Config.Bot.PIN_PICTURE_COMMAND_SEND_TITLE);

            messageBuilder.append("Bitte sende mir den Namen deines Bildes.");

            answerMessage.setText(messageBuilder.toString());
            answerMessage.setChatId(user.getId().toString());
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);

            new SendOnErrorOccurred().execute(absSender, user, chat, new String[]{LOGTAG});

            return;
        }

        try {
            absSender.sendMessage(answerMessage);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }

}
