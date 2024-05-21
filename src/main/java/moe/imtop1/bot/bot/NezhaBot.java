package moe.imtop1.bot.bot;

import lombok.extern.slf4j.Slf4j;
import moe.imtop1.bot.api.NezhaApi;
import moe.imtop1.bot.domain.ServerInfo;
import moe.imtop1.bot.domain.vo.ServerDetailVO;
import moe.imtop1.bot.utils.MessagesEnum;
import moe.imtop1.bot.utils.ToolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * bot主体逻辑
 * @author anoixa
 */
@Component
@Slf4j
public class NezhaBot extends TelegramLongPollingBot {

    @Value(value = "${telegram.bot.config.token}")
    private String token;
    @Value(value = "${telegram.bot.config.userName}")
    private String userName;

    @Autowired
    private NezhaApi nezhaApi;

    @Autowired
    public NezhaBot(DefaultBotOptions options) {
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
        if (StringUtils.hasText(text)) {
            List<SendMessage> msgList = this.getMessage(text, update);

            msgList.stream().forEach(item -> {
                try {
                    execute(item);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            });
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

    }

    /**
     * 封装消息对象
     * @param text 输入内容
     * @param update Update对象
     * @return 消息
     */
    private List<SendMessage> getMessage(String text, Update update) {
        List<SendMessage> msgList = new ArrayList<>();

        String[] split = text.split(" ");
        String command = split[0].trim();
        String param = split.length > 1 ? split[1].trim() : null;

        switch (command) {
            case "/num":
                String tag = StringUtils.hasText(param) ? param : null;
                List<ServerInfo> serverList = nezhaApi.getServerList(tag);
                SendMessage numMessage = SendMessage.builder()
                        .text(String.valueOf(ToolUtils.getNum(serverList)))
                        .chatId(update.getMessage().getChatId().toString())
                        .build();
                msgList.add(numMessage);
                break;
            case "/id":
                if (StringUtils.hasText(param)) {
                    if (ToolUtils.isNumeric(param)) {
                        List<ServerDetailVO> serverDetailListById = nezhaApi
                                .getServerDeList(null, Long.valueOf(param));

                        if (!ObjectUtils.isEmpty(serverDetailListById.getFirst())) {
                            SendMessage idSuccessMessage = SendMessage.builder()
                                    .text(this.formatStatusMessage(serverDetailListById.getFirst()))
                                    .chatId(update.getMessage().getChatId().toString())
                                    .build();
                            msgList.add(idSuccessMessage);
                        }
                    } else {
                        SendMessage idErrorMessage = SendMessage.builder()
                                .text(MessagesEnum.ILLEGAL_PARAM)
                                .chatId(update.getMessage().getChatId().toString())
                                .build();
                        msgList.add(idErrorMessage);
                    }
                } else {
                    SendMessage idErrorMessage = SendMessage.builder()
                            .text(MessagesEnum.NULL_MSG)
                            .chatId(update.getMessage().getChatId().toString())
                            .build();
                    msgList.add(idErrorMessage);
                }
                break;
            case "/search":
                if (StringUtils.hasText(param)) {
                    List<ServerDetailVO> serverDetailListLikeById = nezhaApi
                            .getServerDeList(null, null);
                    List<ServerDetailVO> collect = serverDetailListLikeById.stream()
                            .filter(item -> item.getName().contains(param))
                            .toList();
                    if (!collect.isEmpty()) {
                        collect.stream().forEach(item -> {
                            SendMessage searchSuccessMessage = SendMessage.builder()
                                    .text(this.formatStatusMessage(item))
                                    .chatId(update.getMessage().getChatId().toString())
                                    .build();
                            msgList.add(searchSuccessMessage);
                        });
                    }
                } else {
                    SendMessage searchErrorMessage = SendMessage.builder()
                            .text(MessagesEnum.NULL_MSG)
                            .chatId(update.getMessage().getChatId().toString())
                            .build();
                    msgList.add(searchErrorMessage);
                }
                break;
            default:
                SendMessage errorMessage = SendMessage.builder()
                        .text(MessagesEnum.ILLEGAL_ORDER)
                        .chatId(update.getMessage().getChatId().toString())
                        .build();
                msgList.add(errorMessage);
        }

        return msgList;
    }

    /**
     * 格式化消息
     * @param status 信息DTO
     * @return 格式化后的消息
     */
    private String formatStatusMessage(ServerDetailVO status) {
        return String.format(
                """
                        🌐 %s  (%s)
                        ====================
                        ID: %d    TAG: %s
                        IPv4: %s
                        IPv6: %s %s
                        平台: %s
                        CPU 型号: %s
                        运行时间: %.1f 天
                        负载: %.2f %.2f %.2f (1,5,15)
                        CPU: %.2f%%
                        内存: %.2f GB/%.2f GB (%.2f%%)
                        交换: %.2f GB/%.2f GB (%.2f%%)
                        磁盘: %.2f GB/%.2f GB (%.2f%%)
                        网速: ⬆️ %.2f KB/s ⬇️ %.2f KB/s
                        TCP连接数: %s
                        UDP连接数: %s
                        ====================
                        更新于: %s
                        """,
                status.getName(),
                status.getServerDetailHost().getCountryCode().toUpperCase(),
                status.getId(),
                status.getTag(),
                status.getIpv4(),
                status.getIpv6(),
                StringUtils.hasText(status.getIpv6()) ? "✅" : "❌",
                status.getServerDetailHost().getPlatform() + "-" + status.getServerDetailHost().getPlatformVersion() + " [" + status.getServerDetailHost().getArch() + "]",
                status.getServerDetailHost().getCpu(),
                ToolUtils.secondsToDays(Long.parseLong(status.getServerDetailStatus().getUptime())),
                Double.valueOf(status.getServerDetailStatus().getLoad1()),
                Double.valueOf(status.getServerDetailStatus().getLoad5()),
                Double.valueOf(status.getServerDetailStatus().getLoad15()),
                Double.valueOf(status.getServerDetailStatus().getCpu()),
                ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailStatus().getMemUsed())),
                ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailHost().getMemTotal())),
                (ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailStatus().getMemUsed())) /
                        ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailHost().getMemTotal()))) * 100.0,
                ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailStatus().getSwapUsed())),
                ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailHost().getSwapTotal())),
                (ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailStatus().getSwapUsed())) /
                        ("0".equals(status.getServerDetailHost().getSwapTotal()) ? 1.0 : ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailHost().getSwapTotal())))) * 100.0,
                ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailStatus().getDiskUsed())),
                ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailHost().getDiskTotal())),
                (ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailStatus().getDiskUsed())) /
                        ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailHost().getDiskTotal()))) * 100.0,
                ToolUtils.convertBitsPerSecondToKilobytesPerSecond(Integer.parseInt(status.getServerDetailStatus().getNetInSpeed())),
                ToolUtils.convertBitsPerSecondToKilobytesPerSecond(Integer.parseInt(status.getServerDetailStatus().getNetOutSpeed())),
                status.getServerDetailStatus().getTcpConnCount(),
                status.getServerDetailStatus().getUdpConnCount(),
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
        ).trim();
    }
}
