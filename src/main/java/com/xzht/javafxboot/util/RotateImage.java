package com.xzht.javafxboot.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Erting.Wang
 * @desciption 图片旋转工具类
 * @date 2021/6/9 12:47 下午
 */
public class RotateImage {
    /**
     * 对图片进行旋转
     *
     * @param src   被旋转图片
     * @param angel 旋转角度
     * @return 旋转后的图片
     */
    public static BufferedImage Rotate(Image src, int angel) {
        int src_width = src.getWidth(null);
        int src_height = src.getHeight(null);
        // 计算旋转后图片的尺寸
        Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(
                src_width, src_height)), angel);
        BufferedImage res = null;
        res = new BufferedImage(rect_des.width, rect_des.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = res.createGraphics();
        // 进行转换
        g2.translate((rect_des.width - src_width) / 2,
                (rect_des.height - src_height) / 2);
        g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);

        g2.drawImage(src, null, null);
        return res;
    }

    /**
     * 计算旋转后的图片
     *
     * @param src   被旋转的图片
     * @param angel 旋转角度
     * @return 旋转后的图片
     */
    public static Rectangle CalcRotatedSize(Rectangle src, int angel) {
        // 如果旋转的角度大于90度做相应的转换
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }

        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);

        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_height));
        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
        return new Rectangle(new Dimension(des_width, des_height));
    }

    //旋转180度（通过交换图像的整数像素RGB 值）
    public static BufferedImage rotate180(BufferedImage bi) {
        int width = bi.getWidth();
        int height = bi.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, bi.getType());
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bufferedImage.setRGB(width - i - 1, height - j - 1, bi.getRGB(i, j));
            }
        }
        return bufferedImage;
    }

    public static void main(String[] args) throws Exception {
        String targetFile = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/未命名文件夹/1-1.png";
        String srcFile2 = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/未命名文件夹/1-2.png";
        String targetFile2 = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/未命名文件夹/1-3.png";
        //旋转180度测试
//        BufferedImage image3 = ImageIO.read(new File(targetFile));
//        image3=rotate180(image3);
//        ImageIO.write(image3, "png", new File(srcFile2));

        TrimWhite.trimWhite(srcFile2, targetFile2);
    }

    public static void rotate180(String srcFile, String targetFile) throws Exception {
        BufferedImage image3 = ImageIO.read(new File(srcFile));
        image3 = rotate180(image3);
        ImageIO.write(image3, "png", new File(targetFile));
    }
}
