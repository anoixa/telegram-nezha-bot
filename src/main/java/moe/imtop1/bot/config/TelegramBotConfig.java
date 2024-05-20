package moe.imtop1.bot.config;

import lombok.extern.slf4j.Slf4j;
import moe.imtop1.bot.bot.BotTestOne;
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

    @Bean
    public DefaultBotOptions defaultBotOptions() {
        DefaultBotOptions botOptions = new DefaultBotOptions();
        botOptions.setProxyType(convertToProxyType(proxyType));
        if (botOptions.getProxyType() != DefaultBotOptions.ProxyType.NO_PROXY) {
            botOptions.setProxyHost(address);
            botOptions.setProxyPort(Integer.parseInt(port));

            log.info("Proxy Type: " + proxyType + " Address: " + address + ":" + port);
        }
        return botOptions;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(BotTestOne bot) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);

            log.info("Success registering the bot: " + bot);

            return telegramBotsApi;
        } catch (TelegramApiException e) {
            throw new RuntimeException("Error registering the bot.", e);
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
