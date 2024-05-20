package moe.imtop1.bot.utils;

import moe.imtop1.bot.domain.ServerInfo;
import moe.imtop1.bot.domain.vo.ServerDetailVO;

import java.util.List;

public class ToolUtils {
    /**
     * 判断一个list的总长度
     * @param list 输入list
     * @return list的长度
     */
    public static Long getNum(List<ServerInfo> list) {
        return (long) list.size();
    }

    /**
     * 判断字符串是否是数字
     * @param str 字符串
     * @return 是否是数字
     */
    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    /**
     * 将字节转换为千兆字节。
     * @param bytes 字节值。
     * @return 千兆字节值。
     */
    public static double bytesToGigabytes(long bytes) {
        return bytes / (double) (1024 * 1024 * 1024);
    }

    /**
     * 将网络速度从位每秒转换为千字节每秒。
     * @param bitsPerSecond 网络速度，单位为位每秒（bps）。
     * @return 网络速度，单位为千字节每秒（KB/s）。
     */
    public static double convertBitsPerSecondToKilobytesPerSecond(int bitsPerSecond) {
        return bitsPerSecond / 8.0 / 1024.0;
    }

    /**
     * 将秒转换为天。
     * @param seconds 秒数。
     * @return 对应的天数。
     */
    public static double secondsToDays(long seconds) {
        return seconds / 86400.0;
    }

    /**
     * 格式化消息
     * @param status 信息DTO
     * @return 格式化后的消息
     */
    public static String formatStatusMessage(ServerDetailVO status) {
        return String.format(
                """
                        🌐 %s
                        ====================
                        tag: %s    id: %d
                        ipv4: %s
                        ipv6: %s
                        平台: %s
                        CPU 型号: %s
                        运行时间: %.1f 天
                        负载: %.2f %.2f %.2f (1,5,15)
                        CPU: %.2f%%
                        内存: %.2f GB
                        交换: %.2f GB
                        磁盘: %.2f GB
                        网速: ⬆️ %.2f KB/s ⬇️ %.2f KB/s
                        TCP连接数: %s
                        UDP连接数: %s
                        ====================
                        更新于: %s
                        """,
                status.getName(),
                status.getTag(),
                status.getId(),
                status.getIpv4(),
                status.getIpv6(),
                status.getServerDetailHost().getPlatform(),
                status.getServerDetailHost().getCpu(),
                secondsToDays(Long.parseLong(status.getServerDetailStatus().getUptime())),
                Double.valueOf(status.getServerDetailStatus().getLoad1()),
                Double.valueOf(status.getServerDetailStatus().getLoad5()),
                Double.valueOf(status.getServerDetailStatus().getLoad15()),
                Double.valueOf(status.getServerDetailStatus().getCpu()),
                bytesToGigabytes(Long.parseLong(status.getServerDetailStatus().getMemUsed())),
                bytesToGigabytes(Long.parseLong(status.getServerDetailStatus().getSwapUsed())),
                bytesToGigabytes(Long.parseLong(status.getServerDetailStatus().getDiskUsed())),
                convertBitsPerSecondToKilobytesPerSecond(Integer.parseInt(status.getServerDetailStatus().getNetInSpeed())),
                convertBitsPerSecondToKilobytesPerSecond(Integer.parseInt(status.getServerDetailStatus().getNetOutSpeed())),
                status.getServerDetailStatus().getTcpConnCount(),
                status.getServerDetailStatus().getUdpConnCount(),
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
        );
    }
}
