package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Ticket;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.exception.CannotHandleUpdateException;
import com.turlygazhy.exception.DeadlineBeforeNowException;
import com.turlygazhy.exception.NewDeadlineDoesNotMatchesException;
import com.turlygazhy.google_sheets.SheetsAdapter;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yerassyl_Turlygazhy on 18-May-17.
 */
public class ChangeDeadlineCommand extends Command {
    private Ticket ticket;

    public ChangeDeadlineCommand(String ticketId) {
        try {
            ticket = ticketDao.select(Integer.parseInt(ticketId));
        } catch (Exception e) {
            throw new CannotHandleUpdateException();
        }
    }


    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);
        if (wt == null) {
            sendMessage(207, chatId, bot);//send new deadline
            wt = WaitingType.NEW_DEADLINE;
            return false;
        }
        switch (wt) {
            case NEW_DEADLINE:
                try {
                    check(updateMessageText);
                    String executorsIds = ticket.getCategory().getExecutorsIds();
                    for (String executorId : executorsIds.split(",")) {
                        try {
                            User user = userDao.select(Integer.parseInt(executorId));
                            String text = messageDao.getMessageText(211);//deadline was changed
                            text = text.replace("ticketId", String.valueOf(ticket.getId()))
                                    .replace("ticketText", ticket.getText()) + updateMessageText;
                            sendMessage(text, user.getChatId());
                        } catch (Exception ignored) {
                        }
                    }
                    SheetsAdapter.updateDeadline(updateMessageText, ticket.getGoogleSheetRowId());
                    sendMessage(214);//deadline was updated
                    return true;
                } catch (DeadlineBeforeNowException e) {
                    sendMessage(212);//inserted date in past
                    return false;
                } catch (NewDeadlineDoesNotMatchesException e) {
                    sendMessage(213);//inserted date is incorrect
                    return false;
                }
        }
        return false;
    }

    private void check(String newDeadline) throws DeadlineBeforeNowException, NewDeadlineDoesNotMatchesException {
        Pattern p = Pattern.compile("^\\d{2}.\\d{2}.\\d{2} \\d{2}:\\d{2}$");
        Matcher m = p.matcher(newDeadline);
        if (!m.matches()) {
            throw new NewDeadlineDoesNotMatchesException();
        }
        String[] split = newDeadline.split(" ");
        String date = split[0];
        String time = split[1];
        String[] dateSplit = date.split("\\.");
        String day = dateSplit[0];
        String month = dateSplit[1];
        String year = 1 + dateSplit[2];
        String[] timeSplit = time.split(":");
        String hours = timeSplit[0];
        String minutes = timeSplit[1];
        Date dateObject = new Date();
        dateObject.setYear(Integer.parseInt(year));
        dateObject.setMonth(Integer.parseInt(month));
        dateObject.setDate(Integer.parseInt(day));
        dateObject.setHours(Integer.parseInt(hours));
        dateObject.setMinutes(Integer.parseInt(minutes));
        Date now = new Date();
        if (dateObject.before(now)) {
            throw new DeadlineBeforeNowException();
        }
    }


}
