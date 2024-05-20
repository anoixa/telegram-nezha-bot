package moe.imtop1.bot.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ServerDetailStatus {
    @JsonProperty("CPU")
    private String cpu;

    @JsonProperty("MemUsed")
    private String memUsed;

    @JsonProperty("SwapUsed")
    private String swapUsed;

    @JsonProperty("DiskUsed")
    private String diskUsed;

    @JsonProperty("NetInTransfer")
    private String netInTransfer;

    @JsonProperty("NetOutTransfer")
    private String netOutTransfer;

    @JsonProperty("NetInSpeed")
    private String netInSpeed;

    @JsonProperty("NetOutSpeed")
    private String netOutSpeed;

    @JsonProperty("Uptime")
    private String uptime;

    @JsonProperty("Load1")
    private String load1;

    @JsonProperty("Load5")
    private String load5;

    @JsonProperty("Load15")
    private String load15;

    @JsonProperty("TcpConnCount")
    private String tcpConnCount;

    @JsonProperty("UdpConnCount")
    private String udpConnCount;

    @JsonProperty("ProcessCount")
    private String processCount;
}
