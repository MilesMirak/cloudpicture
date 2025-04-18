package com.yupi.yupicturebackend.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * 图片审核状态枚举
 */
@Getter
public enum PictureReviewStatusEnum {
    REVIEWING("审核中", 0),
    PASS("通过", 1),
    REJECT("不通过", 2);

    private final String text;

    private final int value;
    PictureReviewStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }
    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static PictureReviewStatusEnum getEnumByValue(int value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (PictureReviewStatusEnum pictureReviewStatusEnum : PictureReviewStatusEnum.values()) {
            if (pictureReviewStatusEnum.value == (value)) {
                return pictureReviewStatusEnum;
            }
        }
        return null;
    }
}
