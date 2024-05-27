package moe.imtop1.bot.domain.template;

/**
 * 消息模板
 * @author anoixa
 */
public class MessageTemplate {
    public static final String SERVER_STATUS_TEMPLATE =
            """
            %s %s  (%s)
            ===========================
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
            ===========================
            更新于: %s
            """;
    public static final String ALL_SERVER_STATUS_TEMPLATE =
            """
            统计信息
            ===========================
            CPU核心数: %d
            内存: %.2f GB/%.2f GB (%.2f%%)
            交换: %.2f GB/%.2f GB (%.2f%%)
            磁盘: %.2f GB/%.2f GB (%.2f%%)
            网速: ⬆️ %.2f KB/s ⬇️ %.2f KB/s
            流量: ⬆️ %.2f GB ⬇️ %.2f GB
            流量对等性: %.2f%%
            ===========================
            更新于: %s
            """;
}
