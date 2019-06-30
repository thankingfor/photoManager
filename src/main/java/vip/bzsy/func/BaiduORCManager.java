package vip.bzsy.func;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author lyf
 * @create 2019-05-15 13:31
 */
public class BaiduORCManager {

    public static String SECRET_KEY = "";

    public static String API_KEY = "";

    public static String APP_ID = "";

    static {
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/conf.properties");
        Properties conf = new Properties();
        try {
            conf.load(resourceAsStream);
            SECRET_KEY = conf.getProperty("baidu.SECRET_KEY");
            API_KEY = conf.getProperty("baidu.API_KEY");
            APP_ID = conf.getProperty("baidu.APP_ID");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        String s = gettContentByUrl("http://bzsy.oss-cn-beijing.aliyuncs.com/image/youdao/markdown/874710-20161116202528013-1417169655.png?Expires=1873259299&OSSAccessKeyId=LTAIaMTOFLIoftS7&Signature=32S3QTC8iCkqIQcxAyxaPff1vXw%3D");
        System.out.println(s);
    }

    public static String gettContentByUrl(String url){
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");
        options.put("probability", "true");
        JSONObject jsonObject  = client.basicGeneralUrl(url,options);
        /**
         * 解析json
         */
        Map<String, Object> map = jsonObject.toMap();
        List<Map<String,Object>> word_result = (List<Map<String, Object>>) map.get("words_result");

        StringBuilder stringBuilder = new StringBuilder();

        word_result.stream().forEach(resMap->{
            String str = (String) resMap.get("words");
            stringBuilder.append(str);
        });
        return stringBuilder.toString();
    }

}
