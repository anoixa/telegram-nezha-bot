package moe.imtop1.bot.bot;

import lombok.extern.slf4j.Slf4j;
import moe.imtop1.bot.api.NezhaApi;
import moe.imtop1.bot.domain.ServerInfo;
import moe.imtop1.bot.domain.vo.ServerDetailVO;
import moe.imtop1.bot.utils.ErrorMessages;
import moe.imtop1.bot.utils.ToolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
@Slf4j
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

        if (StringUtils.hasText(text)) {
            String[] split = text.split(" ");
            String command = split[0];
            String param = split.length > 1 ? split[1] : null;

            switch (command) {
                case "/num":
                    String tag = StringUtils.hasText(param) ? param : null;
                    List<ServerInfo> serverList = nezhaApi.getServerList(tag);
                    message = SendMessage.builder()
                            .text(String.valueOf(ToolUtils.getServerNum(serverList)))
                            .chatId(update.getMessage().getChatId().toString())
                            .build();
                    break;
                case "/id":
                    if (StringUtils.hasText(param)) {
                        if (ToolUtils.isNumeric(param)) {
                            List<ServerDetailVO> serverDetailListById = nezhaApi
                                    .getServerDeList(null, Long.valueOf(param));
                            message = SendMessage.builder()
                                    .text(serverDetailListById.toString())
                                    .chatId(update.getMessage().getChatId().toString())
                                    .build();
                        } else {
                            message = SendMessage.builder()
                                    .text(ErrorMessages.ILLEGAL_PARAM)
                                    .chatId(update.getMessage().getChatId().toString())
                                    .build();
                        }
                    } else {
                        message = SendMessage.builder()
                                .text(ErrorMessages.NULL_MSG)
                                .chatId(update.getMessage().getChatId().toString())
                                .build();
                    }
                    break;
                case "/search":
                    if (StringUtils.hasText(param)) {
                        List<ServerDetailVO> serverDetailListLikeById = nezhaApi
                                .getServerDeList(null, null);
                        List<ServerDetailVO> collect = serverDetailListLikeById.stream()
                                .filter(item -> item.getName().contains(param))
                                .toList();
                        message = SendMessage.builder()
                                .text(collect.toString())
                                .chatId(update.getMessage().getChatId().toString())
                                .build();
                    } else {
                        message = SendMessage.builder()
                                .text(ErrorMessages.NULL_MSG)
                                .chatId(update.getMessage().getChatId().toString())
                                .build();
                    }
                    break;
                default:
                    message = SendMessage.builder()
                            .text(ErrorMessages.ILLEGAL_ORDER)
                            .chatId(update.getMessage().getChatId().toString())
                            .build();
            }

//        if ("/num".equals(text)) {
//            List<ServerInfo> serverList = nezhaApi.getServerList(null);
//            message = SendMessage.builder()
//                    .text(serverList.toString())
//                    .chatId(update.getMessage().getChatId().toString())
//                    .build();
//        } else {
//            List<ServerDetailVO> serverList = nezhaApi.getServerDeList(null, Long.valueOf(text));
//            message = SendMessage.builder()
//                    .text(serverList.toString())
//                    .chatId(update.getMessage().getChatId().toString())
//                    .build();
//        }


            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("ERROR:", e);
            }
        }
    }
}
