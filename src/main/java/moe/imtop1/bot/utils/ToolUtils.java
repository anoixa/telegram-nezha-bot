package moe.imtop1.bot.utils;

import moe.imtop1.bot.domain.ServerInfo;

import java.util.List;

public class ToolUtils {

    public static Long getServerNum(List<ServerInfo> list) {
        return (long) list.size();
    }

    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }
}
