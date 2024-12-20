import com.oddsmart.database.DataBase;
import java.util.AbstractMap.SimpleEntry;

import com.oddsmart.database.Match;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.util.AbstractMap.SimpleEntry;

import java.util.*;

public class OddSmartBot extends TelegramLongPollingBot {
    DataBase db = new DataBase("localhost", "oddsmartbot", "root", "");
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
                    Start(chatId);
                    break;
                case "/update":
                    break;
                case "/help":
                    Help(chatId);
                    break;
                /*case "/favorites add":
                    break;
                case "/favorites remove":
                    break;*/
                case "/show":
                    Show(chatId);
                    break;
                case "/best":
                    break;
                case "/show_arbitrages":
                    break;
                case "/today":
                    break;
                }
        }else if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();

        System.out.println("Callback ricevuto: " + callbackData);

        // Gestisci i vari callback
        if (callbackData.equals("/help")) {
            Help(chatId);
        }else if(callbackData.equals("/start")){
            Start(chatId);
        } else if (callbackData.equals("/show")) {
            Show(chatId);
        } else if (callbackData.startsWith("/show")) {
            String[] parts = callbackData.split(" ");
            String leagueId = parts[1];
            try {
                List<Match> matches = db.getLeagueMatches("matches", Integer.parseInt(leagueId));
                StringBuilder sb = new StringBuilder();
                for (Match match : matches)
                    sb.append(match.toString()).append("\n\n");
                sb.append("\n\n<b>Partite trovate: ").append(matches.size()).append("</b>");
                sendSimpleMessage(chatId, sb.toString());
            }catch (NumberFormatException e){
                sendSimpleMessage(chatId, "Comando non riconosciuto: " + callbackData);
            }

        } else {
            sendSimpleMessage(chatId, "Comando non riconosciuto: " + callbackData);
        }
    }

    private void Start(long chatId) {
        sendOptionsMessage(chatId,
                "Benvenuto!👋👋\n" +
                        "Mi chiamo Oddy.\n Sono il tuo assistente personalizzato per la gestione delle scommesse sportive!🏆⚽\n\n" +
                        "Grazie a me potrai sempre essere aggiornato sulle migliori offerte disponibili!🎯🎯\n\n" +
                        "Pronto ad iniziare???💸🤑🫰💸\n" +
                        "Ecco alcuni comandi che potrebbero tornarti utili" +
                        "\n👇👇👇👇👇",
                "help",
                new ArrayList<>(List.of(new SimpleEntry<>("", "Mostra i comandi disponibili"))));
    }

    private void Show(long chatId){
        ArrayList<SimpleEntry<String,String>> campionati = db.getLeagues("leagues");
        if(!campionati.isEmpty()){
            sendOptionsMessage(chatId,"Scegli un campionato:", "show ",campionati);
        }
    }

    private void Help(long chatId){
        sendCommandDescription(chatId,
                "Ecco alcuni comandi che potrebbero tornarti utili 🤔",
                new HashMap<>(){
                    {
                        put("/start", "Inizia la conversazione 💬");
                        put("/help", "Mostra i comandi disponibili 📝");
                        put("/show", "Visualizza i campionati disponibili 🏆");
                        put("/show_arbitrages", "Visualizza le occasioni di arbitraggio disponibili 💸");
                        put("/today", "Visualizza le offerte disponibili per oggi ⏰");
                    }
                });
    }

    // invia un messaggio con una lista di comandi e descrizioni
    private void sendCommandDescription(long chatId, String message, HashMap<String, String> commands) {
        StringBuilder messageBuilder = new StringBuilder(message + "\n\n");

        for (Map.Entry<String, String> entry : commands.entrySet()) {
            messageBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
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
    private void sendOptionsMessage(long chatId, String message, String command_prefix, ArrayList<SimpleEntry<String,String>> options) {
        SendMessage s = new SendMessage();
        s.setChatId(String.valueOf(chatId));
        s.setText(message);
        InlineKeyboardMarkup table = createTable(command_prefix, options);
        s.setReplyMarkup(table);
        try {
            execute(s);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea un markup di tastiera in linea in tabella.
     *
     * @param command_prefix prefisso del comando per i dati di callback
     * @param options lista di coppie contenenti:primo elemento: comando (dato di callback), secondo elemento: testo del pulsante
     * @return markup di tastiera in linea creato
     */
    private InlineKeyboardMarkup createTable(String command_prefix, ArrayList<SimpleEntry<String, String>> options) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        int numCols = (int)Math.round(Math.sqrt(options.size()));
        List<List<InlineKeyboardButton>> table = new ArrayList<>();
        List<InlineKeyboardButton> btnRow = new ArrayList<>();

        for (int i = 0; i < options.size(); i++) {
            int col = i % numCols;
            InlineKeyboardButton button = new InlineKeyboardButton(options.get(i).getValue());
            button.setCallbackData("/" + command_prefix + options.get(i).getKey());
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

    /// fa partire il bot
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
