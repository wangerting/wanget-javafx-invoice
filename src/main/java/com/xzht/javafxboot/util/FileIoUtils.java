package com.xzht.javafxboot.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author Erting.Wang
 * @desciption 类说明
 * @date 2020/11/6 8:51 下午
 */
@Slf4j
public class FileIoUtils {

    public static void main(String[] args) {
//        String url = "https://auto.51autoshop.com//Images/BrandLogo/AErFa·LuoMiOu.jpg";
//        String path = "/Users/wangerting/work/cdn/upload/AErFa·LuoMiOu.jpg";
//        downloadPicture(url, path);

//        String sourceFile = "/Users/wangerting/work/cdn/upload/tempWanget/brand/4a8dad9f-7b32-4933-b8bc-e03446614b10.jpg";
//        String targetFile = "/Users/wangerting/work/cdn/upload/brand/4a8dad9f-7b32-4933-b8bc-e03446614b10.jpg";
//        removeFile(sourceFile, targetFile);
//        String url = "http://img.jyeoo.net/quiz/images/202007/1/3b635b45.png";
//        String fileName = getFileNameByUrl(url);
//        String replace = url.replace(fileName, "").replace("http://img.jyeoo.net/quiz/images/", "");
//        System.out.println(replace);

        String basePath = "/Users/wangerting/Desktop/牛交所/牛交所/发票/2021年发票/5月/";
        List<String> fileList = Lists.newArrayList();
        FileIoUtils.findFileList(new File(basePath), fileList);
        log.debug("fileList={}", fileList);

    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * 移动文件
     *
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @return boolean
     * @throws
     * @author Erting.Wang
     * @date 2020/11/8 7:38 下午
     */
    public static boolean removeFile(String sourceFile, String targetFile) {
        log.debug("sourceFile={},targetFile={}", sourceFile, targetFile);
        File file = new File(sourceFile);
        File destFile = new File(targetFile);
        //检查目标路径是否合法
        if (destFile.exists()) {
            if (destFile.isFile()) {
                log.error("目标路径是个文件，请检查目标路径！");
                return false;
            }
        } else {
            if (!destFile.getParentFile().exists()) {
                if (!destFile.getParentFile().mkdirs()) {
                    log.error("目标文件夹不存在，创建失败！");
                    return false;
                }
            }
        }
        //检查源文件是否合法
        if (file.isFile() && file.exists()) {
            if (!file.renameTo(destFile)) {
                log.error("移动文件失败！");
                return false;
            }
        } else {
            log.error("要备份的文件路径不正确，移动失败！");
            return false;
        }
        log.info("已成功移动文件" + file.getName() + "到" + targetFile);
        return true;
    }

    /**
     * 链接url下载图片
     */
    public static void downloadPicture(String imgUrl, String path) {
        URL url = null;
        try {
            url = new URL(imgUrl);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            File file = new File(path);
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();//创建路径
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            BASE64Encoder encoder = new BASE64Encoder();
            //返回Base64编码过的字节数组字符串
            String encode = encoder.encode(buffer);
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            log.error("e={}", e);
        } catch (IOException e) {
            log.error("e={}", e);
        }
    }

    /**
     * 通过 url 获取流
     *
     * @param imgUrl 图片url
     * @return
     * @throws
     * @author Erting.Wang
     * @date 2020/11/6 8:52 下午
     */
    public static InputStream getInputStream(String imgUrl) {
        InputStream inputStream = null;
        if (StringUtils.isNotBlank(imgUrl)) {
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(imgUrl).openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
                httpURLConnection.setRequestProperty("Accept-Encoding", "gzip");
                httpURLConnection.setRequestProperty("Referer", "no-referrer");
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setReadTimeout(20000);
                inputStream = httpURLConnection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return inputStream;
    }

    /**
     * 根据流 保存 文件
     *
     * @param inputStream 流
     * @param path        路径
     * @return boolean
     * @throws
     * @author Erting.Wang
     * @date 2020/11/6 8:53 下午
     */
    public static boolean downloadImg(InputStream inputStream, String path) {
        boolean flag = true;
        File file = new File(path);
        if (file.exists()) {
            return flag;
        }
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();//创建路径
        }
        try {
            FileUtils.copyToFile(inputStream, file);
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }

    /**
     * 根据url 保存 文件
     *
     * @param url  图片url
     * @param path 路径
     * @return boolean
     * @throws
     * @author Erting.Wang
     * @date 2020/11/6 8:53 下午
     */
    public static boolean downloadToUrl(String url, String path) {
        return downloadImg(getInputStream(url), path);
    }

    public static String getFileNameByUrl(String url) {
        //获取最后一个/
        int indexOf = url.lastIndexOf("/");
        return url.substring(indexOf + 1, url.length());

    }

    /**
     * 读取目录下的所有文件
     *
     * @param dir      目录
     * @param fileList 保存文件名的集合
     * @return
     */
    public static void findFileList(File dir, List<String> fileList) {
        // 判断是否存在目录
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 读取目录下的所有目录文件信息
        String[] files = dir.list();
        // 循环，添加文件名或回调自身
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            // 如果文件
            if (file.isFile()) {
                // 添加文件全路径名
                fileList.add(dir + File.separator + file.getName());
            } else {// 如果是目录
                // 回调自身继续查询
                findFileList(file, fileList);
            }
        }
    }
}
