package com;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seeyon.client.CTPRestClient;
import com.seeyon.client.CTPServiceClientManager;
import com.seeyon.ctp.util.StringUtil;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@CrossOrigin
@RestController
class RestTest {
    @RequestMapping(value = "/rest2", produces = "application/json;charset=utf-8")
        //String rest(@RequestParam String seeyonUrl){
    Object rest2() {
        init();
        String post = client.get("/news/newsType/unit/670869647114347", String.class);//注意：这里的Map data 切勿传入null，及时data没有信息，也需Map data = new HashMap();
        post=post.replaceAll("\r\n","");

        JSONArray array = (JSONArray) JSONArray.parse(post);
        for (Object obj:array) {
            JSONObject jsonObj = (JSONObject)obj;
            for (String key:jsonObj.keySet()) {
                if(jsonObj.get(key)!=null){
                    if(isInteger(jsonObj.get(key).toString())){
                        jsonObj.put(key,String.valueOf(jsonObj.get(key).toString()));
                    }
                }
            }
        }
        return array;
    }

    @RequestMapping(value = "/rest", produces = "application/json;charset=utf-8")
    Object rest(@RequestParam String seeyonUrl) {
        init();
        String post = client.get(seeyonUrl, String.class);//注意：这里的Map data 切勿传入null，及时data没有信息，也需Map data = new HashMap();
        JSONObject jsonObj = (JSONObject) JSONObject.parse(post);
        for (String key:jsonObj.keySet()) {
            if(isInteger(jsonObj.get(key).toString())){
                jsonObj.put(key,String.valueOf(jsonObj.get(key).toString()));
            }
        }
        return jsonObj;
    }

    CTPRestClient client;

    void init() {
        CTPServiceClientManager clientManager = CTPServiceClientManager.getInstance("http://10.6.189.79:80");
        // 取得REST动态客户机实例
        client = clientManager.getRestClient();
        client.authenticate("admin", "cjwsjy123");
    }

    @RequestMapping(value = "/rest3", produces = "application/json;charset=utf-8")
    Object rest3() {
        init();
        String post = client.get("news/newsType/21?ticket=wangbicheng", String.class);//注意：这里的Map data 切勿传入null，及时data没有信息，也需Map data = new HashMap();
        Object obj = JSONObject.parse(post);
        return obj;
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

}
