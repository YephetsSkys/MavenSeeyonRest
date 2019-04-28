package com;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seeyon.client.CTPRestClient;
import com.seeyon.client.CTPServiceClientManager;
import org.springframework.web.bind.annotation.*;
import util.HttpKit;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

@CrossOrigin
@RestController
class RestTest {

    final static String HOST = "http://172.16.7.211:80";
    final static String PATH = "/seeyon/rest/";

    @RequestMapping(value = "/download/{fi}/{fn:.*}", produces = "application/json;charset=utf-8")
    void download(HttpServletResponse response,
                  @PathVariable("fi") String fi,
                  @PathVariable("fn") String fn) {
        response.addHeader("Content-Disposition","attachment;fileName=" + fn);// 设置文件名
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            // 测试用的内网资源
            URL url = new URL(HOST + "/seeyon/rest/attachment/fileforThird/" + fi + "?token=" + getToken());
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            // Post 请求不能使用缓存
            httpURLConnection.setUseCaches(true);
            // 设定请求的方法，默认是GET
            httpURLConnection.setRequestMethod("GET");
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            httpURLConnection.connect();
            httpURLConnection.setConnectTimeout(100);
            httpURLConnection.setReadTimeout(100);

            OutputStream os = response.getOutputStream();
            bis = new BufferedInputStream(httpURLConnection.getInputStream());
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @RequestMapping(value = "/rest2", produces = "application/json;charset=utf-8")
        //String rest(@RequestParam String seeyonUrl){
    Object rest2() {
        init();
        String post = client.get("/news/newsType/unit/670869647114347", String.class);//注意：这里的Map data 切勿传入null，及时data没有信息，也需Map data = new HashMap();
        post = post.replaceAll("\r\n", "");

        JSONArray array = (JSONArray) JSONArray.parse(post);
        for (Object obj : array) {
            JSONObject jsonObj = (JSONObject) obj;
            for (String key : jsonObj.keySet()) {
                if (jsonObj.get(key) != null) {
                    if (isInteger(jsonObj.get(key).toString())) {
                        jsonObj.put(key, String.valueOf(jsonObj.get(key).toString()));
                    }
                }
            }
        }
        return array;
    }

    //localhost:8090/MavenSeeyonRest/rest/get/http?loginName=huanglaofu&seeyonUrl=inquiry/4871932671212472001/5962358151392674515/2/0
    @RequestMapping(value = "/rest/get/http", produces = "application/json;charset=utf-8")
    Object rest_get_http(@RequestParam String seeyonUrl,@RequestParam String loginName) {
        String msg = null;
        try {
            String userToken = HttpKit.get(HOST+PATH+"token/admin/cjwsjy123?loginName="+loginName);
            JSONObject userJson = JSONObject.parseObject(userToken);
            String token = userJson.getString("id");
            msg = HttpKit.get(HOST+PATH+seeyonUrl+"?token="+token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    @RequestMapping(value = "/rest/post/http", produces = "application/json;charset=utf-8")
    Object rest_post_http(@RequestParam String seeyonUrl,@RequestParam String loginName) {
        String msg = null;
        try {
            String userToken = HttpKit.get(HOST+PATH+"token/admin/cjwsjy123?loginName="+loginName);
            JSONObject userJson = JSONObject.parseObject(userToken);
            String token = userJson.getString("id");
            msg = HttpKit.post(HOST+PATH+seeyonUrl+"?token="+token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }


    //公告内附件取法：http://localhost:8090/MavenSeeyonRest/rest?seeyonUrl=attachment/reference/7750969973988082761?type=0&pagNo=1&pageSize=100
    //http://116.54.19.198:8083/MavenSeeyonRest/rest?seeyonUrl=
    @RequestMapping(value = "/rest", produces = "application/json;charset=utf-8")
    Object rest(@RequestParam String seeyonUrl) {
        init();
        Object object = new Object();
        String post = client.get(seeyonUrl, String.class);//注意：这里的Map data 切勿传入null，及时data没有信息，也需Map data = new HashMap();
        post = post.replaceAll("\r\n", "");
        if (isJsonArray(post)) {
            JSONArray array = (JSONArray) JSONArray.parse(post);
            for (Object obj : array) {
                JSONObject jsonObj = (JSONObject) obj;
                for (String key : jsonObj.keySet()) {
                    if (jsonObj.get(key) != null) {
                        if (isInteger(jsonObj.get(key).toString())) {
                            jsonObj.put(key, String.valueOf(jsonObj.get(key).toString()));
                        }
                    }
                }
            }
            object = array;
        }
        if (isJsonObj(post)) {
            JSONObject obj = JSONObject.parseObject(post);
            for (String key : obj.keySet()) {
                if (obj.get(key) != null) {
                    if (isInteger(obj.get(key).toString())) {
                        obj.put(key, String.valueOf(obj.get(key).toString()));
                    }
                }
            }
            object = obj;
        }
        return object;
    }

    CTPRestClient client;

    void init() {
        CTPServiceClientManager clientManager = CTPServiceClientManager.getInstance(HOST);
        // 取得REST动态客户机实例
        client = clientManager.getRestClient();
        client.authenticate("admin", "cjwsjy123");
    }

    @GetMapping(value = "/getToken", produces = "application/json;charset=utf-8")
    Object getToken() {
        init();
        String post = client.get("token/admin/cjwsjy123", String.class, "text/plain");//注意：这里的Map data 切勿传入null，及时data没有信息，也需Map data = new HashMap();
        return post;
    }

    /*方法二：推荐，速度最快
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断字符串是否可以转化为JSON数组
     *
     * @param content
     * @return
     */
    public static boolean isJsonArray(String content) {
        try {
            JSONArray jsonStr = JSONArray.parseArray(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断字符串是否可以转化为JSON对象
     *
     * @param content
     * @return
     */
    public static boolean isJsonObj(String content) {
        try {
            JSONObject jsonStr = JSONObject.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
