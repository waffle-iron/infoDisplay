package org.telegram.bot.commands;
//TODOC add javadoc /documentation
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.telegram.bot.Config;
import org.telegram.bot.DisplayBot;
import org.telegram.bot.database.DatabaseManager;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.bots.commands.ICommandRegistry;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * @author florian
 * @version 1.0
 * @date 25 of October of 2016
 */
public class CancelCommand extends BotCommand {

    public static final String LOGTAG = "CANCELCOMMAND";

    private final ICommandRegistry commandRegistry;

    public CancelCommand(ICommandRegistry commandRegistry) {
        super("cancel", "Bricht die aktuelle Aktion ab (zum Beispiel, das Hochladen eines neuen Bildes).");
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        try {
            DatabaseManager.getInstance().setUserCommandState(user.getId(), Config.Bot.NO_COMMAND);

            HelpCommand helpCommand = new HelpCommand(commandRegistry);
            helpCommand.execute(absSender, user, chat, arguments);

        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);

            new SendOnErrorOccurred().execute(absSender, user, chat, new String[]{LOGTAG});
        }
    }
}
