package jp.co.japantaxi.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

public class Utility {

  static Logger LOGGER = LoggerFactory.getLogger(Utility.class);

  public static <T> List<T> difference(List<T> list1, List<T> list2) {
    List<T> difference = new ArrayList<T>();
    if (list1.isEmpty()) {
      return new ArrayList<T>();
    } else if (list2.isEmpty()) {
      for (T object : list1) {
        difference.add(object);
      }
    } else {
      for (T object : list2) {
        if (!list1.contains(object)) {
          difference.add(object);
        }
      }
    }
    return difference;
  }

  public static <T> List<T> intersection(List<T> list1, List<T> list2) {
    List<T> intersection = new ArrayList<>();
    if (list1.isEmpty()) {
      return new ArrayList<T>();
    }
    if (list2.isEmpty()) {
      return difference(list1, list2);
    }
    for (T object : list1) {
      if (list2.contains(object)) {
        intersection.add(object);
      }
    }
    return intersection;
  }

  public static Float parseFloat(String value) {
    try {
      return Float.parseFloat(value);
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  public static Integer parseInt(String value) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  public static Double parseDouble(String value) {
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  public static Boolean parseBoolean(String b) {
    if ("TRUE".equalsIgnoreCase(b.toUpperCase()) || "1".equalsIgnoreCase(b)) {
      return true;
    } else {
      return null;
    }
  }

  public static Long parseLong(String value) {
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  public static String parseString(String startTime) {
    StringBuilder sb = new StringBuilder();
    sb.append("'");
    sb.append(startTime);
    sb.append("'");
    return sb.toString();
  }

  public static String parseList(List<String> list) {
    StringBuilder b = new StringBuilder("'");
    b.append(String.join("','", list));
    b.append("'");
    return b.toString();
  }

  public static String convertList(List<String> list) {
    StringBuilder b = new StringBuilder("('");
    b.append(String.join("'),('", list));
    b.append("')");
    return b.toString();
  }

  public static <T> List<String> compare(List<T> leftList, List<T> rightList) {
    List<String> list = new ArrayList<String>();
    try {
      ObjectMapper mapper = new ObjectMapper();
      JSONObject leftObject = new JSONObject();
      leftObject.put("left", leftList);
      JSONArray leftArray = leftObject.getJSONArray("left");
      JSONObject rightObject = new JSONObject();
      rightObject.put("right", rightList);
      JSONArray rightArray = rightObject.getJSONArray("right");
      TypeReference<HashMap<String, Object>> type = new TypeReference<HashMap<String, Object>>() {};
      Map<String, Object> leftMap = null;
      Map<String, Object> rightMap = null;
      for (int i = 0; i < leftArray.length(); i++) {
        leftMap = mapper.readValue(leftArray.get(i).toString(), type);
        for (int j = 0; j < rightArray.length(); j++) {
          rightMap = mapper.readValue(rightArray.get(j).toString(), type);
          if (leftArray.getJSONObject(i).getString("sfid").equalsIgnoreCase(
              rightArray.getJSONObject(j).getString("sfid")) && !leftMap.equals(rightMap)) {
            if (!Maps.difference(leftMap, rightMap).entriesDiffering().isEmpty()) {
              list.add(leftArray.getJSONObject(i).getString("sfid").toString());
              LOGGER.info(leftArray.getJSONObject(i).getString("sfid").toString() + " >>> "
                  + Maps.difference(leftMap, rightMap).entriesDiffering().toString());
            }
          }
        }
      }
    } catch (JsonProcessingException e) {
      LOGGER.error("Error JsonProcessingException: {} ", e.getMessage());
    } catch (JSONException e) {
      LOGGER.error("Error JSONException: {} ", e.getMessage());
    } catch (IOException e) {
      LOGGER.error("Error IOException: {} ", e.getMessage());
    }
    return list;
  }

  public static <T> List<String> getIdListFromObjectList(List<T> list) {
    List<String> listId = new ArrayList<>();
    JSONObject leftObject = new JSONObject();
    leftObject.put("list", list);
    JSONArray leftArray = leftObject.getJSONArray("list");
    for (int i = 0; i < leftArray.length(); i++) {
      listId.add(leftArray.getJSONObject(i).getString("sfid").toString());
    }
    return listId;
  }

  public static Map<String, String> getHeadersInfo(HttpServletRequest request) {
    Map<String, String> map = new HashMap<String, String>();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String key = (String) headerNames.nextElement();
      String value = request.getHeader(key);
      map.put(key, value);
    }
    return map;
  }
  
  public static String extractCookie(Map<String, String> header) {
    if (header.containsKey("jtx_session")) {
      return "JTX_SESSION=" + header.get("jtx_session");
    } else if (header.containsKey("cookie")) {
      return header.get("cookie");
    }
    return null;
  }
  
}
