package moe.imtop1.telegramtest.domain.vo;

import lombok.Data;
import moe.imtop1.telegramtest.domain.ServerInfo;

import java.util.List;

/**
 * 返回实体
 * @author anoixa
 */
@Data
public class ProjectInfoResultVo {
    private String code;
    private String message;
    private List<ServerInfo> result;

}