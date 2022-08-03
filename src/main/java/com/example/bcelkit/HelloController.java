package com.example.bcelkit;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HelloController {
    @FXML
    private Button decodeButton;
    @FXML
    private CheckBox modelCheckBox;
    @FXML
    private TextArea textAre;
    @FXML
    private Button openFileButton;
    @FXML
    private CheckBox compressCheckBox;
    private byte[] bufferData = null;
    private String classFilePath = "";

//    @FXML
//    protected void onActionBottonClick() throws IOException {
//        String input = textAre.getText().trim();
//        if(input.length() > 1){
//            if(input.startsWith("$$BCEL$$")) {
//                textAre.setText(decode(input));
//            }else{
//                System.out.println("Encode");
//            }
//        }else{
//            Alert box = new Alert(Alert.AlertType.INFORMATION);
//            box.setTitle("提示");
//            box.setContentText("内容不能为空！");
//            box.showAndWait();
//        }
//    }


    private File getFileHandler(String boxTitle,boolean writeFile){
        FileChooser handle = new FileChooser();
        handle.setInitialDirectory(new File(System.getProperty("user.home")));
        handle.setTitle(boxTitle);
        handle.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java class (*.class)", "*.class"));
        File file = null;
        if(writeFile){
            handle.setInitialFileName("result.class");
            file = handle.showSaveDialog(new Stage());
        }else {
            file = handle.showOpenDialog(new Stage());
        }
        return file;
    }
    @FXML
    protected void fileChooser() {
        File file = getFileHandler("选择要编码的Class文件", false);
        if(file !=null) {
            try {
                this.classFilePath = file.getAbsolutePath();
                this.bufferData = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                textAre.setText("$$BCEL$$" + Utility.encode(this.bufferData, compressCheckBox.isSelected()));
                System.out.println("[+]编码完成");
                showInfor("INFO","编码完成");
            } catch (Exception err) {
                showError("出错了:", err.getMessage());
                System.out.println("[-] " + err.getMessage());
            }
        }
    }

    @FXML
    private void onDecodeButtonClick(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        String code = textAre.getText();
        if(code.length() < 1){
            alert.setContentText("Sir，你要解码什么？");
            alert.showAndWait();
            return;
        }else if(!code.startsWith("$$BCEL$$")){
            alert.setContentText("Sir，没有识别到BCEL编码");
            alert.showAndWait();
            return;
        }


        File outputFile = getFileHandler("另存为", true);
        if(outputFile !=null) {
            try {
                byte[] codeStream = decode(code);
                outputFile.getAbsoluteFile().createNewFile();
                    DataOutputStream outFileStream = new DataOutputStream(new FileOutputStream(outputFile, true));
                    outFileStream.write(codeStream);
                    outFileStream.close();
                    showInfor("INFO","保存成功");
                    System.out.println("[+]Success");
            } catch (Exception err) {
                showError("出错了:",err.getMessage());
                System.out.println("[-] " + err.getMessage());
            }
        }
    }
    @FXML
    private void onModelCheckBoxClick(){
        if(modelCheckBox.isSelected()){
            modelCheckBox.setText("解码模式");
            openFileButton.setDisable(true);
            decodeButton.setDisable(false);

        }else{
            modelCheckBox.setText("编码模式");
            openFileButton.setDisable(false);
            decodeButton.setDisable(true);
        }
    }

    private byte[] decode(String data) {
        data = data.substring(8);
        byte[] bcelCode = null;
        try {
            bcelCode = Utility.decode(data, compressCheckBox.isSelected());

        }catch (Exception e){
            showError("解码错误",e.getMessage());
            System.out.println("[-] " + e.getMessage());
        }
        return bcelCode;
    }

    private void showError(String title,String text){
        Alert popBox = new Alert(Alert.AlertType.ERROR);
        popBox.setTitle(title + ":");
        popBox.setContentText(text);
        popBox.showAndWait();
    }

    private void showInfor(String title,String text){
        Alert popBox = new Alert(Alert.AlertType.INFORMATION);
        popBox.setTitle(title + ":");
        popBox.setContentText(text);
        popBox.showAndWait();
    }
}