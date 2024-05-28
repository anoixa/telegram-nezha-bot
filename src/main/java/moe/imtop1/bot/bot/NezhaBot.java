package moe.imtop1.bot.bot;

import lombok.extern.slf4j.Slf4j;
import moe.imtop1.bot.api.NezhaApi;
import moe.imtop1.bot.domain.entity.ServerInfo;
import moe.imtop1.bot.domain.template.MessageTemplate;
import moe.imtop1.bot.domain.vo.ServerDetailVO;
import moe.imtop1.bot.domain.vo.ServerInfoVO;
import moe.imtop1.bot.domain.enums.AppConstants;
import moe.imtop1.bot.utils.ToolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    /**
     * 接收并处理来自Telegram服务器的更新。
     * @param update 包含消息或回调查询的Telegram更新对象。
     */
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                String callbackData = update.getCallbackQuery().getData();
                long messageId = update.getCallbackQuery().getMessage().getMessageId();
                long chatId = update.getCallbackQuery().getMessage().getChatId();

                this.handleCallback(callbackData, chatId, messageId);
            } else if (update.hasMessage() && update.getMessage().hasText()) {
                String text = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();

                this.handleCommand(text, chatId);
            }
        } catch (TelegramApiException e) {
            log.error("Error processing update.", e);
        }

