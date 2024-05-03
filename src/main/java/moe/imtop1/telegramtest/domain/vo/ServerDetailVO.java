package moe.imtop1.telegramtest.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import moe.imtop1.telegramtest.domain.ServerDetailHost;
import moe.imtop1.telegramtest.domain.ServerDetailStatus;
import moe.imtop1.telegramtest.domain.ServerInfo;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class ServerDetailVO extends ServerInfo {
    @JsonProperty("host")
    private ServerDetailHost serverDetailHost;

    @JsonProperty("status")
    private ServerDetailStatus serverDetailStatus;
}
