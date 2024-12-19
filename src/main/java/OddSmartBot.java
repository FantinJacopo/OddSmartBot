import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.*;

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
            switch  (messageText){
                case "/start":
                    Dictionary<String, String> commands = new Hashtable<>();
                    commands.put("/start", "Avvia il bot");
                    commands.put("/update", "aggiorna i dati");
                    commands.put("/favorites add", "aggiungi ai preferiti");
                    commands.put("/favorites remove", "rimuovi dai preferiti");
                    commands.put("/show + ", "Mostra");
                    commands.put("/best + 'partita'", "Mostra le migliori offerte per la partita selezionata");
                    commands.put("/show_arbitrages", "Mostra le opportunità di arbitraggio");
                    commands.put("/today", "Mostra le partite di oggi");
                    sendCommandDescription(chatId, "Benvenuto!👋👋\n" +
                            "Io sono Oddy.\n Sono il tuo assistente personalizzato per la gestione delle scommesse sportive!🏆⚽\n\n" +
                            "Grazie a me potrai sempre essere aggiornato sulle migliori offerte disponibili!🎯🎯\n\n" +
                            "Pronto ad iniziare???💸🤑🫰💸\n" +
                            "Ecco alcuni comandi che potrebbero tornarti utili" +
                                    "\n👇👇👇👇👇",
                            commands); ;
                    break;
                case "/update":
                    break;
                case "/help":
                    sendOptionsMessage(chatId,"Ecco i comandi disponibili:", new ArrayList<>(List.of("/start", "/update", "/favorites add", "/favorites remove", "/show", "/best", "/show_arbitrages", "/today")));
                    break;
                /*case "/favorites add":
                    break;
                case "/favorites remove":
                    break;*/
                case "/show":
                    break;
                case "/best":
                    break;
                case "/show_arbitrages":
                    break;
                case "/today":
                    break;
                default:
                    sendOptionsMessage(chatId, "Seleziona un comando valido", new ArrayList<>(List.of("/start", "/update", "/favorites add", "/favorites remove", "/show", "/best", "/show_arbitrages", "/today")));
            }
        }
    }

    // invia un messaggio con una lista di comandi e descrizioni
    private void sendCommandDescription(long chatId, String message, Dictionary<String, String> commands) {
        StringBuilder messageBuilder = new StringBuilder(message + "\n\n");

        for (Enumeration<String> keys = commands.keys(); keys.hasMoreElements();) {
            String command = keys.nextElement();
            String description = commands.get(command);
            messageBuilder.append(command).append(": ").append(description).append("\n");
        }

        SendMessage s = new SendMessage();
        s.setChatId(String.valueOf(chatId));
        s.setText(messageBuilder.toString());

        try {
            execute(s);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // invia un messaggio contenente solo testo
    private void sendSimpleMessage(long chatId, String message) {
        SendMessage s = new SendMessage();
        s.setChatId(String.valueOf(chatId));
        s.setText(message);
        try {
            execute(s);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
            InlineKeyboardButton button = new InlineKeyboardButton(options.get(i).replace("/", ""));
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
        String botToken = System.getenv("TELEGRAM_BOT_TOKEN");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new OddSmartBot());
            System.out.println("OddSmartBot is running...");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
