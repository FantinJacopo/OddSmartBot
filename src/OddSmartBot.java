import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

public class OddSmartBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "OddSmartBot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("TELEGRAM_BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            System.out.println("messaggio: " + messageText + " Id chat: " + chatId);

            // test
            if (messageText.equals("/start")) {
                sendOptionsMessage(chatId, "Benvenuto! Seleziona l'opzione desiderata:", new ArrayList<String>() {{
                    add("opzione 1");
                    add("opzione 2");
                    add("opzione 3");
                    add("opzione 4");
                    add("opzione 5");
                }});
            }
        }
    }

    // invia un messaggio con le opzioni (pulsanti)
    private void sendOptionsMessage(long chatId, String message, ArrayList<String> options) {
        SendMessage s = new SendMessage();
        s.setChatId(String.valueOf(chatId));
        s.setText(message);
        InlineKeyboardMarkup table = createTable(options);
        s.setReplyMarkup(table);
        try {
            execute(s);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // crea una tabella con n opzioni. La creo quadrata il più possibile
    private InlineKeyboardMarkup createTable(ArrayList<String> options) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        int numCols = (int)Math.round(Math.sqrt(options.size()));
        List<List<InlineKeyboardButton>> table = new ArrayList<>();
        List<InlineKeyboardButton> btnRow = new ArrayList<>();

        for (int i = 0; i < options.size(); i++) {
            int row = i / numCols;
            int col = i % numCols;
            InlineKeyboardButton button = new InlineKeyboardButton(options.get(i));
            button.setCallbackData(options.get(i).replace(" ", "_"));
            if(col == 0){
                btnRow = new ArrayList<>();
            }
            btnRow.add(button);
            if (col == numCols - 1 && i != options.size() - 1) {
                table.add(btnRow);
            }
        }
        table.add(btnRow);
        keyboard.setKeyboard(table);
        return keyboard;
    }

    public static void main(String[] args) {

        System.out.println("OddSmartBot is running...");

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new OddSmartBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
