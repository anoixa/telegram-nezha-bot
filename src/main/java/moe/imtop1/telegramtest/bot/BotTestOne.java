package moe.imtop1.telegramtest.bot;

import jakarta.annotation.Resource;
import moe.imtop1.telegramtest.api.NezhaApi;
import moe.imtop1.telegramtest.domain.ServerInfo;
import moe.imtop1.telegramtest.domain.vo.ServerDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

/**
 * bot主体逻辑
 * @author anoixa
 */
@Component
public class BotTestOne extends TelegramLongPollingBot {

    @Value(value = "${telegram.bot.config.token}")
    private String token;
    @Value(value = "${telegram.bot.config.userName}")
    private String userName;

    @Autowired
    private NezhaApi nezhaApi;

    @Autowired
    public BotTestOne(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public String getBotUsername() {
        return this.userName;
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String text = update.getMessage().getText();
        SendMessage message = null;

        if ("1".equals(text)) {
            List<ServerInfo> serverList = nezhaApi.getServerList(null);
            message = SendMessage.builder()
                    .text(serverList.toString())
                    .chatId(update.getMessage().getChatId().toString())
                    .build();
        } else {
            List<ServerDetailVO> serverList = nezhaApi.getServerDeList(null, Long.valueOf(text));
            message = SendMessage.builder()
                    .text(serverList.toString())
                    .chatId(update.getMessage().getChatId().toString())
                    .build();
        }


        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
