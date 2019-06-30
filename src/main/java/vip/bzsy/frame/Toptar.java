package vip.bzsy.frame;

import vip.bzsy.func.BaiduORCManager;
import vip.bzsy.func.FileManager;
import vip.bzsy.utils.CliporadOperateUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;

/**
 * @author lyf
 * @create 2019-05-10 21:53
 */
public class Toptar extends JPanel {

    JButton uploadBtn;
    JButton textcheckBtn;
    JButton showClipbordBtn;
    JButton addFolderdBtn;
    MainFrame frame;
    LeftTree leftTree;

    public Toptar(){
        setLayout(new FlowLayout(FlowLayout.LEFT));
        uploadBtn = new JButton("上传文件");
        uploadBtn.addActionListener(new uploadBtnActionListener());
        textcheckBtn = new JButton("文字识别");
        textcheckBtn.addActionListener(new textcheckBtnActionListener());
        showClipbordBtn = new JButton("显示剪切板内容");
        showClipbordBtn.addActionListener(new showClipbordBtnActionListener());
        addFolderdBtn = new JButton("创建文件夹");
        addFolderdBtn.addActionListener(new addFolderdBtnActionListener());
        add(uploadBtn);
        add(textcheckBtn);
        add(showClipbordBtn);
        add(addFolderdBtn);
    }

    public void setLeftTree(LeftTree leftTree) {
        this.leftTree = leftTree;
    }

    public void setFrame(MainFrame frame) {
        this.frame = frame;
    }

    class addFolderdBtnActionListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            String selectPath = leftTree.getCurrentPath();//选中的的路径
            if (selectPath==null){
                JOptionPane.showMessageDialog(frame, "请选择路径！", "警告",JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!leftTree.isFolder(selectPath)){
                JOptionPane.showMessageDialog(frame, "只能创建到文件夹中！", "警告",JOptionPane.WARNING_MESSAGE);
                return;
            }
            String result = JOptionPane.showInputDialog(frame,"输入文件名","添加文件夹",JOptionPane.QUESTION_MESSAGE);
            if (result != null){
                String name = result + "/";
                new FileManager().createFloder(selectPath, name);
                leftTree.addNodeByName(name);
            }
        }
    }

    class showClipbordBtnActionListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String text = CliporadOperateUtils.getClipboardText();
                if (text!=null){
                    leftTree.setEditorPaneText(leftTree.getTextHtmlForJEPane("剪切板",text));
                } else {
                    Image pic = CliporadOperateUtils.getImageFromClipboard();
                    JLabel label = new JLabel();
                    ImageIcon icon = new ImageIcon(pic);
                    label.setIcon(icon);
                    JScrollPane pane = new JScrollPane(label);

                    JOptionPane optionPane = new JOptionPane(pane,JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
                    optionPane.setWantsInput(true);
                    optionPane.setInitialSelectionValue("请输入文件名：");
                    optionPane.setInputValue("你没有输入：");

                    JDialog dialog = optionPane.createDialog(frame,"显示图片");
                    dialog.setSize(300,300);
                    dialog.show();
                    String result = (String) optionPane.getInputValue();
                    if (result.equals("你没有输入：")){
                        JOptionPane.showMessageDialog(frame, "你没有输入！", "警告",JOptionPane.WARNING_MESSAGE);
                    } else {
                        String picFormatter = "png";
                        //ImageIO.write((RenderedImage) pic,"png",new File("D:/11.png"));
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        ImageIO.write((RenderedImage) pic,picFormatter,os);
                        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());

                        String selectPath = leftTree.getCurrentPath();//选中的的路径

                        if (selectPath==null){
                            JOptionPane.showMessageDialog(frame, "请选择路径！", "警告",JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        if (!leftTree.isFolder(selectPath)){
                            JOptionPane.showMessageDialog(frame, "请选择需要上传到的文件夹！", "警告",JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        String filePath = new FileManager().uploadFile(result, selectPath, inputStream,picFormatter);
                        String description = leftTree.getPicHtmlForJEPane(filePath);
                        leftTree.setEditorPaneText(description);
                        leftTree.addNodeByName(result+"."+picFormatter);
                        JOptionPane.showMessageDialog(frame, "文件名"+result+"上传成功！", "警告",JOptionPane.WARNING_MESSAGE);
                    }

                }

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    class uploadBtnActionListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser jf = new JFileChooser();
            jf.showOpenDialog(frame);//显示打开的文件对话框

            File f =  jf.getSelectedFile();//使用文件类获取选择器选择的文件
            if (f==null){
                return;
            }
            String path = f.getAbsolutePath();//返回路径名
            String selectPath = leftTree.getCurrentPath();//选中的的路径

            if (selectPath==null){
                JOptionPane.showMessageDialog(frame, "请选择路径！", "警告",JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!leftTree.isFolder(selectPath)){
                JOptionPane.showMessageDialog(frame, "请选择需要上传到的文件夹！", "警告",JOptionPane.WARNING_MESSAGE);
                return;
            }

            String filePath = new FileManager().uploadFile(selectPath, path);
            String description = leftTree.getPicHtmlForJEPane(filePath);
            leftTree.setEditorPaneText(description);
            leftTree.addNodeByPath(path);
        }
    }

    class textcheckBtnActionListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            String selectPath = leftTree.getCurrentPath();//选中的的路径
            if (selectPath==null){
                JOptionPane.showMessageDialog(frame, "请选择路径！", "警告",JOptionPane.WARNING_MESSAGE);
                return;
            }
            String fileStr = new FileManager().getFileStr(selectPath);
            String text = BaiduORCManager.gettContentByUrl(fileStr);
            leftTree.setEditorPaneText(leftTree.getTextHtmlForJEPane("文字识别",text));
        }
    }
}
