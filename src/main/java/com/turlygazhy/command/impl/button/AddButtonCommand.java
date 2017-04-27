package com.turlygazhy.command.impl.button;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.command.CommandType;
import com.turlygazhy.command.impl.button.feedback.AddFeedbackButtonCommand;
import com.turlygazhy.command.impl.button.show_info.AddShowInfoButtonCommand;
import com.turlygazhy.entity.AddButton;
import com.turlygazhy.exception.NotImplementedMethodException;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 4/16/17.
 */
public class AddButtonCommand extends Command {
    private final AddButton addButton;
    private Command command;

    public AddButtonCommand(AddButton addButton) {
        this.addButton = addButton;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (command == null) {
            CommandType commandType = CommandType.getType(addButton.getCommandTypeId());
            switch (commandType) {
                case SHOW_INFO:
                    command = new AddShowInfoButtonCommand(addButton);
                    break;
                case FEEDBACK:
                    command = new AddFeedbackButtonCommand(addButton);
                    break;
                default:
                    throw new NotImplementedMethodException(commandType);
            }
        }
        return command.execute(update, bot);
    }
}