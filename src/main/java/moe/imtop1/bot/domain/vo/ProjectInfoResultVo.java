package moe.imtop1.bot.domain.vo;

import lombok.Data;
import moe.imtop1.bot.domain.entity.ServerInfo;

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