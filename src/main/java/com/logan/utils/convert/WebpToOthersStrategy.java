package com.logan.utils.convert;


import com.logan.config.PhotoFormat;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * WebP 转其他格式策略（WebP 由 TwelveMonkeys imageio-webp 插件通过 ImageIO SPI 读取）
 * 动图 WebP 只转换第一帧。插件读出的动图帧是 ANMF 中的原始子区域，不会按偏移合成到画布上，
 * 因此这里自行解析 RIFF 头获取画布尺寸和第一帧偏移，再把第一帧绘制到透明画布的对应位置。
 * 插件对已知长度的输入固定按 2048:1 的压缩比做解压炸弹校验，会误拒大面积纯色的无损 WebP（如截图、Logo），
 * 因此以未知长度的流读取，改由插件按 JVM 堆内存推算的上限（-Dcom.twelvemonkeys.imageio.maxImageBytes）校验。
 * author: Logan.qin
 * date: 2026/10/3
 */
public class WebpToOthersStrategy extends StandardImageConvertStrategy {

    private static final int VP8X_FLAG_ANIMATION = 0x02;

    @Override
    public boolean supports(String srcFilePath, String descFilePath) {
        if (srcFilePath == null || descFilePath == null) {
            return false;
        }
        String srcLower = srcFilePath.toLowerCase();
        String descLower = descFilePath.toLowerCase();

        return srcLower.endsWith("." + PhotoFormat.WEBP.getValue()) &&
                (descLower.endsWith("." + PhotoFormat.PNG.getValue()) ||
                        descLower.endsWith("." + PhotoFormat.JPEG.getValue()) ||
                        descLower.endsWith("." + PhotoFormat.JPG.getValue()));
    }

    @Override
    protected BufferedImage readImage(String srcFilePath) throws Exception {
        BufferedImage frame = readFirstFrame(srcFilePath);
        if (frame == null) {
            return null;
        }
        AnimationLayout layout = readAnimationLayout(srcFilePath);
        if (layout == null) {
            return frame;
        }

        BufferedImage canvas = new BufferedImage(layout.canvasWidth, layout.canvasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        try {
            g.drawImage(frame, layout.frameX, layout.frameY, null);
        } finally {
            g.dispose();
        }
        return canvas;
    }

    private BufferedImage readFirstFrame(String srcFilePath) throws IOException {
        try (InputStream in = new BufferedInputStream(new FileInputStream(srcFilePath));
             ImageInputStream iis = new MemoryCacheImageInputStream(in)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                return null;
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(iis, true, true);
                return reader.read(0);
            } finally {
                reader.dispose();
            }
        }
    }

    /**
     * 解析动图 WebP 的画布尺寸和第一帧偏移，参考 https://developers.google.com/speed/webp/docs/riff_container
     * @return 非动图或解析失败时返回 null
     */
    private AnimationLayout readAnimationLayout(String srcFilePath) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(srcFilePath, "r")) {
            byte[] header = new byte[30];
            if (raf.read(header) < header.length
                    || !"RIFF".equals(fourCC(header, 0))
                    || !"WEBP".equals(fourCC(header, 8))
                    || !"VP8X".equals(fourCC(header, 12))
                    || (header[20] & VP8X_FLAG_ANIMATION) == 0) {
                return null;
            }
            AnimationLayout layout = new AnimationLayout();
            layout.canvasWidth = uint24(header, 24) + 1;
            layout.canvasHeight = uint24(header, 27) + 1;

            // 从 VP8X 之后逐个遍历 chunk，找到第一个 ANMF
            long pos = 12;
            byte[] chunkHeader = new byte[14];
            while (pos + 8 <= raf.length()) {
                raf.seek(pos);
                if (raf.read(chunkHeader) < 8) {
                    return null;
                }
                long size = uint32(chunkHeader, 4);
                if ("ANMF".equals(fourCC(chunkHeader, 0))) {
                    layout.frameX = uint24(chunkHeader, 8) * 2;
                    layout.frameY = uint24(chunkHeader, 11) * 2;
                    return layout;
                }
                // chunk 数据按偶数字节对齐
                pos += 8 + size + (size & 1);
            }
            return null;
        }
    }

    private String fourCC(byte[] b, int off) {
        return new String(b, off, 4, StandardCharsets.US_ASCII);
    }

    private int uint24(byte[] b, int off) {
        return (b[off] & 0xFF) | (b[off + 1] & 0xFF) << 8 | (b[off + 2] & 0xFF) << 16;
    }

    private long uint32(byte[] b, int off) {
        return uint24(b, off) | (long) (b[off + 3] & 0xFF) << 24;
    }

    private static class AnimationLayout {
        int canvasWidth;
        int canvasHeight;
        int frameX;
        int frameY;
    }
}
