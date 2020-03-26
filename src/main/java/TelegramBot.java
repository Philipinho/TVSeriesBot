import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import util.DB;
import util.ReadProperty;

import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {
    private String siteUrl = ReadProperty.getValue("site");


    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage();
        CrawlMovies tvSearch = new CrawlMovies();
        SeasonList seasonList = new SeasonList();
        EpisodeList episodeList = new EpisodeList();
        Download download = new Download();
        String downArrow = EmojiParser.parseToUnicode(":arrow_down:");

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equalsIgnoreCase("/start")){
                message.setChatId(chatId)
                        .setText("Hello @" + update.getMessage().getChat().getUserName() +
                                ", welcome. I'm here to help you download movies and TV Series.");
                DB.saveUser(update.getMessage().getChat().getUserName(),
                        String.valueOf(update.getMessage().getChat().getId()));
            }

            if (!messageText.endsWith(".html") && !messageText.equalsIgnoreCase("/start")) {
                for (InlineKeyboardMarkup markup : setInlineKeyboard(tvSearch.SearchMovies(messageText))) {
                    if(markup.getKeyboard().size() >0) {
                        message.setText(downArrow)
                                .setChatId(chatId)
                                .setReplyMarkup(markup);
                    } else {
                        message.setChatId(chatId)
                                .setText("No result found, please try another search.");
                    }
                }
            }
        }  else if (update.hasCallbackQuery()){

            String callbackString = siteUrl + update.getCallbackQuery().getData();
            long callbackChatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackString.contains("/id/")){
                for (InlineKeyboardMarkup markup : setInlineKeyboard(seasonList.fetchSeasonList(callbackString))) {

                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setPhoto(tvSearch.fetchThumbnail(callbackString))
                            .setChatId(callbackChatId).setReplyMarkup(markup);
                    try {
                        execute(sendPhoto);
                    } catch (TelegramApiException e){
                        e.printStackTrace();
                    }
                }

            }

            if ((callbackString.contains("/movies/") || callbackString.contains("/tv-series/"))) {

                for (InlineKeyboardMarkup markup : setInlineKeyboard(episodeList.fetchEpisodes(callbackString))) {

                    message.setText(downArrow)
                            .setChatId(callbackChatId)
                            .setReplyMarkup(markup);
                }
            }

            if ((callbackString.contains("/download/"))) {

                InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> inlineRows = new ArrayList<>();

                String downloadLink = download.fetchDownloadLink(callbackString);

                List<InlineKeyboardButton> inlineRow = new ArrayList<>();
                inlineRow.add(new InlineKeyboardButton()
                        .setText("Click here to download").setUrl(downloadLink));

                inlineRows.add(inlineRow);

                keyboardMarkup.setKeyboard(inlineRows);
                message.setReplyMarkup(keyboardMarkup)
                        .setChatId(callbackChatId)
                        .setText(downArrow);

            }

        }

        try {
            if (message.getChatId() != null) {
                execute(message);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return ReadProperty.getValue("bot.username");
    }

    @Override
    public String getBotToken() {
        return ReadProperty.getValue("bot.token");
    }

    public List<InlineKeyboardMarkup> setInlineKeyboard(Map<String,String> hashMapData){

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> inlineRows = new ArrayList<>();

        List<InlineKeyboardMarkup> markupList = new ArrayList<>();


        for (String data : hashMapData.keySet()){

            List<InlineKeyboardButton> inlineRow = new ArrayList<>();
            String newString = hashMapData.get(data).substring(24);

            if (newString.length() >= 65){
                newString = newString.substring(0,newString.length() -40);
                newString += ".html";
            }

            inlineRow.add(new InlineKeyboardButton()
                    .setText(data).
                            setCallbackData(newString));
            inlineRows.add(inlineRow);
        }

        keyboardMarkup.setKeyboard(inlineRows);
        markupList.add(keyboardMarkup);

        return  markupList;
    }

}
