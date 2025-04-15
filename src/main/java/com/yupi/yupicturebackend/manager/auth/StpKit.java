package com.yupi.yupicturebackend.manager.auth;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

/**
 * 权限认证工具类
 */
@Component
public class StpKit {
    public static final String SPACE_TYPE = "space";

    /**
     * 默认的原生会话对象
     */
    public static final StpLogic DEFAULT = StpUtil.stpLogic;

    /**
     * 团队空间会话对象，管理space表所有的账号登录，权限认证
     */
    public static final StpLogic SPACE = new StpLogic(SPACE_TYPE);
}
