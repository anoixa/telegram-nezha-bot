package moe.imtop1.bot.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * /info命令数据VO
 * @author anoixa
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ServerInfoVO {
    private Integer serverSum;
    private BigDecimal memSum;
    private  BigDecimal useMemSum;
    private BigDecimal memUsageRate;
    private BigDecimal swapSum;
    private BigDecimal useSwapSum;
    private BigDecimal swapUsageRate;
    private BigDecimal diskSum;
    private BigDecimal useDiskSum;
    private BigDecimal diskUsageRate;
    private Integer physicalCores;
    private Integer virtualCores;
    private Integer sumCpuCores;
    private BigDecimal netOutTransferSum;
    private BigDecimal netInTransferSum;;
    private BigDecimal trafficParity;
    private BigDecimal netOutSpeedSum;
    private BigDecimal netInSpeedSum;

}
