package com.yupi.yupicturebackend.model.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间使用排行请求（仅管理员）
 */
@Data
public class SpaceRankAnalyzeRequest implements Serializable {

    /**
     * 获取前N个的空间 默认取前10个
     */
    private Integer topN=10;

    private static final long serialVersionUID = 1L;
}
