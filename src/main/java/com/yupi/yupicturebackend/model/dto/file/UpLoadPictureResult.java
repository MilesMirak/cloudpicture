package com.yupi.yupicturebackend.model.dto.file;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

/**
 * 文件上传结果
 */
@Data
public class UpLoadPictureResult {

    /**
     * 图片 url
     */
    private String url;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 图片体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private int picWidth;

    /**
     * 图片高度
     */
    private int picHeight;

    /**
     * 图片宽高比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;
}
