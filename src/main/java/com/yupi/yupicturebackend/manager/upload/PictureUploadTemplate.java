package com.yupi.yupicturebackend.manager.upload;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.yupi.yupicturebackend.config.CosClientConfig;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.manager.CosManager;
import com.yupi.yupicturebackend.model.dto.file.UpLoadPictureResult;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

/**
 * 图片上传模板
 */
@Slf4j
public abstract class PictureUploadTemplate {
    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private CosManager cosManager;

    /**
     * 上传图片
     *
     * @param inputSource
     * @param uploadPathRrefix
     * @return
     */
    public UpLoadPictureResult uploadPicture(Object inputSource, String uploadPathRrefix) {
        // 1校验图片
        validPicture(inputSource);
        //2图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = getOriginFilename(inputSource);
        //自己拼接文件上传路径，而不是使用原始文件名称，可以增强文件安全性
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("/%s/%s", uploadPathRrefix, uploadFilename);
        //解析结果并返回
        File file = null;
        //上传文件
        try {
            // 3创建临时文件，获取文件到服务器
            file = File.createTempFile(uploadPath, null);
            // 处理文件来源
            processFile(inputSource,file);
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 4上传图片到对象存储
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 5获取图片信息对象，封装返回结果
            return bulidResult(originalFilename, file, uploadPath,imageInfo);
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            //临时文件清理
            this.deleteTempFile(file);
        }
    }

    /**
     * 校验输入源
     * @param inputSource
     */
    protected abstract void processFile(Object inputSource,File file) throws Exception;

    /**
     * 获取原始文件名
     * @param inputSource
     * @return
     */
    protected abstract String getOriginFilename(Object inputSource);

    /**
     * 处理输入源并生成本地临时文件
     * @param inputSource
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 封装返回结果
     * @param imageInfo
     * @param uploadPath
     * @param originalFilename
     * @param file
     * @return
     */
    private UpLoadPictureResult bulidResult(String originalFilename, File file,String uploadPath,ImageInfo imageInfo) {
        // 计算宽高
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        //封装返回结果
        UpLoadPictureResult upLoadPictureResult = new UpLoadPictureResult();
        upLoadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        upLoadPictureResult.setName(FileUtil.mainName(originalFilename));
        upLoadPictureResult.setPicSize(FileUtil.size(file));
        upLoadPictureResult.setPicWidth(picWidth);
        upLoadPictureResult.setPicHeight(picHeight);
        upLoadPictureResult.setPicScale(picScale);
        upLoadPictureResult.setPicFormat(imageInfo.getFormat());
        //返回可访问地址
        return upLoadPictureResult;
    }

    /**
     * 删除临时文件
     *
     * @param file
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        //删除临时文件
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }
}
