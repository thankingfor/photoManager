package vip.bzsy.utils;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * @author lyf
 * @create 2019-05-15 15:44
 */
@SuppressWarnings("all")
public class CliporadOperateUtils {

    /**
     * 从指定的剪切板中获取文本内容
     * 本地剪切板使用 Clipborad cp = new Clipboard("clip1"); 来构造
     * 系统剪切板使用 Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
     * 剪切板的内容 getContents(null); 返回Transferable
     */
    public static String getClipboardText() throws Exception{
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();//获取系统剪贴板
        // 获取剪切板中的内容
        Transferable clipT = clip.getContents(null);
        if (clipT != null) {
            // 检查内容是否是文本类型
            if (clipT.isDataFlavorSupported(DataFlavor.stringFlavor))
                return (String)clipT.getTransferData(DataFlavor.stringFlavor);
        }
        return null;
    }

    //往剪切板写文本数据
    public static void setClipboardText(Clipboard clip, String writeMe) {
        Transferable tText = new StringSelection(writeMe);
        clip.setContents(tText, null);
    }
    // 从剪切板读取图像
    public static Image getImageFromClipboard() throws Exception{
        Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable cc = sysc.getContents(null);
        if (cc == null)
            return null;
        else if(cc.isDataFlavorSupported(DataFlavor.imageFlavor))
            return (Image)cc.getTransferData(DataFlavor.imageFlavor);
        return null;
    }
    // 写图像到剪切板
    public static void setClipboardImage2(final Image image) {
        Transferable trans = new Transferable(){
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] { DataFlavor.imageFlavor };
            }
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.imageFlavor.equals(flavor);
            }
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if(isDataFlavorSupported(flavor))
                    return image;
                throw new UnsupportedFlavorException(flavor);
            }
        };
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
    }
}
