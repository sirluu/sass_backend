package jp.co.japantaxi.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jp.co.japantaxi.model.ManhattanAccount;

@Component
public class SecurityConfig {

  static Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

  @Value("${shanghai.account.url}")
  private String api;

  public JSONArray getPermissionGroups(String sesssion) {
    JSONArray jsonArray = new JSONArray();
    try {
      URL url = new URL(api + "/self");
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("Cookie", sesssion);
      String tmp = null;
      BufferedReader br =
          new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
      StringBuilder stringBuilder = new StringBuilder();
      while ((tmp = br.readLine()) != null) {
        stringBuilder.append(tmp);
      }
      JSONObject jsonObject = new JSONObject(stringBuilder.toString()).getJSONObject("role");
      jsonArray = jsonObject.getJSONArray("permissionGroups");
      br.close();
      con.disconnect();
    } catch (MalformedURLException e) {
      LOGGER.warn("Error MalformedURLException >>> " + e.getMessage());
    } catch (IOException e) {
      LOGGER.warn("Error IOException >>> " + e.getMessage());
    }
    return jsonArray;
  }

  public List<ManhattanAccount.PermissionGroups> convertPermissionGroups(JSONArray jsonArray) {
    List<ManhattanAccount.PermissionGroups> groups = new ArrayList<>();
    if (!jsonArray.isEmpty()) {
      ManhattanAccount.PermissionGroups permissionGroups = new ManhattanAccount.PermissionGroups();
      JSONObject jsonObject = new JSONObject();
      JSONArray permisArray = new JSONArray();
      ManhattanAccount.Permission permission = new ManhattanAccount.Permission();
      List<ManhattanAccount.Permission> permisList = new ArrayList<>();
      for (int i = 0; i < jsonArray.length(); i++) {
        permissionGroups = new ManhattanAccount.PermissionGroups();
        jsonObject = new JSONObject(jsonArray.get(i).toString());
        permissionGroups.setPermissionGroupId(jsonObject.getString("permissionGroupId"));
        permissionGroups.setPermissionGroupName(jsonObject.getString("permissionGroupName"));
        permisArray = jsonObject.getJSONArray("permissions");
        for (int j = 0; j < permisArray.length(); j++) {
          jsonObject = permisArray.getJSONObject(j);
          permisList = new ArrayList<>();
          permission = new ManhattanAccount.Permission();
          permission.setCreate(Boolean.valueOf(jsonObject.get("create").toString().trim()));
          permission.setDelete(Boolean.valueOf(jsonObject.get("delete").toString().trim()));
          permission.setPermissionId(Integer.parseInt(jsonObject.get("permissionId").toString().trim()));
          permission.setPermissionName(jsonObject.get("permissionName").toString().trim());
          permission.setRead(Boolean.valueOf(jsonObject.get("read").toString().trim()));
          permission.setUpdate(Boolean.valueOf(jsonObject.get("update").toString().trim()));
          permisList.add(j, permission);
          permissionGroups.setPermissions(permisList);
        }
        groups.add(i, permissionGroups);
      }
    }
    return groups;
  }

  public Boolean checkApprove(List<ManhattanAccount.PermissionGroups> groups) {
    int size = groups.size();
    if (size > 0) {
      ManhattanAccount.PermissionGroups permissionGroups = new ManhattanAccount.PermissionGroups();
      List<ManhattanAccount.Permission> permisList = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        permissionGroups = groups.get(i);
        if ("STOCKHOLM".equalsIgnoreCase(permissionGroups.getPermissionGroupId())) {
          permisList = permissionGroups.getPermissions();
          for (int j = 0; j < permisList.size(); j++) {
            if (permisList.get(j).getRead()) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

}
