package jp.co.japantaxi.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonMapper {
 
  static JSONObject object = new JSONObject();
  static ObjectMapper mapper = new ObjectMapper();

  public static ObjectMapper newMapper() {
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.registerModule(new JavaTimeModule());
    mapper.registerModule(new Jdk8Module());
    return mapper;
  }

  public static List<Object> toList(JSONArray array) {
    List<Object> list = new ArrayList<>();
    if (!array.isEmpty()) {
      for (int i = 0; i < array.length(); i++) {
        Object value = array.get(i);
        if (value instanceof JSONArray) {
          value = toList((JSONArray) value);
        }
        if (value instanceof JSONObject) {
          value = toMap((JSONObject) value);
        }
        list.add(value);
      }
    }
    return list;
  }

  public static Map<String, Object> toMap(JSONObject object) {
    Map<String, Object> map = new HashMap<>();
    if (!object.isEmpty()) {
      Iterator<?> keysItr = object.keySet().iterator();
      while (keysItr.hasNext()) {
        String key = (String) keysItr.next();
        Object value = object.get(key);
        if (value instanceof JSONArray) {
          value = toList((JSONArray) value);
        }
        if (value instanceof JSONObject) {
          value = toMap((JSONObject) value);
        }
        map.put(key, value);
      }
    }
    return map;
  }

  public static void readDataSync(String context) throws IOException {
	InputStream is = JsonMapper.class.getResourceAsStream("/datasync.json");
	BufferedReader buff = new BufferedReader(
			new InputStreamReader(is, "UTF-8"));
	String line = null;
	StringBuilder sb = new StringBuilder();
	while ((line = buff.readLine()) != null) {
		sb.append(line);
	}
	JSONObject jsonObject = new JSONObject(sb.toString());
	object = jsonObject.getJSONObject(context.toLowerCase());
	buff.close();
  }
  
}
