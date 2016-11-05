package org.telegram.bot.commands.pinPictureCommand;

import liketechnik.InfoDisplay.Config;
import org.apache.commons.io.FileUtils;
import org.telegram.bot.commands.SendOnErrorOccurred;
import org.telegram.bot.database.DatabaseManager;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * @author florian
 * @version 1.0
 * @date 01 of November 2016
 */
public class SendPicture extends BotCommand {

    public static final String LOGTAG = "PINPICTURECOMMAND_SENDPICTURE";

    public SendPicture() {
        super("send_picture", "Saves the photo send by a user after he executed /pin_picture (and SendTitle, SendDescription and SendDuration).");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        SendMessage answer = new SendMessage();

        try {

            DatabaseManager databaseManager = DatabaseManager.getInstance();

            StringBuilder messageBuilder = new StringBuilder();

            if (arguments[0].equals(Config.Bot.HAS_PHOTO)) {
                String displayFileName = databaseManager.getCurrentPictureTitle(user.getId());

                GetFile getFileRequest = new GetFile();
                getFileRequest.setFileId(arguments[1]);

                File file = absSender.getFile(getFileRequest);
                URL fileUrl = new URL(
                        "https://api.telegram.org/file/bot" +
                                Config.Bot.DISPLAY_TOKEN + "/" + file.getFilePath());
                Path image = FileSystems.getDefault().getPath(Config.Paths.DISPLAY_FILES + "/" + displayFileName);

                FileUtils.copyURLToFile(fileUrl, image.toFile(), 10000, 10000);

                databaseManager.setDisplayFileType(displayFileName, Config.Bot.DISPLAY_FILE_TYPE_IMAGE);
                databaseManager.createDisplayFile(displayFileName);

                messageBuilder.append("Du hast ein neues Bild an das Brett hochgeladen.").append("\n").append("/help");

                databaseManager.setUserCommandState(user.getId(), Config.Bot.NO_COMMAND);
            } else {
                messageBuilder.append("Sende mir bitte ein Bild.");
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