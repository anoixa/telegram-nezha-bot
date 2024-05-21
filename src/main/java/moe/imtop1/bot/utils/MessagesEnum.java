package moe.imtop1.bot.utils;

/**
 * 常量定义类
 * @author anoixa
 */
public class MessagesEnum {
    public static final String NULL_MSG = "参数不能为空";
    public static final String ILLEGAL_ORDER = "非法命令";
    public static final String ILLEGAL_PARAM = "非法参数";
    public static final String SUCCESS_REGISTERING_BOT = "Success registering the bot";
    public static final String ERROR_REGISTERING_BOT = "Error registering the bot";
    public static final String PROXY_INFO_TEMPLATE = "Proxy Type: %s  Address: %s";
    public static final String SERVER_STATUS_TEMPLATE =
            """
            %s %s  (%s)
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
            流量: ⬆️ %.2f GB ⬇️ %.2f GB
            TCP连接数: %s
            UDP连接数: %s
            ====================
            更新于: %s
            """;
}
