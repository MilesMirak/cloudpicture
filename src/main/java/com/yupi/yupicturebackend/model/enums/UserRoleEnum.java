package com.yupi.yupicturebackend.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRoleEnum {
    ADMIN("管理员", "admin"),
    USER("普通用户", "user");

    private final String text;

    private final String value;
    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.value.equals(value)) {
                return userRoleEnum;
            }
        }
        return null;
    }
}
