package vip.bzsy.frame;

import vip.bzsy.func.FileManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * @author lyf
 * @create 2019-05-10 21:44
 */
public class MainFrame extends JFrame {

    private TextArea textArea;
    LeftTree leftTree;
    Toptar toptar;

    public MainFrame(String title){
        super(title);
        setLayout(new BorderLayout());



        /*textArea = new TextArea();
        FileManager fileManager = new FileManager();
        Map<String, List<String>> rootFiles = fileManager.getRootFiles("");
        List<String> folder = rootFiles.get(FileManager.FOLDER);
        folder.stream().forEach(str ->{
            textArea.append(str);
        });
        add(textArea,BorderLayout.CENTER);*/

        initTopbar();
        initLeftPanel();
        toptar.setLeftTree(leftTree);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSizeAndLocation();
    }

    private void setSizeAndLocation() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth()/4;
        int screenHeight = (int) screenSize.getHeight()/4;
        setSize(screenWidth*2,screenHeight*2);
        setLocation(screenWidth,screenHeight);
    }

    public void initTopbar(){
        toptar = new Toptar();
        add(toptar,BorderLayout.NORTH);
        toptar.setFrame(this);

    }

    public void initLeftPanel(){
        leftTree = new LeftTree();
        add(leftTree.getjSplitPane(),BorderLayout.CENTER);
    }

}
