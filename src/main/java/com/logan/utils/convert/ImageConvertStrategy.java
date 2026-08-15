package com.logan.utils.convert;

/**
 * 图片转换策略接口
 * author: Logan.qin
 * date: 2026/8/15
 */
public interface ImageConvertStrategy {

    /**
     * 执行图片转换
     * @param srcFilePath 源文件路径
     * @param descFilePath 目标文件路径
     * @param quality 质量参数 (0-1)
     * @return 转换是否成功
     */
    boolean convert(String srcFilePath, String descFilePath, float quality);

    /**
     * 判断当前策略是否支持该转换
     * @param srcFilePath 源文件路径
     * @param descFilePath 目标文件路径
     * @return 是否支持
     */
    boolean supports(String srcFilePath, String descFilePath);
}