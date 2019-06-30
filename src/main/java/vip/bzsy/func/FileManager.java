package vip.bzsy.func;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.sun.media.sound.SoftTuning;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * @author lyf
 * @create 2019-05-11 21:13
 */
public class FileManager {

    public static String FILE = "file";
    public static String FOLDER = "folder";

    private String bucketName ;
    private OSSClient client;

    {
        this.bucketName = Driver.getBucketName();
        this.client = Driver.getClient();
    }

    public Map<String,List<String>> getRootFiles(String key){
        // 构造ListObjectsRequest请求。
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
        // 设置正斜线（/）为文件夹的分隔符。
        listObjectsRequest.setDelimiter("/");
        // 列出fun目录下的所有文件和文件夹。
        listObjectsRequest.setPrefix(key);
        ObjectListing listing = client.listObjects(listObjectsRequest);

        // 遍历所有文件。
        List<String> fileList = new ArrayList<String>();
        // objectSummaries的列表中给出的是fun目录下的文件。
        for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
            fileList.add(objectSummary.getKey());
            //System.out.println(objectSummary.getKey());
        }

        // 遍历所有commonPrefix。
        List<String> folderList = listing.getCommonPrefixes();
        //System.out.println("\nCommonPrefixes:");
        // commonPrefixs列表中给出的是fun目录下的所有子文件夹。fun/movie/001.avi和fun/movie/007.avi两个文件没有被列出来，
        // 因为它们属于fun文件夹下的movie目录。
        /*for (String commonPrefix : listing.getCommonPrefixes()) {
            System.out.println(commonPrefix);
        }*/

        // 关闭OSSClient。
        //client.shutdown();
        Map<String,List<String>> map = new HashMap<String, List<String>>();
        map.put("file",fileList);
        map.put("folder",folderList);
        return map;
    }

    public void createFloder(String key,String name){
        client.putObject(bucketName,key+name,new ByteArrayInputStream(new byte[0]));
    }

    public String uploadFile(String name,String key,InputStream is,String formmater){
        key =  key + name + "." + formmater;
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, is);
        client.putObject(putObjectRequest);
        String fileStr = getFileStr(key);
        System.out.println(fileStr);
        return fileStr;
    }

    public String uploadFile(String name,String key,InputStream is){
        return uploadFile(name,key,is,"png");
    }

    public String uploadFile(String key,String path){
        int i = path.lastIndexOf("\\");
        key = key + path.substring(i+1,path.length());
        UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
        // The local file to upload---it must exist.
        uploadFileRequest.setUploadFile(path);
        // Sets the concurrent upload task number to 5.
        uploadFileRequest.setTaskNum(5);
        uploadFileRequest.setPartSize(1024 * 1024 * 2);//最大2M
        // Enables the checkpoint file. By default it's off.
        uploadFileRequest.setEnableCheckpoint(true);

        UploadFileResult uploadResult = null;
        try {
            uploadResult = client.uploadFile(uploadFileRequest);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        CompleteMultipartUploadResult multipartUploadResult =
                uploadResult.getMultipartUploadResult();
        String key1 = multipartUploadResult.getKey();
        String fileStr = getFileStr(key1);
        return fileStr;
    }

    public InputStream getFileInputStream(String key){
        OSSObject object = client.getObject(bucketName, key);
        return object.getObjectContent();
    }

    public String getFileStr(String key){
        Date date = new Date(new Date().getTime()+3600l*100*24*365*100);//设置100年过期
        URL url = client.generatePresignedUrl(bucketName, key, date);
        return url.toString();
    }

    public URL getFileUrl(String key){
        Date date = new Date(new Date().getTime()+3600l*100*24*365*100);//设置100年过期
        URL url = client.generatePresignedUrl(bucketName, key, date);
        return url;
    }
}
