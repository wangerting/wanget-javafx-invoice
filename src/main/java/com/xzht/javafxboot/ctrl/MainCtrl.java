package com.xzht.javafxboot.ctrl;

import com.xzht.javafxboot.util.FileIoUtils;
import com.xzht.javafxboot.util.PdfUtils;
import com.xzht.javafxboot.util.PdfboxUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 主界面控制器
 *
 * @author westinyang
 * @date 2019/4/23 2:01
 */
@Slf4j
@FXMLController
public class MainCtrl implements Initializable {

    // 主容器
    public Pane rootPane;

    public Button btnAlert, btnChooseFile, btnChooseDirectory, btnNumToch, btnOpenDir;

    public String directory;

    public TextArea textArea;

    public TextField text, textNum, textCh;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("initialize: {}", location.getPath());
    }

    /**
     * 弹出框按钮单击事件
     *
     * @param actionEvent
     */
    public void onBtnAlertClick() {
        if (StringUtils.isNotEmpty(directory)) {
            try {
                String basePath = directory.concat("/");
                String sourcePdf = basePath.concat("moreToOne.pdf");
                String targetPdf = basePath.concat("4to1.pdf");
                FileIoUtils.deleteFile(sourcePdf);
                FileIoUtils.deleteFile(targetPdf);
                //多个pdf合并成一个pdf
                PdfUtils.MergePdf(basePath, sourcePdf);
                // 金额统计
                String total = PdfUtils.readPdfGetMoney(sourcePdf);
                //pdf转图片
                PdfboxUtil.pdfToImage(sourcePdf, basePath);
                //图片转pdf
                PdfboxUtil.imagesToPdf(sourcePdf, basePath);
                //4合1
                PdfUtils.merge4PagesIntoOne(sourcePdf, targetPdf);
                //删除临时生成的pdf
                FileIoUtils.deleteFile(sourcePdf);
                //删除生成的图片
                PdfboxUtil.delImages(basePath);
                //将发票总金额统计好后，填充到文本框中
                textArea.setText(total);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("请先选择文件夹！！！！！");
            alert.show();
        }
    }

    public void onBtnOpenDir() throws IOException {
        if (StringUtils.isNotEmpty(directory)) {
            Desktop.getDesktop().open(new File(directory));
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("请先选择文件夹！！！！！");
            alert.show();
        }
    }

    /**
     * 数字转中文大写
     */
    public void onBtnNumToChClick() {
        String textNumText = textNum.getText();
        if (StringUtils.isNotEmpty(textNumText)) {
            String rmbString = PdfUtils.toRmbString(new BigDecimal(textNumText));
            textCh.setText(rmbString);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("请输入金额");
            alert.show();
        }
    }


    /**
     * 选择文件按钮单机事件
     *
     * @param actionEvent
     */
    public void onBtnChooseFileClick() {
        Window window = rootPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        // 文件类型过滤器
        /*FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Excel files (*.xls, *.xlsx)", "*.xls", "*.xlsx");
        fileChooser.getExtensionFilters().add(filter);*/
        // ...
        File file = fileChooser.showOpenDialog(window);
        String fileName = file == null ? "" : file.getName();
        String fileAbsolutePath = file == null ? "" : file.getAbsolutePath();

        if (file != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("文件路径：" + fileAbsolutePath);
            alert.show();
        }
    }

    /**
     * 选择文件按钮单机事件
     *
     * @param actionEvent
     */
    public void onBtnChooseDirectoryClick() {
        Window window = rootPane.getScene().getWindow();
        DirectoryChooser file = new DirectoryChooser();
        file.setTitle("请选择要合成的电子发票目录");
        //这个file就是选择的文件夹了
        File newFolder = file.showDialog(window);

        if (file != null) {
            System.out.println(newFolder.getAbsolutePath());
            directory = newFolder.getAbsolutePath();

            text.setText(directory);
        } else {
            System.out.print("no directory chosen");
        }
    }
}
