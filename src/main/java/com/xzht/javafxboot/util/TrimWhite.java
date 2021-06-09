package com.xzht.javafxboot.util;

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
public class TrimWhite {
    private BufferedImage img;

    public static int WIGHT_DIV = 20;
    public static int HEIGHT_DIV = 30;

    public TrimWhite(File input) {
        try {
            img = ImageIO.read(input);
        } catch (IOException e) {
            throw new RuntimeException("Problem reading image", e);
        }
    }

    public void trim() {
        int width = getTrimmedWidth();
        int height = getTrimmedHeight();

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

    private int getTrimmedWidth() {
        int height = this.img.getHeight();
        int width = this.img.getWidth();
        int trimmedWidth = 0;
        for (int i = 0; i < height; i++) {
            for (int j = width - 1; j >= 0; j--) {
                if (img.getRGB(j, i) != Color.WHITE.getRGB() && j > trimmedWidth) {
                    trimmedWidth = j;
                    break;
                }
            }
        }
        return trimmedWidth + WIGHT_DIV;
    }

    private int getTrimmedHeight() {
        int width = this.img.getWidth();
        int height = this.img.getHeight();
        int trimmedHeight = 0;
        for (int i = 0; i < width; i++) {
            for (int j = height - 1; j >= 0; j--) {
                if (img.getRGB(i, j) != Color.WHITE.getRGB() && j > trimmedHeight) {
                    trimmedHeight = j;
                    break;
                }
            }
        }
        return trimmedHeight + HEIGHT_DIV;
    }

    public static void main(String[] args) throws Exception {
        String srcFile = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/未命名文件夹/4.png";
        String targetFile = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/未命名文件夹/4-1.png";
        String srcFile2 = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/未命名文件夹/4-2.png";
        String targetFile2 = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/未命名文件夹/4-3.png";
        trimWhite(srcFile, targetFile);
        RotateImage.rotate180(targetFile, srcFile2);
        trimWhite(srcFile2, targetFile2);
    }

    public static void trimWhite(String srcFile, String targetFile) {
        TrimWhite trim = new TrimWhite(new File(srcFile));
        trim.trim();
        trim.write(new File(targetFile));
    }
}
