package org.telegram.bot.commands;

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
 * @date 24 of Oktober of 2016
 */
public class IDCommand extends BotCommand {

    public static final String LOGTAG = "IDCOMMAND";

    public IDCommand() {
        super("ids", "Zeigt dir deine Telgramm UserID und die ChatID dieses Chats an.");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        SendMessage answer = new SendMessage();

        try {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Deine UserID ist: ").append(user.getId()).append("\n");
            messageBuilder.append("Unsere ChatID ist: ").append(chat.getId());

            messageBuilder.append("\n").append("/help");

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
