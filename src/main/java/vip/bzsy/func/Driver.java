package vip.bzsy.func;

import com.aliyun.oss.OSSClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Driver {

    private static String endpoint = "";
    private static String accessKeyId = "";
    private static String accessKeySecret = "";
    private static String bucketName = "";
    private static OSSClient ossClient = null;

    static {
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/conf.properties");
        Properties conf = new Properties();
        try {
            conf.load(resourceAsStream);
            endpoint = conf.getProperty("aliyun.endpoint");
            accessKeyId = conf.getProperty("aliyun.accessKeyId");
            accessKeySecret = conf.getProperty("aliyun.accessKeySecret");
            bucketName = conf.getProperty("aliyun.bucketName");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static OSSClient  getClient(){
        if (ossClient ==null){
            ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);// 创建OSSClient实例。
        }
        return ossClient;
    }

    public static String getBucketName(){
        return bucketName;
    }
}
