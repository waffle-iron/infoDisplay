package org.telegram.bot.commands;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.telegram.bot.Config;
import org.telegram.bot.database.DatabaseManager;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.bots.commands.ICommandRegistry;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.awt.event.KeyEvent;
import java.io.IOException;

import static org.telegram.bot.Main.sendOnErrorOccurred;

/**
 * @author florian
 * @version 1.0
 * @date 23 of Oktober of 2016
 */
public class HelpCommand extends BotCommand {

    private static final String LOGTAG = "HELPCOMMAND";

    private final ICommandRegistry commandRegistry;

    public HelpCommand(ICommandRegistry commandRegistry) {
        super("help", "Wozu dient dieser Bot und wie kannst du den Bot kontrollieren?");
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        SendMessage answer = new SendMessage();

        try {
            DatabaseManager.getInstance().setUserState(user.getId(), true);

            StringBuilder messageBuilder = new StringBuilder();

            messageBuilder.append("<b>Hilfe</b>").append("\n\n");
            messageBuilder.append("Beschreibung:").append("\n");
            messageBuilder.append("Mit diesem Bot kannst Du Bilder an das virtuelle Brett hochladen. Als Administrator " +
                    "kannst du auch veraltete Bilder anderer löschen und deren Eigenschaften bearbeiten.").append("\n\n");

            messageBuilder.append("So kannst du mich nutzen:").append("\n");

            for (BotCommand botCommand : commandRegistry.getRegisteredCommands()) {
                if (!botCommand.getCommandIdentifier().equals("help") &&
                        !botCommand.getCommandIdentifier().equals("start") &&
                        !botCommand.getCommandIdentifier().equals("stop") &&
                        !botCommand.getCommandIdentifier().equals("ids") &&
                        !botCommand.getCommandIdentifier().equals("answer")) {

                    messageBuilder.append("/").append(botCommand.getCommandIdentifier()).append(":\n    ")
                            .append(botCommand.getDescription()).append("\n");
                }
            }

            if (user.getId().equals(Config.Bot.ADMIN_USER_ID)) {
                messageBuilder.append("/").append(commandRegistry.getRegisteredCommand("answer")
                        .getCommandIdentifier()).append(":\n    ").append(commandRegistry.getRegisteredCommand("answer")
                        .getDescription()).append("\n");
            }

            answer.setChatId(chat.getId().toString());
            answer.enableHtml(true);
            answer.setText(messageBuilder.toString());

        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);

            answer.setText("Der Bot ist aufgrund eines Fehlers beendet worden. Bitte informiere den Administrator.");

            try {
                absSender.sendMessage(answer);
            } catch (TelegramApiException e1) {
                BotLogger.error(LOGTAG, e1);
            }

            System.exit(1);
        }

        try {
            absSender.sendMessage(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}
