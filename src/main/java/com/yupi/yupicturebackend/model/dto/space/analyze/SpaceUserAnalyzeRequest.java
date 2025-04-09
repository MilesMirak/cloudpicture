package com.yupi.yupicturebackend.model.dto.space.analyze;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户空间上传行为分析请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceUserAnalyzeRequest extends SpaceAnalyzeRequest{
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 时间维度: day/week/mouth
     */
    private String timeDimension;
}