//        String text = update.getMessage().getText();
//        if (StringUtils.hasText(text)) {
//            List<SendMessage> msgList = this.getMessage(text, update);
//
//            msgList.stream().forEach(item -> {
//                try {
//                    execute(item);
//                } catch (TelegramApiException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        }


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
     * 处理回调查询，通常用于内联键盘按钮的交互。
     * @param callbackData 从回调中获取的数据，用于决定后续操作。
     * @param chatId 聊天的唯一标识符。
     * @param messageId 需要编辑的消息的ID。
     * @throws TelegramApiException 如果在执行Telegram API操作时发生错误。
     */
    private void handleCallback(String callbackData, long chatId, long messageId) throws TelegramApiException {
        String[] parts = callbackData.split(" ", 2);
        String command = parts[0];
        String param = parts.length > 1 ? parts[1] : null;

        List<SendMessage> messages = getMessage(command, chatId, param);
        for (SendMessage message : messages) {
            EditMessageText editMessageText = new EditMessageText();

            editMessageText.setMessageId(Math.toIntExact(messageId));
            editMessageText.setChatId(String.valueOf(chatId));
            editMessageText.setText(message.getText());
            editMessageText.setReplyMarkup((InlineKeyboardMarkup) message.getReplyMarkup());

            execute(editMessageText);
        }
    }

    /**
     * 处理用户通过文本消息发送的命令。
     * @param text 用户发送的文本，包含命令和可能的参数。
     * @param chatId 聊天的唯一标识符。
     * @throws TelegramApiException 如果在执行Telegram API操作时发生错误。
     */
    private void handleCommand(String text, long chatId) throws TelegramApiException {
        String[] parts = text.split(" ", 2);
        String command = parts[0];
        String param = parts.length > 1 ? parts[1] : null;

        List<SendMessage> messages = getMessage(command, chatId, param);
        for (SendMessage message : messages) {
            execute(message);
        }
    }

    /**
     * 创建包含“刷新”按钮的内联键盘。
     * 该按钮通过回调数据触发与原始命令相同的操作，允许用户刷新内容而不产生新的消息。
     * @param command 用户最初输入的命令，用于在点击按钮时重新执行。
     * @param param 命令可能包含的参数，将一并发送作为回调数据。
     * @return 构造好的内联键盘，准备附加到SendMessage对象中。
     */
    private InlineKeyboardMarkup createRefreshButton(String command, String param) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(AppConstants.REFRESH_BUTTON);
        inlineKeyboardButton.setCallbackData(command + (param != null ? " " + param : ""));

        rowInline.add(inlineKeyboardButton);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    /**
     * 创建带有内联键盘按钮的Telegram消息。
     * @param chatId 消息发送的目标聊天ID。
     * @param text 消息文本。
     * @param markupInline 包含内联键盘的布局。
     * @return 构造完成的SendMessage对象。
     */
    private SendMessage createMessage(String chatId, String text, InlineKeyboardMarkup markupInline) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        if (markupInline != null) {
            message.setReplyMarkup(markupInline);
        }
        return message;
    }

    /**
     * 根据输入命令和参数生成响应消息。
     * @param command 用户输入的命令。
     * @param chatId 消息所在的聊天ID。
     * @param param 命令后可能跟随的参数。
     * @return 一个包含所有响应消息的列表。
     */
    private List<SendMessage> getMessage(String command, long chatId, String param) {
        List<SendMessage> msgList = new ArrayList<>();

        // 根据命令分别处理
        switch (command) {
            case "/num":
                // 返回服务器信息列表
                //TODO 判断服务器是否在线
                String tagForQueryNum = StringUtils.hasText(param) ? param : null;
                List<ServerInfo> serverList = nezhaApi.getServerList(tagForQueryNum);
                if (serverList.isEmpty()) {
                    msgList.add(this.createMessage(
                            String.valueOf(chatId),
                            "No servers found.",
                            this.createRefreshButton(command, param)
                    ));
                } else {
                    String serverCountMsg = serverList.size() + " servers found.";
                    msgList.add(this.createMessage(
                            String.valueOf(chatId),
                            serverCountMsg,
                            this.createRefreshButton(command, param)
                    ));
                }
                break;
            case "/id":
                //根据ID返回服务器的详细信息
                try {
                    long serverId = Long.parseLong(param);
                    List<ServerDetailVO> serverDetailListById = nezhaApi.getServerDetailList(null, serverId);
                    if (serverDetailListById != null) {
                        ServerDetailVO serverDetailVO = serverDetailListById.getFirst();
                        String detailMessage = formatStatusMessage(serverDetailVO);
                        msgList.add(this.createMessage(
                                String.valueOf(chatId),
                                detailMessage,
                                this.createRefreshButton(command, param)
                        ));
                    } else {
                        msgList.add(this.createMessage(
                                String.valueOf(chatId),
                                "Server with ID " + param + " not found.",
                                this.createRefreshButton(command, null)
                        ));
                    }
                } catch (NumberFormatException e) {
                    msgList.add(this.createMessage(
                            String.valueOf(chatId),
                            "Invalid ID format.",
                            this.createRefreshButton(command, null)
                    ));
                }
                break;
            case "/search":
                //根据名称搜索服务器
                List<ServerDetailVO> serverDetailListLikeById = nezhaApi.getServerDetailList(null, null);
                List<ServerDetailVO> collect = serverDetailListLikeById.stream()
                        .filter(item -> item.getName().contains(param))
                        .toList();
                if (collect.isEmpty()) {
                    msgList.add(this.createMessage(
                            String.valueOf(chatId),
                            "No servers match your query.",
                            this.createRefreshButton(command, param)));
                } else {
                    collect.forEach(server -> {
                        String detailMessage = formatStatusMessage(server);
                        msgList.add(this.createMessage(
                                String.valueOf(chatId),
                                detailMessage,
                                this.createRefreshButton(command, param)
                        ));
                    });
                }
                break;
            case "/all":
                String tagForQueryAll = StringUtils.hasText(param) ? param : null;
                List<ServerDetailVO> serverDetailList = nezhaApi.getServerDetailList(tagForQueryAll, null);

                //获取数据
                ServerInfoVO severInfo = this.getSeverInfo(serverDetailList);
                String msg = this.formatStatusMessage(severInfo);

                msgList.add(this.createMessage(
                        String.valueOf(chatId),
                        msg,
                        this.createRefreshButton(command, param)
                ));

                break;
            default:
                if (ToolUtils.containsSlash(command, "/")) {
                    SendMessage errorMessage = SendMessage.builder()
                            .text(AppConstants.ILLEGAL_ORDER)
                            .chatId(chatId)
                            .build();

                    msgList.add(errorMessage);
                }
        }

        return msgList;
    }

    /**
     * 查询全部服务器基本信息
     * @param serverDetailList 服务器详细列表
     * @return 整体服务器详细对象
     */
    private ServerInfoVO getSeverInfo(List<ServerDetailVO> serverDetailList) {
        //服务器数量
        int serverSum = serverDetailList.size();

        //内存总数
        BigDecimal memSum = serverDetailList.stream()
                .map(item -> new BigDecimal(item.getServerDetailHost().getMemTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //使用的总内存
        BigDecimal useMemSum = serverDetailList.stream()
                .map(item -> new BigDecimal(item.getServerDetailStatus().getMemUsed()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //总体内存使用率
        BigDecimal memUsageRate = useMemSum.divide(memSum, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        //swap总数
        BigDecimal swapSum = serverDetailList.stream()
                .map(item -> new BigDecimal(item.getServerDetailHost().getSwapTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //使用的总swap
        BigDecimal useSwapSum = serverDetailList.stream()
                .map(item -> new BigDecimal(item.getServerDetailStatus().getSwapUsed()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //总体swap使用率
        BigDecimal swapUsageRate = useSwapSum.divide(swapSum, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        //磁盘空间总数
        BigDecimal diskSum = serverDetailList.stream()
                .map(item -> new BigDecimal(item.getServerDetailHost().getDiskTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //使用的总磁盘空间
        BigDecimal useDiskSum = serverDetailList.stream()
                .map(item -> new BigDecimal(item.getServerDetailStatus().getDiskUsed()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //总体磁盘使用率
        BigDecimal diskUsageRate = useDiskSum.divide(diskSum, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        //CPU核心
        int physicalCores = 0;
        int virtualCores = 0;
        for (ServerDetailVO vo : serverDetailList){
            List<String> cpu = vo.getServerDetailHost().getCpu();

            if (cpu != null) {
                for (String s : cpu) {
                    try {
                        physicalCores += ToolUtils.getCores(s, "Physical Core");
                    } catch (IllegalArgumentException e) {
                        physicalCores += 0;
                    }
                    try {
                        virtualCores += ToolUtils.getCores(s, "Virtual Core");
                    } catch (IllegalArgumentException e) {
                        virtualCores += 0;
                    }
                }
            }
        }
        int sumCpuCores = physicalCores + virtualCores;

        //使用的总上行
        BigDecimal netOutTransferSum = serverDetailList.stream()
                .map(item -> new BigDecimal(item.getServerDetailStatus().getNetOutTransfer()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //使用的总下行
        BigDecimal netInTransferSum = serverDetailList.stream()
                .map(item -> new BigDecimal(item.getServerDetailStatus().getNetInTransfer()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 计算流量对等性
        BigDecimal totalTransferSum = netOutTransferSum.add(netInTransferSum);
        BigDecimal transferDifference = netOutTransferSum.subtract(netInTransferSum).abs();
        BigDecimal trafficParity = BigDecimal.ONE.subtract(
                transferDifference.divide(totalTransferSum, 4, RoundingMode.HALF_UP)
        ).multiply(BigDecimal.valueOf(100));

        //上行总速率
        BigDecimal netOutSpeedSum = serverDetailList.stream()
                .map(item -> new BigDecimal(item.getServerDetailStatus().getNetOutSpeed()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //下行总速率
        BigDecimal netInSpeedSum = serverDetailList.stream()
                .map(item -> new BigDecimal(item.getServerDetailStatus().getNetInSpeed()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //封装数据
        return new ServerInfoVO(
                serverSum, memSum, useMemSum, memUsageRate, swapSum, useSwapSum, swapUsageRate,diskSum, useDiskSum,
                diskUsageRate, physicalCores, virtualCores, sumCpuCores, netOutTransferSum, netInTransferSum,
                trafficParity, netOutSpeedSum, netInSpeedSum
        );
    }

    /**
     * 格式化消息
     * @param status 信息DTO
     * @return 格式化后的消息
     */
    private String formatStatusMessage(ServerDetailVO status) {
        return String.format(
                MessageTemplate.SERVER_STATUS_TEMPLATE,
                ToolUtils.countryCodeToFlagEmoji(status.getServerDetailHost().getCountryCode()),
                status.getName(),
                status.getServerDetailHost().getCountryCode().toUpperCase(),
                status.getId(),
                status.getTag(),
                status.getIpv4(),
                status.getIpv6(),
                StringUtils.hasText(status.getIpv6()) ? "✅" : "❌",
                status.getServerDetailHost().getPlatform() + "-" +
                        status.getServerDetailHost().getPlatformVersion() +
                        " [" + status.getServerDetailHost().getArch() + "]",
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
                        ("0".equals(status.getServerDetailHost().getSwapTotal()) ? 1.0 :
                                ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailHost().getSwapTotal())))) * 100.0,
                ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailStatus().getDiskUsed())),
                ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailHost().getDiskTotal())),
                (ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailStatus().getDiskUsed())) /
                        ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailHost().getDiskTotal()))) * 100.0,
                ToolUtils.convertBitsPerSecondToKilobytesPerSecond(Integer.parseInt(status.getServerDetailStatus().getNetInSpeed())),
                ToolUtils.convertBitsPerSecondToKilobytesPerSecond(Integer.parseInt(status.getServerDetailStatus().getNetOutSpeed())),
                ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailStatus().getNetOutTransfer())),
                ToolUtils.bytesToGigabytes(Long.parseLong(status.getServerDetailStatus().getNetInTransfer())),
                status.getServerDetailStatus().getTcpConnCount(),
                status.getServerDetailStatus().getUdpConnCount(),
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
        ).trim();
    }


    /**
     * 格式化消息(/all)
     * @param status 信息DTO
     * @return 格式化后的消息
     */
    private String formatStatusMessage(ServerInfoVO status){
        return String.format(
                MessageTemplate.ALL_SERVER_STATUS_TEMPLATE,
                status.getServerSum(),
                status.getSumCpuCores().longValue(),
                ToolUtils.bytesToGigabytes(status.getUseMemSum().doubleValue()),
                ToolUtils.bytesToGigabytes(status.getMemSum().doubleValue()),
                status.getMemUsageRate().doubleValue(),
                ToolUtils.bytesToGigabytes(status.getUseSwapSum().doubleValue()),
                ToolUtils.bytesToGigabytes(status.getSwapSum().doubleValue()),
                status.getSwapUsageRate().doubleValue(),
                ToolUtils.bytesToGigabytes(status.getUseDiskSum().doubleValue()),
                ToolUtils.bytesToGigabytes(status.getDiskSum().doubleValue()),
                status.getDiskUsageRate().doubleValue(),
                ToolUtils.convertBitsPerSecondToKilobytesPerSecond(status.getNetOutSpeedSum().doubleValue()),
                ToolUtils.convertBitsPerSecondToKilobytesPerSecond(status.getNetInSpeedSum().doubleValue()),
                ToolUtils.bytesToGigabytes(status.getNetOutTransferSum().doubleValue()),
                ToolUtils.bytesToGigabytes(status.getNetInTransferSum().doubleValue()),
                status.getTrafficParity().doubleValue(),
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
        ).trim();
    }
}
