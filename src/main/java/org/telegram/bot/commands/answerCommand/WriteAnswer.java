/*
 * Copyright (C) 2016  Florian Warzecha <flowa2000@gmail.com>
 *
 * This file is part of infoDisplay.
 *
 * infoDisplay is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * infoDisplay is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * infoDisplay uses TelegramBots Java API <https://github.com/rubenlagus/TelegramBots> by Ruben Bermudez.
 * TelegramBots API is licensed under GNU General Public License version 3 <https://github.com/rubenlagus/TelegramBots>.
 *
 * infoDisplay uses parts of the Apache Commons project <https://commons.apache.org/>.
 * Apache commons is licensed under the Apache License Version 2.0 <http://www.apache.org/licenses/>.
 */

package org.telegram.bot.commands.answerCommand;

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

/**
 * @author Florian Warzecha
 * @version 1.0.1
 * @date 01 of November 2016
 *
 * This command handles the answer of a chosen question.
 */
public class WriteAnswer extends BotCommand {

    public static final String LOGTAG = "ANSWERCOMMAND_WRITEANSWER";

    /**
     * Set the identifier and a short description of the command.
     */
    public WriteAnswer() {
        super("write_answer", "This command evaluates the answer after a user executed /answer (and ChooseNumber).");
    }

    /**
     * Evaluate the message send by a user.
     * This is done by taking the message that contains the answer and send it to the user who asked the question.
     * @param absSender
     * @param user
     * @param chat
     * @param arguments
     */
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        SendMessage answer = new SendMessage();
        SendMessage confirmation = new SendMessage();

        String message = arguments[0];

        try {

            DatabaseManager databaseManager = DatabaseManager.getInstance();

            StringBuilder messageBuilder = new StringBuilder();

            int selectedQuestion = databaseManager.getSelectedQuestion(user.getId());

            messageBuilder.append("Deine Frage war: ").append("\n");
            messageBuilder.append(databaseManager.getQuestion(selectedQuestion - 1)).append("\n\n");
            messageBuilder.append("Die Antwort: ").append("\n");
            messageBuilder.append(message);

            StringBuilder confirmationBuilder = new StringBuilder();
            confirmationBuilder.append("Antwort erfolgreich gesendet.").append("\n").append("/help");

            answer.setChatId(databaseManager.getQuestionChatID(selectedQuestion - 1).toString());
            answer.setText(messageBuilder.toString());

            confirmation.setChatId(databaseManager.getAdminChatId().toString());
            confirmation.setText(confirmationBuilder.toString());

            databaseManager.deleteQuestion(selectedQuestion - 1);
            databaseManager.setSelectedQuestion(user.getId(), -1);


        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);

            new SendOnErrorOccurred().execute(absSender, user, chat, new String[]{LOGTAG});

            return;
        }

        try {
            absSender.sendMessage(confirmation);
            absSender.sendMessage(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}