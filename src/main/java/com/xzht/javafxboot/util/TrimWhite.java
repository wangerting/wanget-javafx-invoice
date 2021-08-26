package com.xzht.javafxboot.util;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Erting.Wang
 * @desciption 裁剪/修剪具有空白空间的JPG文件
 * @date 2021/6/9 11:40 上午
 */
@Slf4j
public class TrimWhite {
    private BufferedImage img;

    public TrimWhite(File input) {
        try {
            img = ImageIO.read(input);
        } catch (IOException e) {
            throw new RuntimeException("Problem reading image", e);
        }
    }

    /**
     * 接口说明
     *
     * @param color Color.WHITE:去白边
     * @return void
     * @throws
     * @author Erting.Wang
     * @date 2021/6/10 10:59 上午
     */
    public void trim(Color color) {

        int width = getTrimmedWidth(color);
        int height = getTrimmedHeight(color);
        log.debug("width={},height={}", width, height);
        BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = newImg.createGraphics();
        g.drawImage(img, 0, 0, null);
        img = newImg;
    }

    public void write(File f) {
        try {
            ImageIO.write(img, "png", f);
        } catch (IOException e) {
            throw new RuntimeException("Problem writing image", e);
        }
    }

    private int getTrimmedWidth(Color color) {
        int height = this.img.getHeight();
        int width = this.img.getWidth();
        int trimmedWidth = 0;
        for (int i = 0; i < height; i++) {
            for (int j = width - 1; j >= 0; j--) {
                if (img.getRGB(j, i) != color.getRGB() && j > trimmedWidth) {
                    trimmedWidth = j;
                    break;
                }
            }
        }
        return trimmedWidth + 5;
    }

    private int getTrimmedHeight(Color color) {
        int width = this.img.getWidth();
        int height = this.img.getHeight();
        int trimmedHeight = 0;
        for (int i = 0; i < width; i++) {
            for (int j = height - 1; j >= 0; j--) {
                if (img.getRGB(i, j) != color.getRGB() && j > trimmedHeight) {
                    trimmedHeight = j;
                    break;
                }
            }
        }
        return trimmedHeight + 5;
    }
    public static String basePath = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/wangerting/9/";
    public static void main(String[] args) throws Exception {
//        String srcFile = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/未命名文件夹/4.png";
//        String targetFile = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/未命名文件夹/4-1.png";
//        String srcFile2 = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/未命名文件夹/4-2.png";
//        String targetFile2 = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/未命名文件夹/4-3.png";
//        trimWhite(srcFile, targetFile);
//        RotateImage.rotate180(targetFile, srcFile2);
//        trimWhite(srcFile2, targetFile2);
        String sourcePdf = basePath.concat("011002100311_37596105_北京固若石科技有限公司.pdf");
        //pdf转图片
        PdfboxUtil.pdfToImage(sourcePdf, basePath, "");
    }

    public static void trimWhite(String srcFile, String targetFile) {
        TrimWhite trim = new TrimWhite(new File(srcFile));
        trim.trim(Color.WHITE);
        trim.write(new File(targetFile));
    }

    public static void trimBlack(String srcFile, String targetFile) {
        TrimWhite trim = new TrimWhite(new File(srcFile));
        trim.trim(Color.BLACK);
        trim.write(new File(targetFile));
    }
}
