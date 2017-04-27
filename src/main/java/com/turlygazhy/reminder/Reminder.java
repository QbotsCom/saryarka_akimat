package com.turlygazhy.reminder;

import com.turlygazhy.Bot;
import com.turlygazhy.reminder.timer_task.*;
import com.turlygazhy.tool.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Timer;

/**
 * Created by Yerassyl_Turlygazhy on 02-Mar-17.
 */
public class Reminder {
    private static final Logger logger = LoggerFactory.getLogger(Reminder.class);

    private Bot bot;
    private Timer timer = new Timer(true);

    public Reminder(Bot bot) {
        this.bot = bot;
        setNextNightTask();
        setNextWeeklyTask();
        setNextMonthTask();
        setPushUpTask(8);
        setPushUpTask(11);
        setEndDayTask(20);
        setEndDayTask(21);
        setEndDayTask(22);
        setEndDayTask(23);
    }

    public void setEndDayTask(int hour) {
        Date date = DateUtil.getHour(hour);
        logger.info("Next end day task set to " + date);

        EndDayTask endDayTask = new EndDayTask(bot, this);
        timer.schedule(endDayTask, date);
    }

    private void setPushUpTask(int hour) {
        Date date = DateUtil.getHour(hour);
        logger.info("Next 8MorningTask set to " + date);

        PushUpTask pushUpTask = new PushUpTask(bot, this);
        timer.schedule(pushUpTask, date);
    }

    private void setNextMonthTask() {
        Date date = DateUtil.getNextMonth();
        logger.info("Next month task set to " + date);

        MonthTask monthTask = new MonthTask(bot, this);
        timer.schedule(monthTask, date);
    }

    private void setNextWeeklyTask() {
        Date date = DateUtil.getNextWeek();
        logger.info("Next weekly task set to " + date);

        WeeklyTask weeklyTask = new WeeklyTask(bot, this);
        timer.schedule(weeklyTask, date);
    }

    public void setNextNightTask() {
        Date date = DateUtil.getNextNight();

        logger.info("new reminder time: " + date);
        EveryNightTask everyNightTask = new EveryNightTask(bot, this);
        timer.schedule(everyNightTask, date);
    }

    public void setNextPushUpTask() {
        setPushUpTask(new Date().getHours());
    }
}
