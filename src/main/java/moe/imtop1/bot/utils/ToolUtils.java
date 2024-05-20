package moe.imtop1.bot.utils;

import moe.imtop1.bot.domain.ServerInfo;
import moe.imtop1.bot.domain.vo.ServerDetailVO;

import java.util.List;

public class ToolUtils {

    public static Long getServerNum(List<ServerInfo> list) {
        return (long) list.size();
    }

    public static boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

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
                        运行时间: %s 天
                        负载: %s
                        CPU: %.2s%%
                        内存: %s
                        交换: %s
                        磁盘: %s
                        网速: ⬆️ %s ⬇️ %s
                        
                        更新于: %s
                        """,
                status.getName(),
                status.getTag(),
                status.getId(),
                status.getIpv4(),
                status.getIpv6(),
                status.getServerDetailHost().getPlatform(),
                status.getServerDetailHost().getCpu(),
                status.getServerDetailStatus().getUptime(),
                status.getServerDetailStatus().getLoad1() + " "
                        + status.getServerDetailStatus().getLoad5() + " "
                        + status.getServerDetailStatus().getLoad15(),
                status.getServerDetailStatus().getCpu(),
                status.getServerDetailStatus().getMemUsed(),
                status.getServerDetailStatus().getSwapUsed(),
                status.getServerDetailStatus().getDiskUsed(),
                status.getServerDetailStatus().getNetInSpeed(),
                status.getServerDetailStatus().getNetOutSpeed(),
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
        );
    }
}
