package com.turlygazhy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

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
//        Bot bot = new Bot("Сарыарка акимат", "295957927:AAHtCvP5Gcc1kNCmYpgc7yor646ebinnNiQ", 109, YERASSYL_CHAT_ID);
        Bot bot = new Bot("Сарыарка акимат", "302643839:AAHOsiedpf8bsyCzXETeU0C-ijZGu8v4sN0", 109, YERASSYL_CHAT_ID);//todo test
        register(bot);
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
