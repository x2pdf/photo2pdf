package com.logan.utils.convert;


import java.util.ArrayList;
import java.util.List;

/**
 * 图片转换策略工厂
 * author: Logan.qin
 * date: 2026/8/15
 */
public class ImageConvertStrategyFactory {

    private static final List<ImageConvertStrategy> strategies = new ArrayList<>();

    static {
        registerStrategies();
    }

    /**
     * 注册所有策略（按优先级顺序）
     */
    private static void registerStrategies() {
        strategies.add(new OthersToJxlStrategy());
        strategies.add(new JxlToOthersStrategy());
        strategies.add(new OthersToAvifStrategy());
        strategies.add(new AvifToOthersStrategy());
        strategies.add(new WebpToOthersStrategy());
        strategies.add(new StandardImageConvertStrategy());
    }

    /**
     * 根据源文件和目标文件获取合适的转换策略
     * @param srcFilePath 源文件路径
     * @param descFilePath 目标文件路径
     * @return 匹配的转换策略，如果没有匹配则返回 null
     */
    public static ImageConvertStrategy getStrategy(String srcFilePath, String descFilePath) {
        for (ImageConvertStrategy strategy : strategies) {
            if (strategy.supports(srcFilePath, descFilePath)) {
                return strategy;
            }
        }
        return null;
    }

    /**
     * 注册自定义策略
     * @param strategy 自定义策略
     */
    public static void registerStrategy(ImageConvertStrategy strategy) {
        if (strategy != null && !strategies.contains(strategy)) {
            strategies.add(strategy);
        }
    }
}
