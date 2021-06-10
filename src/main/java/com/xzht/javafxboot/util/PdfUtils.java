package com.xzht.javafxboot.util;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.util.CollectionUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Erting.Wang
 * @desciption 类说明
 * @date 2021/3/8 9:49 上午
 */
@Slf4j
public class PdfUtils {
    public static String basePath = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/6/李晴/";

    public static void main(String[] args) throws Exception {
//        String sourcePdf = basePath + "moreToOne.pdf";
//        String targetPdf = basePath + "4to1-1.pdf";
//        FileIoUtils.deleteFile(sourcePdf);
//        FileIoUtils.deleteFile(targetPdf);
//        MergePdf(basePath, sourcePdf);
//        merge4PagesIntoOne(sourcePdf, targetPdf);
//        readPdfGetMoney(sourcePdf);
//        getImages(basePath + "其它_59.40元_2021.03.03_北京京东世纪信息技术有限公司.pdf");
        readPdfGetMoney("/Users/wangerting/Desktop/牛交所/牛交所/发票/0104/st/牛交所 Compliance Service Invoice - Jan 2021.pdf", "投資");
    }

    public static boolean isExistKeyWords(String keyWords, PDDocument document, int page) throws Exception {
        if (StringUtils.isEmpty(keyWords)) {
            return false;
        }
        // 文本内容
        PDFTextStripper stripper = new PDFTextStripper();
        // 设置按顺序输出
        stripper.setSortByPosition(true);
        stripper.setStartPage(page);
        stripper.setEndPage(page);
        String text = stripper.getText(document).replaceAll("\\r", "").replaceAll("\\n", "").replaceAll(" ", "");
        log.debug("text={}", text);
        for (String keyWord : keyWords.split(",")) {
            if (text.contains(keyWords)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isExistKeyWords(String keyWords, String pdfFile) {
        if (StringUtils.isEmpty(keyWords)) {
            return false;
        }
        File file = new File(pdfFile);
        InputStream is = null;
        PDDocument document = null;
        try {
            document = PDDocument.load(file);
            int pageSize = document.getNumberOfPages();
            // 一页一页读取
            for (int i = 0; i < pageSize; i++) {
                // 文本内容
                PDFTextStripper stripper = new PDFTextStripper();
                // 设置按顺序输出
                stripper.setSortByPosition(true);
                stripper.setStartPage(i + 1);
                stripper.setEndPage(i + 1);
                String text = stripper.getText(document).replaceAll("\\r", "").replaceAll("\\n", "").replaceAll(" ", "");
                log.debug("text={}", text);
                return text.contains(keyWords);
            }
        } catch (Exception e) {
            log.error("pdfFile={}, e={}", pdfFile, e);
        } finally {
            try {
                if (document != null) {
                    document.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.error("e={}", e);
            }
        }
        return false;
    }

    public static void readPdfText(String path) {
        File file = new File(path);
        InputStream is = null;
        PDDocument document = null;
        try {
            document = PDDocument.load(file);
            int pageSize = document.getNumberOfPages();
            // 一页一页读取
            for (int i = 0; i < pageSize; i++) {
                // 文本内容
                PDFTextStripper stripper = new PDFTextStripper();
                // 设置按顺序输出
                stripper.setSortByPosition(true);
                stripper.setStartPage(i + 1);
                stripper.setEndPage(i + 1);
                String text = stripper.getText(document);
                log.debug("text={}", text);
                log.debug("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                if (text.contains("北京京东世纪信息技术有限公司")) {
                    log.warn("北京京东世纪信息技术有限公司");
                }
            }
        } catch (Exception e) {
            log.error("path={}, e={}", path, e);
        } finally {
            try {
                if (document != null) {
                    document.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.error("e={}", e);
            }
        }
    }

    /**
     * 获取 pdf 的 内容 目前指定获取 金额
     *
     * @param pdfFile
     * @param keyWords
     * @return void
     * @throws
     * @author Erting.Wang
     * @date 2021/3/9 10:27 上午
     */
    public static String readPdfGetMoney(String pdfFile, String keyWords) {
        File file = new File(pdfFile);
        InputStream is = null;
        PDDocument document = null;
        try {
            BigDecimal total = BigDecimal.ZERO;
            Map<String, BigDecimal> categoryTotal = Maps.newHashMap();
            String[] keyWordList = keyWords.split(",");
            document = PDDocument.load(file);
            int pageSize = document.getNumberOfPages();
            // 一页一页读取
            for (int i = 0; i < pageSize; i++) {
                // 文本内容
                PDFTextStripper stripper = new PDFTextStripper();
                // 设置按顺序输出
                stripper.setSortByPosition(true);
                stripper.setStartPage(i + 1);
                stripper.setEndPage(i + 1);
                String text = stripper.getText(document);
                String[] lines = text.split("\\r?\\n");
                String pageMoneyStr = "";
                for (String line : lines) {
                    if (line.contains("价税合计")) {
                        int start = line.indexOf("￥") > 0 ? line.indexOf("￥") : line.indexOf("¥");
                        if (start > 0) {
                            pageMoneyStr = line.substring(start, line.length())
                                    .replaceAll("￥", "")
                                    .replaceAll("¥", "").trim();
                        } else {
                            pageMoneyStr = getNumToStr(line);
                            if (StringUtils.isEmpty(pageMoneyStr)) {
                                String str = line.replace("价税合计", "").replace("（大写）", "").replace("（小写）", "")
                                        .replaceAll(" ", "");
                                log.debug("str={}", str);
                                pageMoneyStr = MoneyUtil.ChineseConvertToNumber(str);
                            }
                        }
                        break;
                    }
                    if (line.contains("總額")) {
                        pageMoneyStr = line.replace("總額", "").replaceAll(" ", "").replaceAll(",", "");
                        break;
                    }
                }
                BigDecimal pageMoney = new BigDecimal(pageMoneyStr);
                total = total.add(pageMoney);
                if (StringUtils.isNotEmpty(keyWords)) {
                    String pageText = stripper.getText(document).replaceAll("\\r", "").replaceAll("\\n", "").replaceAll(" ", "");
                    for (String keyWord : keyWordList) {
                        if (pageText.contains(keyWord)) {
                            if (categoryTotal.containsKey(keyWord)) {
                                categoryTotal.put(keyWord, categoryTotal.get(keyWord).add(pageMoney));
                            } else {
                                categoryTotal.put(keyWord, pageMoney);
                            }
                            break;
                        }else{
                            if (categoryTotal.containsKey("其他")) {
                                categoryTotal.put("其他", categoryTotal.get("其他").add(pageMoney));
                            } else {
                                categoryTotal.put("其他", pageMoney);
                            }
                        }
                    }
                }
            }
            log.debug("total={},RMB={},categoryTotal={}", total, MoneyUtil.toRmbString(total), categoryTotal);
            String result = "";
            if (!CollectionUtils.isEmpty(categoryTotal)) {
                for (Map.Entry<String, BigDecimal> entry : categoryTotal.entrySet()) {
                    result = result.concat("分类：").concat(entry.getKey()).concat("=>")
                            .concat("金额：").concat(entry.getValue().toString()).concat("\r\n");
                }
            }

            return result.concat("总金额=").concat(total.toString()).concat(" | ").concat(MoneyUtil.toRmbString(total))
                    .concat("\r\n已经在对应文件夹生成 '4to1.pdf' 文件用于打印！");
        } catch (Exception e) {
            log.error("path={}, e={}", pdfFile, e);
        } finally {
            try {
                if (document != null) {
                    document.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.error("e={}", e);
            }
        }
        return null;
    }

    /**
     * 将 多个pdf合并为一个pdf
     *
     * @param basePath
     * @param targetFile 合成后 文件名
     * @return void
     * @throws
     * @author Erting.Wang
     * @date 2021/3/9 9:12 上午
     */
    public static void MergePdf(String basePath, String targetFile) throws IOException {
        //pdf合并工具类
        PDFMergerUtility mergePdf = new PDFMergerUtility();
        //这是需要合并的PDF文件
        List<String> fileList = Lists.newArrayList();
        FileIoUtils.findFileList(new File(basePath), fileList);
        for (String pdfFile : fileList) {
            if (pdfFile.contains(".pdf")
                    && !pdfFile.contains("4to1.pdf")
                    && !pdfFile.contains("moreToOne.pdf")) {
                //删除多余的页数，只取第一页
                cutPdf(pdfFile);
                mergePdf.addSource(pdfFile);
            }
        }
        //设置合并生成pdf文件名称
        mergePdf.setDestinationFileName(targetFile);
        //合并pdf
        mergePdf.mergeDocuments();
        System.out.println("pdf文件合并成功");
    }

    /**
     * 删除多余的页数，只取第一页
     *
     * @param pdfPath
     * @return void
     * @throws
     * @author Erting.Wang
     * @date 2021/4/21 3:22 下午
     */
    private static void cutPdf(String pdfPath) {
        File file = new File(pdfPath);
        PDDocument document = null;
        try {
            document = PDDocument.load(file);
            int noOfPages = document.getNumberOfPages();
            log.debug("pdfPath={},noOfPages={}", pdfPath, noOfPages);
            if (noOfPages > 1) {
                for (int pages = noOfPages; pages > 1; pages--) {
                    log.debug("pages={}", pages);
                    document.removePage(pages - 1);
                }
                document.save(pdfPath);
            }
            // 竖向 转 横向
            PDPage page = document.getPage(0);
            PDRectangle pageSize = page.getMediaBox();
            float width = pageSize.getWidth();
            float height = pageSize.getHeight();
            log.debug("width={},height={}", width, height);
            if (width < height) {
                page.setRotation(0);
                try {
                    document.save(pdfPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void merge4PagesIntoOne(String originalPdfFile, String outputPdfFile) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(originalPdfFile);
        Document doc = new Document(new RectangleReadOnly(842f, 595f), 0, 0, 0, 0);
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(outputPdfFile));
        doc.open();
        int totalPages = reader.getNumberOfPages();
        for (int i = 1; i <= totalPages; i = i + 4) {
            doc.newPage();
            PdfContentByte cb = writer.getDirectContent();
            int wk = 1;
            for (int j = i; j < i + 4; j++) {
                if (j > totalPages) {
                    break;
                }
                // page #1
                PdfImportedPage page = writer.getImportedPage(reader, j);

                float documentWidth = doc.getPageSize().getWidth() / 2;
                documentWidth = documentWidth - 10f;
                float documentHeight = doc.getPageSize().getHeight();
                if (i > 1) {
                    documentHeight = documentHeight - 40f;
                }

                float pageWidth = page.getWidth();
                float pageHeight = page.getHeight();

                float widthScale = documentWidth / pageWidth;
                float heightScale = documentHeight / pageHeight;
                float scale = Math.min(widthScale, heightScale);

                float offsetX = (documentWidth - (pageWidth * scale)) / 2 + 5f;
                if (wk % 2 == 0) {
                    offsetX = ((documentWidth - (pageWidth * scale)) / 2) + documentWidth + 15f;
                }
                float offsetY = (documentWidth - (pageHeight * scale)) * 2 + 50f;
                if (wk > 2) {
                    offsetY = 10f;
                }
                cb.addTemplate(page, scale, 0, 0, scale, offsetX, offsetY);
                wk++;
            }

        }
        doc.close();
    }

    public static void getImages(String pdfFile) {
        File file = new File(pdfFile);
        InputStream is = null;
        PDDocument document = null;
        try {
            if (pdfFile.endsWith(".pdf")) {
                document = PDDocument.load(file);
                int pageSize = document.getNumberOfPages();
                // 一页一页读取
                for (int i = 0; i < pageSize; i++) {
                    // 文本内容
                    PDFTextStripper stripper = new PDFTextStripper();
                    // 设置按顺序输出
                    stripper.setSortByPosition(true);
                    stripper.setStartPage(i + 1);
                    stripper.setEndPage(i + 1);
                    String text = stripper.getText(document);
                    System.out.println(text.trim());
                    System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-");

                    // 图片内容
                    PDPage page = document.getPage(i);
                    PDResources resources = page.getResources();
                    Iterable<COSName> cosNames = resources.getXObjectNames();
                    log.debug("cosNames={}", cosNames);
                    if (cosNames != null) {
                        Iterator<COSName> cosNamesIter = cosNames.iterator();
                        while (cosNamesIter.hasNext()) {
                            COSName cosName = cosNamesIter.next();
                            if (resources.isImageXObject(cosName)) {
                                PDImageXObject ipdmage = (PDImageXObject) resources.getXObject(cosName);
                                BufferedImage image = ipdmage.getImage();
                                FileOutputStream out = new FileOutputStream(basePath + "output/" + UUID.randomUUID() + ".png");
                                try {
                                    ImageIO.write(image, "png", out);
                                } catch (IOException e) {
                                } finally {
                                    try {
                                        out.close();
                                    } catch (IOException e) {
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (InvalidPasswordException e) {
        } catch (IOException e) {
        } finally {
            try {
                if (document != null) {
                    document.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public static String getNumToStr(String str) {
        StringBuilder builder = new StringBuilder();
        String regEx = "(\\d+(\\.\\d+)?)";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        //当符合正则表达式定义的条件时
        while (m.find()) {
            builder.append(m.group());
        }
        return builder.toString();
    }
}
