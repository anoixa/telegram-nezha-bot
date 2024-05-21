package moe.imtop1.bot.utils;

import moe.imtop1.bot.domain.ServerInfo;

import java.util.List;

/**
 * 工具类
 * @author anoixa
 */
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
     * 将国家代码转换为对应的国旗表情符号。
     * @param countryCode 两个字母的国家代码（例如 "US"、"HK" 等）。
     * @return 对应国家的国旗表情符号。
     */
    public static String countryCodeToFlagEmoji(String countryCode) {
        if (countryCode == null || countryCode.length() != 2) {
            return "🌐";
        }

        countryCode = countryCode.toUpperCase();

        // 将每个字符转换为对应的区域指示符符号
        StringBuilder flagEmoji = new StringBuilder();
        for (char c : countryCode.toCharArray()) {
            int flagOffset = c - 'A' + 0x1F1E6;
            flagEmoji.appendCodePoint(flagOffset);
        }

        return flagEmoji.toString();
    }

}
