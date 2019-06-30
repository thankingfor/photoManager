package vip.bzsy.frame;


import vip.bzsy.func.FileManager;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lyf
 * @create 2019-05-11 21:58
 */
public class LeftTree {
    JEditorPane editorPane;
    JTree tree;
    DefaultTreeModel treeModel;
    DefaultMutableTreeNode root;
    JSplitPane jSplitPane;

    public LeftTree(){
        root = new DefaultMutableTreeNode("阿里root");
        addNodes(getFoldersNodes(""),root);

        tree = new JTree(root);
        treeModel =(DefaultTreeModel) tree.getModel();

        tree.setEditable(false);//不可以编辑
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);//点击为单机
        tree.addTreeSelectionListener(new MyTreeSelectorHandle());//点击事件

        JScrollPane scrollPane1 = new JScrollPane(tree);
        scrollPane1.setPreferredSize(new Dimension(300,1));

        editorPane = new JEditorPane();
        editorPane.setContentType("text/html");

        JScrollPane scrollPane2 = new JScrollPane(editorPane);
        jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,scrollPane1,scrollPane2);

    }

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    public JSplitPane getjSplitPane() {
        return jSplitPane;
    }

    private void addNodes(java.util.List<DefaultMutableTreeNode> list,DefaultMutableTreeNode root){
        for (DefaultMutableTreeNode node:list) {
            root.add(node);
            //treeModel.insertNodeInto(node, root, root.getChildCount());
            //tree.scrollPathToVisible(new TreePath(node.getPath()));
        }
    }

    public java.util.List<DefaultMutableTreeNode> getFoldersNodes(String path){
        return getNodes(path,FileManager.FOLDER);
    }

    public java.util.List<DefaultMutableTreeNode> getFilesNodes(String path){
        return getNodes(path,FileManager.FILE);
    }

    private java.util.List<DefaultMutableTreeNode> getNodes(String path,String findName){
        Map<String, List<String>> pathNodes = getPathNodes(path);
        List<String> list = pathNodes.get(findName);
        java.util.List<DefaultMutableTreeNode> listNodes = new ArrayList<>();
        list.stream().forEach(name ->{
            String nameNode = splitName(name);
            if (path.indexOf(nameNode) < 0) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(nameNode);
                node.setAllowsChildren(true);
                listNodes.add(node);
            }
        });
        return listNodes;
    }

    /**
     * 如果最后位/ 就分割出一个文件夹
     * 如果不是 就把文件的名字弄出来
     */
    private String splitName(String name) {

        if (name.lastIndexOf("/") == name.length()-1){
            String[] split = name.split("/");
            return split[split.length-1]+"/";
        } else {
            String[] split = name.split("/");
            return split[split.length-1];
        }
    }

    /**
     * 获取指定路径的下所有相关文件
     * @param path 查找路径
     * @return 路径文件
     */
    public Map<String, java.util.List<String>> getPathNodes(String path){
        FileManager fileManager = new FileManager();
        Map<String, java.util.List<String>> rootFiles = fileManager.getRootFiles(path);
        return rootFiles;
    }

    public static String toStringByPaths(TreeNode[] path){
        StringBuilder paths = new StringBuilder();
        for (int i = 1; i < path.length ; i++){
            paths.append(path[i].toString());
        }
        return paths.toString();
    }

    public String getCurrentPath(){
        DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if(selectNode==null){
            return null;
        }
        String path = toStringByPaths(selectNode.getPath());
        return path;
    }

    /**
     * 是否是文件夹
     * @param path
     * @return
     */
    public boolean isFolder(String path){
        if (path.lastIndexOf("/") == path.length()-1){
            return true;
        }
        return false;
    }

    /**
     * 设置编辑板内容
     * @param text
     */
    public void setEditorPaneText(String text){
        editorPane.setText(text);
    }

    /**
     * 获取图片html文字
     * @param picPath
     * @return
     */
    public String getPicHtmlForJEPane(String picPath){
        String description = "<html><img src='"+picPath+"' /></br><h1>文件地址</h1><p>"+picPath+"</p></html>";
        return description;
    }

    /**
     * 获取文字 html文字
     * @param title
     * @param text
     * @return
     */
    public String getTextHtmlForJEPane(String title,String text){
        String description = "<html>" +
                "<h1>"+title+"</h1>" +
                "<h3>"+text+"</h3>" +
                "</html>";
        return description;
    }

    /**
     * 路径下添加节点 C:/nnn就是获取  节点名为nnn
     * @param path
     */
    public void addNodeByPath(String path){
        int i = path.lastIndexOf(File.separator);
        String nodeName = path.substring(i+1,path.length());
        addNodeByName(nodeName);
    }

    /**
     * 路径下添加节点
     */
    public void addNodeByName(String name){
        DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
        treeModel.insertNodeInto(node,selectNode,selectNode.getChildCount());
    }

    class MyTreeSelectorHandle implements TreeSelectionListener{

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            JTree tree = (JTree) e.getSource();
            DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            String nodeName = selectNode.toString();
            if (nodeName.lastIndexOf("/") == nodeName.length()-1){//文件夹
                String path = toStringByPaths(selectNode.getPath());
                List<DefaultMutableTreeNode> foldersNodes = getFoldersNodes(path);
                addNodes(foldersNodes,selectNode);
                List<DefaultMutableTreeNode> filesNodes = getFilesNodes(path);
                addNodes(filesNodes,selectNode);
            } else {//文件
                String path = toStringByPaths(selectNode.getPath());
                try {
                    String fileStr = new FileManager().getFileStr(path);
                    String description = getPicHtmlForJEPane(fileStr);
                    editorPane.setText(description);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
