package com.xzht.javafxboot.util;

import com.itextpdf.text.exceptions.UnsupportedPdfException;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Erting.Wang
 * @desciption 类说明
 * @date 2021/3/26 10:22 上午
 */
@Slf4j
public class ItextPdfUtil {

    public static String basePath = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/未命名文件夹/";

    public static void main(String[] args) throws Exception {
//        getLayer(basePath + "其它_59.40元_2021.03.03_北京京东世纪信息技术有限公司.pdf");
        extractImage(basePath + "1.pdf");
    }

    public static void getLayer(String pdfFile) throws Exception {
        PdfReader reader = new PdfReader(pdfFile);
        PdfStamper stamper = new PdfStamper(reader, null);
        Map<String, PdfLayer> pdfLayers = stamper.getPdfLayers();
        log.debug("pdfLayers={}", pdfLayers);
        for (String key : pdfLayers.keySet()) {
            //这里的key虽然也是图层的名称，
            //但是是所有图层包括看不到的图层的名称，而且有多个同名的图层的话获取到的key是 图层(数字)
            PdfLayer pdfLayer = pdfLayers.get(key);
            //判断PDF图层是否显示
            if (pdfLayer.isOnPanel()) {
                System.out.println(pdfLayer.get(PdfName.NAME).toString());
            }
        }
    }

    public static void extractImage(String pdfFile) {
        PdfReader reader = null;
        try {
            //读取pdf文件
            reader = new PdfReader(pdfFile);
            //获得pdf文件的页数
            int sumPage = reader.getNumberOfPages();
            //读取pdf文件中的每一页
            for (int i = 1; i <= sumPage; i++) {
                //得到pdf每一页的字典对象
                PdfDictionary dictionary = reader.getPageN(i);
                //通过RESOURCES得到对应的字典对象
                PdfDictionary res = (PdfDictionary) PdfReader.getPdfObject(dictionary.get(PdfName.RESOURCES));
                //得到XOBJECT图片对象
                PdfDictionary xobj = (PdfDictionary) PdfReader.getPdfObject(res.get(PdfName.XOBJECT));
                if (xobj != null) {
                    for (Iterator it = xobj.getKeys().iterator(); it.hasNext(); ) {
                        PdfObject obj = xobj.get((PdfName) it.next());
                        if (obj.isIndirect()) {
                            PdfDictionary tg = (PdfDictionary) PdfReader.getPdfObject(obj);
                            PdfName type = (PdfName) PdfReader.getPdfObject(tg.get(PdfName.SUBTYPE));
                            if (PdfName.IMAGE.equals(type)) {
                                PdfObject object = PdfReader.getPdfObject(obj);
                                if (object.isStream()) {
                                    PRStream prstream = (PRStream) object;
                                    byte[] b;
                                    try {
                                        b = PdfReader.getStreamBytes(prstream);
                                    } catch (UnsupportedPdfException e) {
                                        b = PdfReader.getStreamBytesRaw(prstream);
                                    }
                                    FileOutputStream output = new FileOutputStream(String.format(basePath + "output/pdf_image_%d.png", i));
                                    output.write(b);
                                    output.flush();
                                    output.close();
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
