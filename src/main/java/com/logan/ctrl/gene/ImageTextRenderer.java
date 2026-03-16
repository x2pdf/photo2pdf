package com.logan.ctrl.gene;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Iterator;

/**
 * ImageTextRenderer - render text into JPEG images.
 * <p>
 * Two static methods:
 * - renderTextImage(...) : pixel-precise rendering based on given aspect ratio and long side pixels.
 * - renderTextImage2(...): A4-like layout where fonts and layout scale proportionally with output pixels.
 * <p>
 * Both return byte[] of JPEG data (highest quality when photoQuality=1f).
 */
public class ImageTextRenderer {

    /**
     * 渲染文本为 JPEG 图片并返回图片 bytes。
     * <p>
     * 参数与你要求对应，若传入 null 或 非法数值，则使用默认值（见代码）。
     * <p>
     * 返回：byte[] JPEG 数据（photoQuality 范围 0..1，默认为 1.0f；edgePixels 范围 0..500，默认 20）。
     * <p>
     * 可能抛出 IOException（写入 JPEG 时）。
     */
    public static byte[] renderTextImage(
            String title,
            String text,
            String subTitle,
            String subTitleNote,
            String photoBackGroundColor, // e.g. "220,220,220"
            String photoEdgeColor,       // e.g. "255,255,255"
            Integer edgePixels,         // default 20
            Integer titleFontSize,      // default 16
            Integer textFontSize,       // default 20
            Integer subTitleFontSize,   // default 12
            Integer subTitleNoteFontSize,// default 10
            Integer width,              // default 1080
            Float photoQuality          // default 1.0f
    ) throws IOException {

        // --- Defaults & sanitize inputs ---
        if (photoBackGroundColor == null) photoBackGroundColor = "220,220,220";
        if (photoEdgeColor == null) photoEdgeColor = "255,255,255";
        int edge = (edgePixels == null) ? 20 : Math.max(0, Math.min(500, edgePixels));
        int bodySize = (textFontSize == null || textFontSize <= 0) ? 20 : textFontSize;
        int tSize = (titleFontSize == null || titleFontSize <= 0) ? 16 : titleFontSize;
        int stSize = (subTitleFontSize == null || subTitleFontSize <= 0) ? 12 : subTitleFontSize;
        int stnSize = (subTitleNoteFontSize == null || subTitleNoteFontSize <= 0) ? 10 : subTitleNoteFontSize;
        int imgWidth = (width == null || width <= 0) ? 1080 : width;
        float quality = (photoQuality == null) ? 1.0f : Math.max(0f, Math.min(1f, photoQuality));

        // Parse colors "r,g,b"
        Color bgColor = parseColor(photoBackGroundColor, new Color(220, 220, 220));
        Color edgeColor = parseColor(photoEdgeColor, Color.WHITE);

        // Margins and fixed offsets (as required)
        final int titleTopFromImage = 80; // 主标题距离图片上边缘 40 px（绝对）
        final int bodyLeftFromImage = 80; // 正文距离图片左边缘 40 px（绝对）
        final int bodyRightFromImage = 80; // 右边界留白
        final int bottomExtra = 80; // 整体比文本实际需要多 40 px

        // Fonts: 使用系统默认字体家族（会由JRE做回退以支持 emoji）
        String baseFamily = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames()[0];

        Font titleFont = new Font("SansSerif", Font.BOLD, tSize);
        Font bodyFont = new Font("SansSerif", Font.PLAIN, bodySize);
        Font subTitleFont = new Font("SansSerif", Font.BOLD, stSize);
        Font subTitleNoteFont = new Font("SansSerif", Font.PLAIN, stnSize);


        // Create temporary Graphics2D to measure layout (1-pixel image)
        BufferedImage tmpImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = tmpImg.createGraphics();
        enableQualityRenderingHints(g2);
        FontRenderContext frc = g2.getFontRenderContext();

        // compute inner drawing area (inside frame)
        int innerX = edge;
        int innerY = edge;
        int innerWidth = imgWidth - 2 * edge;
        if (innerWidth <= 0) innerWidth = Math.max(1, imgWidth - 2 * edge);

        // Horizontal positions according to requirement (absolute from image edges)
        int titleY = Math.max(titleTopFromImage, innerY + 10); // ensure not inside frame
        int bodyX = Math.max(bodyLeftFromImage, innerX + 10);
        int bodyAvailableWidth = imgWidth - bodyX - Math.max(bodyRightFromImage, innerX + 10);

        // We will measure height by summing measured layout heights
        float yCursor = 0f;
        float measuredHeight = 0f;

        // 1) Title height (if present)
        if (title != null && !title.isEmpty()) {
            TextLayout tLayout = new TextLayout(title, titleFont, frc);
            measuredHeight += tLayout.getAscent() + tLayout.getDescent() + tLayout.getLeading();
            // leave a small gap after title
            measuredHeight += 8;
        } else {
            // if no title, titleTopFromImage still reserves nothing; body should start after innerY + something
        }

        // 2) Body text: preserve explicit newlines. For each paragraph, measure wrapped lines
        if (text == null) text = "";
        String[] paragraphs = text.split("\n", -1); // keep trailing empties

        float bodyTotalHeight = 0f;
        float lineSpacingExtra = Math.max(2f, bodySize * 0.1f); // small extra spacing
        for (String para : paragraphs) {
            if (para.length() == 0) {
                // blank line -> add one line height
                TextLayout layout = new TextLayout(" ", bodyFont, frc);
                bodyTotalHeight += layout.getAscent() + layout.getDescent() + layout.getLeading() + lineSpacingExtra;
                continue;
            }
            AttributedString aStr = new AttributedString(para);
            aStr.addAttribute(TextAttribute.FONT, bodyFont);
            AttributedCharacterIterator it = aStr.getIterator();
            LineBreakMeasurer measurer = new LineBreakMeasurer(it, frc);
            int paraStart = it.getBeginIndex();
            int paraEnd = it.getEndIndex();
            measurer.setPosition(paraStart);
            while (measurer.getPosition() < paraEnd) {
                TextLayout layout = measurer.nextLayout(bodyAvailableWidth);
                bodyTotalHeight += layout.getAscent() + layout.getDescent() + layout.getLeading() + lineSpacingExtra;
            }
        }
        measuredHeight += bodyTotalHeight;

        // 3) Reserve space for subtitle + note (place near bottom). But per spec, subtitle near bottom of image,
        // so we'll compute total height = top reserved + body height + subtitle heights + bottomExtra
        float subTotalHeight = 0f;
        if (subTitle != null && !subTitle.isEmpty()) {
            TextLayout sLayout = new TextLayout(subTitle, subTitleFont, frc);
            subTotalHeight += sLayout.getAscent() + sLayout.getDescent() + sLayout.getLeading();
            subTotalHeight += 6; // gap
        }
        if (subTitleNote != null && !subTitleNote.isEmpty()) {
            TextLayout snLayout = new TextLayout(subTitleNote, subTitleNoteFont, frc);
            subTotalHeight += snLayout.getAscent() + snLayout.getDescent() + snLayout.getLeading();
        }

        measuredHeight += subTotalHeight;
        measuredHeight += bottomExtra; // extra padding per requirement

        // But we must ensure title distance from top is respected:
        // totalHeight must be at least titleTopFromImage + titleHeight + body etc.
        // We'll compute absolute top content start:
        float titleHeight = 0f;
        if (title != null && !title.isEmpty()) {
            TextLayout tLayout = new TextLayout(title, titleFont, frc);
            titleHeight = tLayout.getAscent() + tLayout.getDescent() + tLayout.getLeading();
        }
        // Content measuredHeight currently is titleHeight+body+sub+bottomExtra; but title should start at titleY,
        // so final image height must be at least titleY + titleHeight + (measuredHeight - titleHeight)
        float minHeightByTitle = titleY + measuredHeight;
        int finalHeight = Math.max((int) Math.ceil(minHeightByTitle), (int) Math.ceil(measuredHeight + edge * 2 + 10));
        // ensure a little minimum
        finalHeight = Math.max(finalHeight, 200);

        g2.dispose();

        // --- Create final image and draw ---
        BufferedImage out = new BufferedImage(imgWidth, finalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        enableQualityRenderingHints(g);

        // Fill overall background (grey)
        g.setColor(bgColor);
        g.fillRect(0, 0, imgWidth, finalHeight);

        // Draw inner "frame" (white rectangle inset by edge pixels)
        g.setColor(edgeColor);
        g.fillRect(innerX, innerY, innerWidth, Math.max(1, finalHeight - 2 * edge));

        // Now compute where to render title/body/subtitle relative to image coordinates.
        // Title: centered horizontally, y = titleY (baseline to be computed)
        float renderY = titleY;
        if (title != null && !title.isEmpty()) {
            g.setFont(titleFont);
            FontMetrics fm = g.getFontMetrics();
            // TextLayout to compute ascent for baseline
            TextLayout tLayout = new TextLayout(title, titleFont, g.getFontRenderContext());
            float ascent = tLayout.getAscent();
            // baseline y position: renderY + ascent
            float baseline = renderY + ascent;
            // center horizontally
            float titleWidth = (float) tLayout.getAdvance();
            float titleX = (imgWidth - titleWidth) / 2f;
            // draw
            g.setColor(Color.BLACK);
            tLayout.draw(g, titleX, baseline);
            renderY = baseline + tLayout.getDescent() + tLayout.getLeading() + 8; // move cursor below title
        } else {
            // if no title, set renderY near innerY + 10
            renderY = Math.max(innerY + 10, titleY);
        }

        // Body: start at bodyX, render lines left-aligned
        float bodyStartY = renderY;
        float yPos = bodyStartY;
        g.setFont(bodyFont);
        g.setColor(Color.BLACK);
        FontRenderContext finalFrc = g.getFontRenderContext();
        float paraGap = Math.max(2f, bodySize * 0.1f);

        for (String para : paragraphs) {
            if (para.length() == 0) {
                // blank line
                TextLayout blank = new TextLayout(" ", bodyFont, finalFrc);
                yPos += blank.getAscent();
                yPos += blank.getDescent() + blank.getLeading() + paraGap;
                continue;
            }
            AttributedString aStr = new AttributedString(para);
            aStr.addAttribute(TextAttribute.FONT, bodyFont);
            AttributedCharacterIterator it = aStr.getIterator();
            LineBreakMeasurer measurer = new LineBreakMeasurer(it, finalFrc);
            int paraStart = it.getBeginIndex();
            int paraEnd = it.getEndIndex();
            measurer.setPosition(paraStart);
            while (measurer.getPosition() < paraEnd) {
                TextLayout layout = measurer.nextLayout(bodyAvailableWidth);
                float ascent = layout.getAscent();
                float descent = layout.getDescent();
                float leading = layout.getLeading();
                // baseline y for this line:
                float baseline = yPos + ascent;
                layout.draw(g, bodyX, baseline);
                yPos = baseline + descent + leading + paraGap;
            }
        }

        // Subtitle and subtitle note: place near bottom of image (靠近底部居中)
        float bottomY = finalHeight - edge - bottomExtra / 2f; // baseline reference area
        // We'll draw subtitle above note if both exist.
        float subNoteHeight = 0f;
        if (subTitleNote != null && !subTitleNote.isEmpty()) {
            TextLayout snLayout = new TextLayout(subTitleNote, subTitleNoteFont, finalFrc);
            subNoteHeight = snLayout.getAscent() + snLayout.getDescent() + snLayout.getLeading();
        }
        float subHeight = 0f;
        if (subTitle != null && !subTitle.isEmpty()) {
            TextLayout sLayout = new TextLayout(subTitle, subTitleFont, finalFrc);
            subHeight = sLayout.getAscent() + sLayout.getDescent() + sLayout.getLeading();
        }
        // compute baseline positions
        float noteBaseline = bottomY;
        if (subTitleNote != null && !subTitleNote.isEmpty() && subTitle != null && !subTitle.isEmpty()) {
            // place note below subtitle: subtitle baseline = noteBaseline - (noteHeight + gap)
            noteBaseline = bottomY;
            float noteAscent = new TextLayout(subTitleNote, subTitleNoteFont, finalFrc).getAscent();
            float noteDescent = new TextLayout(subTitleNote, subTitleNoteFont, finalFrc).getDescent();
            float noteTotal = noteAscent + noteDescent;
            float gapBetween = 6f;
            float subtitleBaseline = noteBaseline - (noteTotal + gapBetween) - (new TextLayout(subTitle, subTitleFont, finalFrc).getDescent());
            // Draw subtitle centered
            TextLayout sLayout = new TextLayout(subTitle, subTitleFont, finalFrc);
            float sWidth = (float) sLayout.getAdvance();
            float sX = (imgWidth - sWidth) / 2f;
            sLayout.draw(g, sX, subtitleBaseline + sLayout.getAscent());
            // Draw note centered below
            TextLayout snLayout = new TextLayout(subTitleNote, subTitleNoteFont, finalFrc);
            float snWidth = (float) snLayout.getAdvance();
            float snX = (imgWidth - snWidth) / 2f;
            float snBaseline = subtitleBaseline + sLayout.getAscent() + sLayout.getDescent() + gapBetween + snLayout.getAscent();
            snLayout.draw(g, snX, snBaseline);
        } else if (subTitle != null && !subTitle.isEmpty()) {
            TextLayout sLayout = new TextLayout(subTitle, subTitleFont, finalFrc);
            float sWidth = (float) sLayout.getAdvance();
            float sX = (imgWidth - sWidth) / 2f;
            // place baseline so subtitle vertically sits a bit above bottomExtra
            float sBaseline = bottomY - (subTitleNote != null && !subTitleNote.isEmpty() ? (subNoteHeight + 6f) : 0f);
            sLayout.draw(g, sX, sBaseline);
        } else if (subTitleNote != null && !subTitleNote.isEmpty()) {
            TextLayout snLayout = new TextLayout(subTitleNote, subTitleNoteFont, finalFrc);
            float snWidth = (float) snLayout.getAdvance();
            float snX = (imgWidth - snWidth) / 2f;
            float snBaseline = bottomY;
            snLayout.draw(g, snX, snBaseline);
        }

        // dispose
        g.dispose();

        // --- Write JPEG to byte[] with specified quality ---
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter jpgWriter = null;
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (writers.hasNext()) jpgWriter = writers.next();

        if (jpgWriter == null) {
            // fallback: write PNG bytes instead (shouldn't normally happen)
            ImageIO.write(out, "png", baos);
            return baos.toByteArray();
        }

        ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
        if (jpgWriteParam.canWriteCompressed()) {
            jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpgWriteParam.setCompressionQuality(quality);
        }

        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        jpgWriter.setOutput(ios);
        IIOImage outputImage = new IIOImage(out, null, null);
        jpgWriter.write(null, outputImage, jpgWriteParam);
        ios.close();
        jpgWriter.dispose();

        return baos.toByteArray();
    }

    // ---------------- helper methods ----------------

    private static Color parseColor(String rgb, Color fallback) {
        if (rgb == null) return fallback;
        try {
            String[] parts = rgb.split(",");
            if (parts.length < 3) return fallback;
            int r = Integer.parseInt(parts[0].trim());
            int g = Integer.parseInt(parts[1].trim());
            int b = Integer.parseInt(parts[2].trim());
            r = clamp(r, 0, 255);
            g = clamp(g, 0, 255);
            b = clamp(b, 0, 255);
            return new Color(r, g, b);
        } catch (Exception e) {
            return fallback;
        }
    }

    private static int clamp(int v, int a, int b) {
        return Math.max(a, Math.min(b, v));
    }

    private static void enableQualityRenderingHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }


    /**
     * 将字节数组保存为图片文件
     *
     * @param image     图片的字节数组
     * @param imageName 文件名（含扩展名，例如：avatar.jpg、photo123.png）
     * @param path      保存目录路径（例如："/var/www/upload/" 或 "D:\\images\\"）
     *                  建议以 / 或 \\ 结尾
     * @return 保存成功返回 true，失败返回 false
     */
    public static boolean saveImage(byte[] image, String imageName, String path) {
        if (image == null || image.length == 0) {
            System.err.println("图片字节数组为空");
            return false;
        }

        if (imageName == null || imageName.trim().isEmpty()) {
            System.err.println("文件名不能为空");
            return false;
        }

        // 确保目录存在
        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.err.println("创建目录失败: " + path);
                return false;
            }
        }

        // 拼接完整文件路径
        String fullPath;
        if (path.endsWith("/") || path.endsWith("\\")) {
            fullPath = path + imageName;
        } else {
            fullPath = path + File.separator + imageName;
        }

        File file = new File(fullPath);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(image);
            fos.flush();
            System.out.println("图片保存成功: " + fullPath);
            return true;
        } catch (IOException e) {
            System.err.println("保存图片失败: " + fullPath);
            e.printStackTrace();
            return false;
        }
    }
}
