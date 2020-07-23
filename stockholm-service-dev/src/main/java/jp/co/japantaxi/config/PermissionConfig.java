package jp.co.japantaxi.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PermissionConfig {

  static Logger LOGGER = LoggerFactory.getLogger(PermissionConfig.class);

  @Value("${shanghai.account.url}")
  private String api;

  public JSONArray getPermissionGroups(String sesssion) {
    JSONArray jsonArray = new JSONArray();
    try {
      URL url = new URL(api + "/self");
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Cookie", "JTX_SESSION=" + sesssion);
      String tmp = null;
      BufferedReader br =
          new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("UTF-8")));
      StringBuilder stringBuilder = new StringBuilder();
      while ((tmp = br.readLine()) != null) {
        stringBuilder.append(tmp);
      }
      JSONObject jsonObject = new JSONObject(stringBuilder.toString()).getJSONObject("role");
      br.close();
      con.disconnect();
      jsonArray = jsonObject.getJSONArray("permissionGroups");
    } catch (IOException e) {
      LOGGER.warn("JTX_SESSION is null >>> " + e.getMessage());
    }
    return jsonArray;
  }
  
  public boolean checkApprove(JSONArray jsonArray) {
    JSONObject jsonObject = new JSONObject();
    JSONArray jArray = new JSONArray();
    if (!jsonArray.isEmpty()) {
      for (int i = 0; i < jsonArray.length(); i++) {
        jsonObject = new JSONObject(jsonArray.get(i).toString());
        if ("STOCKHOLM".equalsIgnoreCase(jsonObject.getString("permissionGroupId"))) {
          jArray = jsonObject.getJSONArray("permissions");
          for (int j = 0; j < jArray.length(); j++) {
            jsonObject = jArray.getJSONObject(j); 
            if ("true".equalsIgnoreCase(jsonObject.get("read").toString().toLowerCase())) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

}
