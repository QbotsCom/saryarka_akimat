package com.turlygazhy;

import com.turlygazhy.dao.DaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Yerassyl_Turlygazhy on 11/24/2016.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int BOT_MOTHER_ID = 0;
    private static final long YERASSYL_CHAT_ID = 293188753L;
    private static TelegramBotsApi telegramBotsApi;

    public static void main(String[] args) {

        logger.info("ApiContextInitializer.init()");
        ApiContextInitializer.init();

        telegramBotsApi = new TelegramBotsApi();
//        Bot bot = new Bot("Mother bot", "325789458:AAEGL-ERbDULJI8uGWo_grOcPbxnkzExNec", BOT_MOTHER_ID, YERASSYL_CHAT_ID);
        Bot bot = new Bot("Mother bot test", "330782553:AAERpMlflu7ELyY29SoPSBwByGXIlwKXwsQ", BOT_MOTHER_ID, YERASSYL_CHAT_ID);
        register(bot);
        try {
            List<Bot> bots = DaoFactory.getFactory().getBotsDao().selectAll();
            for (Bot bot1 : bots) {
                register(bot1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void register(Bot bot) {
        try {
            telegramBotsApi.registerBot(bot);
            logger.info("Bot was registered");
        } catch (TelegramApiRequestException e) {
            throw new RuntimeException(e);
        }
    }
}
