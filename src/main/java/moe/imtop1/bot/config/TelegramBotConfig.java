package moe.imtop1.bot.config;

import lombok.extern.slf4j.Slf4j;
import moe.imtop1.bot.bot.NezhaBot;
import moe.imtop1.bot.domain.enums.AppConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Telegram Bot配置类
 * @author anoixa
 */
@Configuration
@Slf4j
public class TelegramBotConfig {

    @Value("${telegram.proxy.config.proxyType}")
    private String proxyType;
    @Value("${telegram.proxy.config.address}")
    private String address;
    @Value("${telegram.proxy.config.port}")
    private String port;

    /**
     * 代理配置Bean
     * @return
     */
    @Bean
    public DefaultBotOptions defaultBotOptions() {
        DefaultBotOptions botOptions = new DefaultBotOptions();
        botOptions.setProxyType(convertToProxyType(proxyType));
        if (botOptions.getProxyType() != DefaultBotOptions.ProxyType.NO_PROXY) {
            botOptions.setProxyHost(address);
            botOptions.setProxyPort(Integer.parseInt(port));

            log.info(String.format(AppConstants.PROXY_INFO_TEMPLATE, proxyType, address + ":" + port));
        }
        return botOptions;
    }

    /**
     * 机器人注册配置Bean
     * @param bot 注册的bot
     * @return
     */
    @Bean
    public TelegramBotsApi telegramBotsApi(NezhaBot bot) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);

            log.info(AppConstants.SUCCESS_REGISTERING_BOT + ": " + bot);

            return telegramBotsApi;
        } catch (TelegramApiException e) {
            throw new RuntimeException(AppConstants.ERROR_REGISTERING_BOT + ": ", e);
        }
    }

    private DefaultBotOptions.ProxyType convertToProxyType(String proxyTypeStr) {
        try {
            return DefaultBotOptions.ProxyType.valueOf(proxyTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid proxy type provided in configuration: " + proxyTypeStr);
        }
    }
}
