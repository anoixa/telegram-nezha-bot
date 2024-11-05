package moe.imtop1.bot.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import moe.imtop1.bot.domain.entity.ServerDetailHost;
import moe.imtop1.bot.domain.entity.ServerDetailStatus;


@Data
@ToString
public class ServerDetailVO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("tag")
    private String tag;

    @JsonProperty("last_active")
    private String lastActive;

    @JsonProperty("ipv4")
    private String ipv4;

    @JsonProperty("ipv6")
    private String ipv6;

    @JsonProperty("valid_ip")
    private String validIp;

    @JsonProperty("display_index")
    private String displayIndex;

    @JsonProperty("hide_for_guest")
    private Boolean hideForGuest;

    @JsonProperty("host")
    private ServerDetailHost serverDetailHost;

    @JsonProperty("status")
    private ServerDetailStatus serverDetailStatus;
}
