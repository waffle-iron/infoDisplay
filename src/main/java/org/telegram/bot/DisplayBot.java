package org.telegram.bot;

import liketechnik.InfoDisplay.Config;
import org.apache.commons.io.FileUtils;
import org.telegram.bot.commands.*;
import org.telegram.bot.commands.answerCommand.AnswerCommand;
import org.telegram.bot.commands.answerCommand.ChooseNumber;
import org.telegram.bot.commands.answerCommand.WriteAnswer;
import org.telegram.bot.commands.askCommand.AskCommand;
import org.telegram.bot.commands.askCommand.WriteQuestion;
import org.telegram.bot.commands.pinPictureCommand.*;
import org.telegram.bot.database.DatabaseManager;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.bots.commands.ICommandRegistry;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;

import static org.telegram.bot.Main.sendOnErrorOccurred;

/**
 * @author florian
 * @version 1.0
 * @date 22 of October of 2016
 */
public class DisplayBot extends TelegramLongPollingCommandBot {

    public static final String LOGTAG = "DISPLAYBOT";

    /**
     * Register my commands and set an default action for unknown commands send to the bot.
     * Default action executes the {@link HelpCommand}.
     */
    public DisplayBot() {
        register(new StartCommand());
        register(new StopCommand());
        register(new IDCommand());
        register(new RegisterCommand());
        register(new AdministratorCommand());
        register(new AskCommand());
        register(new AnswerCommand());
        register(new PinPictureCommand());
        register(new CancelCommand(this));

        HelpCommand helpCommand = new HelpCommand(this);
        register(helpCommand);

        /**
         * This gets executed when an unknown command is send to the bot.
         * It tells the user that the command is not known and sends it the content of the help command.
         */
        registerDefaultAction(((absSender, message) -> {
            SendMessage commandUnknownMessage = new SendMessage();
            commandUnknownMessage.setChatId(message.getChatId().toString());
            commandUnknownMessage.setText("Der Befehl '" + message.getText() + "' ist diesem Bot nicht bekannt. Hier der " +
                    "Hilfetext:");

            try {
                absSender.sendMessage(commandUnknownMessage);
            } catch (TelegramApiException e) {
                BotLogger.error(LOGTAG, e);
            }

            helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[] {});
        }));
    }

    /**
     * Returns the {@link ICommandRegistry} of this bot instance.
     * @return The {@link ICommandRegistry} of this bot instance.
     */
    public ICommandRegistry getICommandRegistry() {
        return this;
    }

    /**
     * Gets called when a text message is received, instead of a command.
     * It checks if the user used a command which needs further information by the user and
     * if that is the case, calls the corresponding method and gives it the update.
     * @param update The received update.
     */
    @Override
    public void processNonCommandUpdate(Update update) {

        DatabaseManager databaseManager = DatabaseManager.getInstance();
        try {

            if (update.hasMessage()) {

                if (update.getMessage().hasText()) {
//TOFEAT Add if clause that checks for NO_COMMAND and tells the user about it and returns help content
                    if (databaseManager.getUserCommandState(update.getMessage().getFrom().getId())
                            .equals(Config.Bot.ASK_COMMAND_WRITE_QUESTION)) {

                        new WriteQuestion().execute(this, update.getMessage().getFrom(), update.getMessage().getChat(),
                                new String[]{update.getMessage().getText()});


                    } else if (databaseManager.getUserCommandState(update.getMessage().getFrom().getId())
                            .equals(Config.Bot.ANSWER_COMMAND_CHOOSE_NUMBER)) {

                        new ChooseNumber().execute(this, update.getMessage().getFrom(), update.getMessage().getChat(),
                                new String[]{update.getMessage().getText()});

                    } else if (databaseManager.getUserCommandState(update.getMessage().getFrom().getId())
                            .equals(Config.Bot.ANSWER_COMMAND_WRITE_ANSWER)) {

                        new WriteAnswer().execute(this, update.getMessage().getFrom(), update.getMessage().getChat(),
                                new String[]{update.getMessage().getText()});

                    } else if (databaseManager.getUserCommandState(update.getMessage().getFrom().getId())
                            .equals(Config.Bot.PIN_PICTURE_COMMAND_SEND_TITLE)) {

                       new SendTitle().execute(this, update.getMessage().getFrom(), update.getMessage().getChat(),
                               new String[]{update.getMessage().getText()});

                    } else if (databaseManager.getUserCommandState(update.getMessage().getFrom().getId())
                            .equals(Config.Bot.PIN_PICTURE_COMMAND_SEND_DESCRIPTION)) {

                        new SendDescription().execute(this, update.getMessage().getFrom(), update.getMessage().getChat(),
                                new String[]{update.getMessage().getText()});

                    } else if (databaseManager.getUserCommandState(update.getMessage().getFrom().getId())
                            .equals(Config.Bot.PIN_PICTURE_COMMAND_SEND_DURATION)) {

                        new SendDuration().execute(this, update.getMessage().getFrom(), update.getMessage().getChat(),
                                new String[]{update.getMessage().getText()});

                    }
//TOBUG Fix the receiving of uncompressed images (strange)
                } else if (databaseManager.getUserCommandState(update.getMessage().getFrom().getId())
                                    .equals(Config.Bot.PIN_PICTURE_COMMAND_SEND_PICTURE)) {

                        if (update.getMessage().getPhoto() != null) {

                            List<PhotoSize> photos = update.getMessage().getPhoto();

                            int width = 0;
                            int height = 0;

                            int biggestPhoto = 0;


                            for (int x = 0; x < photos.size(); x++) {
                                if (width < photos.get(x).getWidth() || height < photos.get(x).getHeight()) {
                                    biggestPhoto = x;
                                }
                            }

                            new SendPicture().execute(this, update.getMessage().getFrom(), update.getMessage().getChat(),
                                    new String[]{Config.Bot.HAS_PHOTO, photos.get(biggestPhoto).getFileId()});
                        } else {
                            new SendPicture().execute(this, update.getMessage().getFrom(), update.getMessage().getChat(),
                                    new String[]{Config.Bot.HAS_NO_PHOTO});
                        }
                    }
            }
        } catch (Exception e) {
            BotLogger.error("PROCESSNONCOMMANDUPDATE", e);
        }
    }

    /**
     * Gets called if the username of the bot is needed.
     * The returned value is defined in the config file.
     * @return The bot's username.
     */
    @Override
    public String getBotUsername() {
        return Config.Bot.DISPLAY_USER;
    }

    /**
     * Gets called if the token of the bot is needed.
     * The returned value is defined in the config file.
     * @return The bot's token
     */
    @Override
    public String getBotToken() {
        return Config.Bot.DISPLAY_TOKEN;
    }
}
