package com.yupi.yupicturebackend.model.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用空间分析请求
 */
@Data
public class SpaceAnalyzeRequest implements Serializable {
    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 是否查询公开图片
     */
    private boolean queryPublic;

    /**
     * 是否查询所有图片
     */
    private boolean queryAll;

    private static final long serialVersionUID = 1L;
}
