package demo.util;


import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonWriter {

    public void toJson(String str, String outURL) throws Exception {  	
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(outURL),"UTF-8");

        JSONObject obj=new JSONObject();//创建JSONObject对象
     
        JSONObject object = JSONObject.parseObject(str);
        String pretty = "";
        if(object.get("cells")!=null) {
        	pretty = JSON.toJSONString(object.get("cells"), SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, 
        			SerializerFeature.WriteDateUseDateFormat);
        }
        else {
        	pretty = JSON.toJSONString(object.get("variables"), SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, 
        			SerializerFeature.WriteDateUseDateFormat);
        }

        osw.write(pretty);
        osw.flush();//清空缓冲区，强制输出数据
        osw.close();//关闭输出流
	}
}
