package com.xzht.javafxboot.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Erting.Wang
 * @desciption 类说明
 * @date 2021/6/3 5:36 下午
 */
@Slf4j
public class PdfboxUtil {

    public static String basePath = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/6/李晴/";
    //经过测试,dpi为96,100,105,120,150,200中,105显示效果较为清晰,体积稳定,dpi越高图片体积越大,一般电脑显示分辨率为96
    public static final float DEFAULT_DPI = 300;
    //默认转换的图片格式为jpg
    public static final String DEFAULT_FORMAT = "png";

    public static void main(String[] args) throws Exception {

        String sourcePdf = basePath.concat("moreToOne.pdf");
        PdfUtils.MergePdf(basePath, sourcePdf);
        String total = PdfUtils.readPdfGetMoney(sourcePdf);
        log.debug("total={}", total);
        //pdf转图片
        pdfToImage(sourcePdf, basePath);
        //图片转pdf
        imagesToPdf(sourcePdf, basePath);

        //4合1 pdf
        String targetPdf = basePath + "4to1.pdf";
        PdfUtils.merge4PagesIntoOne(sourcePdf, targetPdf);
        //删除生成的图片
        delImages(basePath);

    }

    /**
     * pdf转图片
     *
     * @param pdfPath PDF路径
     * @imgPath img路径
     * @page_end 要转换的页码，也可以定义开始页码和结束页码，我这里只需要一页，根据需求自行添加
     */
    public static void pdfToImage(String pdfPath, String imgPath) {
        try {
            //图像合并使用参数
            // 总宽度
            int width = 0;
            int height = 0;
            // 保存一张图片中的RGB数据
            int[] singleImgRGB;
            int shiftHeight = 0;
            //保存每张图片的像素值
            BufferedImage imageResult = null;
            //利用PdfBox生成图像
            PDDocument pdDocument = PDDocument.load(new File(pdfPath));
            PDFRenderer renderer = new PDFRenderer(pdDocument);
            //循环每个页码  A4=2480×3508
            for (int i = 0, len = pdDocument.getNumberOfPages(); i < len; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, DEFAULT_DPI, ImageType.RGB);
                int imageWidth = image.getWidth();
                int imageHeight = image.getHeight();
                log.debug("imageWidth={},imageHeight={}", imageWidth, imageHeight);
                //计算高度和偏移量
                //使用第一张图片宽度;
                width = imageWidth;
                height = imageHeight;
                if (width < height) {
                    height = imageHeight / 2 - 100;
                }
                log.debug("width={},height={}", width, height);
                //保存每页图片的像素值
                imageResult = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                //这里有高度，可以将imageHeight*len，我这里值提取一页所以不需要
                singleImgRGB = image.getRGB(0, 0, width, height, null, 0, width);
                // 写入流中
                imageResult.setRGB(0, shiftHeight, width, height, singleImgRGB, 0, width);
                ImageIO.write(imageResult, DEFAULT_FORMAT, new File(imgPath.concat(String.valueOf(i + 1)).concat(".png")));
            }
            pdDocument.close();
            // 写图片

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param pdfFile    生成pdf文件
     * @param imagesPath 需要转换的图片路径的数组
     */
    public static void imagesToPdf(String pdfFile, String imagesPath) {
        try {
            File file = new File(pdfFile);
            // 第一步：创建一个document对象。
            Document document = new Document();
            document.setMargins(0, 0, 0, 0);
            // 第二步：
            // 创建一个PdfWriter实例，
            PdfWriter.getInstance(document, new FileOutputStream(file));
            // 第三步：打开文档。
            document.open();
            // 第四步：在文档中增加图片。
            File files = new File(imagesPath);
            String[] images = files.list();
            int len = images.length;

            for (int i = 0; i < len; i++) {
                if (images[i].toLowerCase().endsWith(".bmp")
                        || images[i].toLowerCase().endsWith(".jpg")
                        || images[i].toLowerCase().endsWith(".jpeg")
                        || images[i].toLowerCase().endsWith(".gif")
                        || images[i].toLowerCase().endsWith(".png")) {
                    String temp = imagesPath.concat(File.separator).concat(images[i]);
                    Image img = Image.getInstance(temp);
                    img.setAlignment(Image.ALIGN_CENTER);
                    // 根据图片大小设置页面，一定要先设置页面，再newPage（），否则无效
                    document.setPageSize(new Rectangle(img.getWidth(), img.getHeight()));
                    document.newPage();
                    document.add(img);
                }
            }
            // 第五步：关闭文档。
            document.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 删除目录下的图片
     *
     * @param imagesPath
     * @return void
     * @throws
     * @author Erting.Wang
     * @date 2021/6/4 10:30 上午
     */
    public static void delImages(String imagesPath) {
        File files = new File(imagesPath);
        for (String file : files.list()) {
            if (file.toLowerCase().endsWith(".bmp")
                    || file.toLowerCase().endsWith(".jpg")
                    || file.toLowerCase().endsWith(".jpeg")
                    || file.toLowerCase().endsWith(".gif")
                    || file.toLowerCase().endsWith(".png")) {
                String temp = imagesPath.concat(File.separator).concat(file);
                FileIoUtils.deleteFile(temp);
            }
        }
    }

    public static String getNumber(String str) {
        String regEx = "[^0-9.]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
