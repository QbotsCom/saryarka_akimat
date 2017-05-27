package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Ticket;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.exception.CannotHandleUpdateException;
import com.turlygazhy.exception.DeadlineBeforeNowException;
import com.turlygazhy.exception.NewDeadlineDoesNotMatchesException;
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
                    //todo уведомить всех исполнителей
                    //todo изменить в бд
                    //todo изменить в гугл диске
                } catch (DeadlineBeforeNowException e) {
                    e.printStackTrace();//todo сказать дата которую вы поставили уже прошла
                } catch (NewDeadlineDoesNotMatchesException e) {
                    e.printStackTrace();//todo не удалось распознать попробуйте ввести еще раз
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
