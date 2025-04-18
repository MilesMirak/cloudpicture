package com.yupi.yupicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 批量抓取图片请求
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {
    /**
     * 搜素词
     */
    private String searchText;
    /**
     * 抓取数量
     */
    private Integer count=10;

    /**
     * 图片名称前缀
     */
    private String namePrefix;

    private static final long serialVersionUID = 1L;
}
