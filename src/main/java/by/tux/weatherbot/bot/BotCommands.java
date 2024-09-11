package by.tux.weatherbot.bot;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import java.util.List;

public interface BotCommands {
    List<BotCommand> LIST_OF_COMMANDS = List.of(
            new BotCommand("/start", "Запустить бота"),
            new BotCommand("/help", "Получить информацию")
    );

    String HELP_TEXT = "Привет, Я телеграм бот\n" +
            "я могу показывать погоду по местоположению\n" +
            "или по населенному пункту.\n";
}