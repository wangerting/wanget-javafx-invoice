package com.xzht.javafxboot.util;


import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.graphics.layer.PdfLayer;
import com.spire.pdf.graphics.layer.PdfLayerCollection;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Erting.Wang
 * @desciption 类说明
 * @date 2021/3/25 6:16 下午
 */
@Slf4j
public class SpirePdfUtil {
    public static String basePath = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/0318/";

    public static void main(String[] args) throws IOException {
//        deleteImageSign(basePath + "其它_59.40元_2021.03.03_北京京东世纪信息技术有限公司.pdf");
        getImages(basePath + "其它_59.40元_2021.03.03_北京京东世纪信息技术有限公司.pdf");

//        readPdf(basePath + "其它_59.40元_2021.03.03_北京京东世纪信息技术有限公司.pdf", basePath + "ExtractText.txt");
//        getLayer(basePath + "其它_59.40元_2021.03.03_北京京东世纪信息技术有限公司.pdf");
    }

    public static void getLayer(String pdfFile) {
        //加载文档
        PdfDocument pdf = new PdfDocument();
        pdf.loadFromFile(pdfFile);
        PdfLayerCollection layers = pdf.getLayers();
        log.debug("layers={}", layers.toString());
        for (int i = 0; i < layers.getCount(); i++) {
            PdfLayer pdfLayer = layers.get(i);
            log.debug("pdfLayer={}", pdfLayer);
        }

//        //根据图层名称索引，删除图层
//        pdf.getLayers().removeLayer("字符串");
//
//        //保存文档
//        pdf.saveToFile("RemoveLayer.pdf", FileFormat.PDF);
//        pdf.dispose();
    }

    public static void deleteImageSign(String path) {
        //创建PDF实例
        PdfDocument doc = new PdfDocument();
        //加载PDF源文档
        doc.loadFromFile(path);
        //获取文档的第一页
        PdfPageBase page = doc.getPages().get(0);
        //删除第一页的第一张图
        page.deleteImage(0);
        //保存文档
        doc.saveToFile(basePath + "4to1.pdf");
        doc.close();
    }

    public static void getImages(String path) throws IOException {
        //创建PDF实例
        PdfDocument pdf = new PdfDocument();
        //加载PDF源文件
        pdf.loadFromFile(path);

        //遍历PDF每一页
        for (int i = 0; i < pdf.getPages().getCount(); i++) {
            //获取PDF页面
            PdfPageBase page = pdf.getPages().get(i);
            int index = 0;
            for (BufferedImage image : page.extractImages()) {
                File output = new File(basePath + "output/" + String.format("Image_%d.png", index++));
                //将图片保存为PNG格式文件
                ImageIO.write(image, "PNG", output);
                log.debug("index={}", index);
            }
        }
    }

    /**
     * 读取 pdf 内容
     *
     * @param pdfFile
     * @param txtFile "ExtractText.txt"
     * @return void
     * @throws
     * @author Erting.Wang
     * @date 2021/3/26 9:55 上午
     */
    public static void readPdf(String pdfFile, String txtFile) {
        //创建PdfDocument实例
        PdfDocument doc = new PdfDocument();
        //加载PDF文件
        doc.loadFromFile(pdfFile);

        //创建StringBuilder实例
        StringBuilder sb = new StringBuilder();

        PdfPageBase page;
        //遍历PDF页面，获取每个页面的文本并添加到StringBuilder对象
        for (int i = 0; i < doc.getPages().getCount(); i++) {
            page = doc.getPages().get(i);
            sb.append(page.extractText(true));
        }
        FileWriter writer;
        try {
            //将StringBuilder对象中的文本写入到文本文件
            writer = new FileWriter(txtFile);
            writer.write(sb.toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        doc.close();
    }
}
