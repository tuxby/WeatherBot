package by.tux.weatherbot;

import by.tux.weatherbot.Service.Openweathermap;
import by.tux.weatherbot.config.BotConfig;
import by.tux.weatherbot.model.ChatLogModel;
import by.tux.weatherbot.repository.ChatHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static by.tux.weatherbot.Service.Openweathermap.getWeatherByLocation;

@Slf4j
@Component
public class WeatherBot extends TelegramLongPollingBot {
    private final ChatHistoryRepository chatHistoryRepository;
    final BotConfig config;

    public WeatherBot(ChatHistoryRepository chatHistoryRepository, BotConfig config) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.config = config;
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String receivedMessage;
        long chatId = update.getMessage().getChatId();
        long userId = update.getMessage().getFrom().getId();
        String userName = update.getMessage().getFrom().getFirstName();

        Message message = update.getMessage();

        String result = null;
        if (message.hasLocation()) {
            // Если пользователь отправил местоположение
            result = answerByUserLocation(update);
        } else {
            // Если пользователь не отправил местоположение, отобразим кнопку с запросом местоположения
            // Отправляем сообщение с клавиатурой пользователю
            if(update.hasMessage()) {
                //если получено сообщение текстом
                if (update.getMessage().hasText()) {
                    receivedMessage = update.getMessage().getText();
                    result = botAnswerUtils(receivedMessage , chatId, userName , update.getMessage());
                }
                //если нажата одна из кнопок бота
            } else if (update.hasCallbackQuery()) {
                chatId = update.getCallbackQuery().getMessage().getChatId();
                userId = update.getCallbackQuery().getFrom().getId();
                userName = update.getCallbackQuery().getFrom().getFirstName();
                receivedMessage = update.getCallbackQuery().getData();
                result = botAnswerUtils(receivedMessage, chatId, userName, update.getMessage());
            }
            result = result.replace("\n", " ").replace("\n", " ");
            String request = message.getText().replace("\n", " ").replace("\n", " ");
            log.info("--->> chatId="+chatId+",userId="+userId+",userName="+userName+",message="+ request);
            log.info("<<--- chatId="+chatId+",userId="+userId+",userName="+userName+",message="+result);
            ChatLogModel chatLogModel = new ChatLogModel();
            chatLogModel.setChatId(chatId);
            chatLogModel.setUserId(userId);
            chatLogModel.setUserName(userName);
            chatLogModel.setRequest(request);
            chatLogModel.setResult(result);
            chatHistoryRepository.save(chatLogModel);
        }
    }

    private ReplyKeyboardMarkup createLocationButton() {
        // Создаем кнопку запроса местоположения
        KeyboardButton requestLocationButton = new KeyboardButton();
        requestLocationButton.setText("Отправить местоположение");
        requestLocationButton.setRequestLocation(true);

        // Создаем ряд для хранения кнопки запроса местоположения
        KeyboardRow row = new KeyboardRow();
        row.add(requestLocationButton);

        // Создаем клавиатуру и добавляем ряд
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        // Создаем объект ReplyKeyboardMarkup и устанавливаем клавиатуру
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
        replyMarkup.setResizeKeyboard(true);
        replyMarkup.setOneTimeKeyboard(true);
        replyMarkup.setKeyboard(keyboard);

        return replyMarkup;
    }

    private String answerByUserLocation(Update update) {
        Message message = update.getMessage();
        // Обработка полученного местоположения
        Long chatId = message.getChatId();
        Double latitude = message.getLocation().getLatitude();
        Double longitude = message.getLocation().getLongitude();

        // Отправка ответа пользователю
        String weatherMessage = getWeatherByLocation(latitude.toString(),longitude.toString());
        String result;
        SendMessage response = new SendMessage();
        response.setChatId(chatId);

        if (weatherMessage!=null){
            result = weatherMessage;
        }
        else
            result = "Ошибка определения погоды";

        try {
            response.setText(result);
            execute(response);
            return result;
        } catch (TelegramApiException e) {
            return null;
        }
    }

    private String botAnswerUtils(String receivedMessage, long chatId, String userName, Message message) {
        String htmlString = "<b>bold</b>\n"+
                "<strong>bold</strong>\n"+
                "<i>italic</i>\n"+
                "<em>italic</em>\n"+
                "<u>underline</u>\n"+
                "<ins>underline</ins>\n"+
                "<s>strikethrough</s>\n"+
                "<strike>strikethrough</strike>\n"+
                "<del>strikethrough</del>\n"+
                "<span class=\"tg-spoiler\">spoiler</span>\n"+
                "<tg-spoiler>spoiler</tg-spoiler>\n"+
                "<b>bold <i>italic bold <s>italic bold strikethrough <span class=\"tg-spoiler\">italic bold strikethrough spoiler</span></s> <u>underline italic bold</u></i> bold</b>\n"+
                "<a href=\"http://www.example.com/\">inline URL</a>\n"+
                "<a href=\"tg://user?id=123456789\">inline mention of a user</a>\n"+
                "<tg-emoji emoji-id=\"5368324170671202286\">👍</tg-emoji>\n"+
                "<code>inline fixed-width code</code>\n"+
                "<pre>pre-formatted fixed-width code block</pre>\n"+
                "<pre><code class=\"language-python\">pre-formatted fixed-width code block written in the Python programming language</code></pre>\n";
        String result;
        switch (receivedMessage){
            case "/start":
                return startBot( chatId, userName);
            case "html":
                return sendMessage(chatId,htmlString);
            default:
                result = Openweathermap.getWeatherByCity(receivedMessage);
                if (result!=null)
                    return sendMessage(chatId, result);
                else {
                    return sendMessage(chatId, "Не удалось узнать погоду по вашему запросу\n<i>Вы можете получить текущие данные о погоде\nотправив ваше местоположение</i>");
                }
        }
    }

    private String startBot(long chatId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableHtml(true);
        message.setReplyMarkup( createLocationButton() );
        String result = "<b>\uD83C\uDF24\uD83C\uDF26Привет, " + userName + "!</b>\n<i>Я умею показывать погоду по местоположению\nили по названию населенного пункту</i>";
        message.setText(result);
        try {
            execute(message);
            log.info("Reply sent to " + userName);
            return result;
        } catch (TelegramApiException e){
            log.error(e.getMessage());
            return null;
        }
    }

    private String sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableHtml(true);
        message.setReplyMarkup( createLocationButton() );
        message.setText(textToSend);
        try {
            execute(message);
            log.info("Reply sent to chat_id=" + chatId);
            return textToSend;
        } catch (TelegramApiException e){
            log.error(e.getMessage());
            return null;
        }
    }


}