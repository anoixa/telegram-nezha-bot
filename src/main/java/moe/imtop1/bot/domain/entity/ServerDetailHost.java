package moe.imtop1.bot.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ServerDetailHost {
    @JsonProperty("Platform")
    private String platform;

    @JsonProperty("PlatformVersion")
    private String platformVersion;

    @JsonProperty("CPU")
    private List<String> cpu;

    @JsonProperty("MemTotal")
    private String memTotal;

    @JsonProperty("DiskTotal")
    private String diskTotal;

    @JsonProperty("SwapTotal")
    private String swapTotal;

    @JsonProperty("Arch")
    private String arch;

    @JsonProperty("Virtualization")
    private String virtualization;

    @JsonProperty("BootTime")
    private String bootTime;

    @JsonProperty("CountryCode")
    private String countryCode;

    @JsonProperty("Version")
    private String version;
}
