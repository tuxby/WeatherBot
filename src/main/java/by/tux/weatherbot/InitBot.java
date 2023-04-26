package by.tux.weatherbot;

import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
@Service
public class InitBot {
    @PostConstruct
    public void init() throws TelegramApiException{
        System.out.println("Weather BOT started!");

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(
                DefaultBotSession.class
        );
        telegramBotsApi.registerBot(new Bot());
    }
}
