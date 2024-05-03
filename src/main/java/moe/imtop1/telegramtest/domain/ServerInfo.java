package moe.imtop1.telegramtest.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;

@Data
@ToString
public class ServerInfo {
    private Long id;
    private String name;
    private String tag;

    @JsonProperty("last_active")
    private String lastActive;

    private String ipv4;
    private String ipv6;

    @JsonProperty("valid_ip")
    private String validIp;
}
