package jp.co.japantaxi.config;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;

import jp.co.japantaxi.utils.Constant;

@Component
public class CacheManagerConfig {

  private static Logger LOGGER = LoggerFactory.getLogger(CacheManagerConfig.class);

  private static Map<String, String> store = new HashMap<>();

  private static Map<String, Integer> errorStore = new HashMap<>();

  private static Map<String, List<String>> listStore = new HashMap<>();

  @Autowired
  private SalesforceConfig salesforceConfig;

  @Cacheable(cacheNames = {"token"}, key = "#id")
  public String getToken(String data) {
    String token = store.get(data);
    if (token == null || token.isEmpty()) {
      LOGGER.info("Get token data in the first time ...");
      JsonNode jsonNode = salesforceConfig.getAccessToken();
      store.put("token", jsonNode.get("access_token").asText());
      store.put("uri", jsonNode.get("instance_url").asText() + "/services/apexrest/stockholm/");
      token = store.get(data);
    } else {
      LOGGER.info("Executing get token data in cache memory token store ...");
    }
    return token;
  }

  @Cacheable
  public void setToken(String token) {
    store.put("token", token);
  }
  
  @Cacheable(cacheNames = {"uri"}, key = "#uri")
  public String getUri(String uri) {
    String instanceUrl = store.get(uri);
    if (instanceUrl == null) {
      store.put("token", null);
      getToken("token");
      instanceUrl = store.get(uri);
    }
    return instanceUrl;
  }

  @Cacheable(cacheNames = {"next_page_token"}, key = "#nptk")
  public String getNextPageToken(String nextPageToken) {
    String res = store.get(nextPageToken);
    if (res != null) {
      return res;
    }
    return null;
  }

  @Cacheable
  public void setNextPageToken(String nextPageToken) {
    store.put("next_page_token", nextPageToken);
  }

  @Cacheable(cacheNames = {"fromdatetime"}, key = "#fdt")
  public String getFromDateTime(String fromdatetime) {
    String res = store.get(fromdatetime);
    if (res != null) {
      return res;
    }
    return null;
  }

  @Cacheable
  public void setFromDateTime(String fromdatetime) {
    store.put(Constant.FROM_DATETIME, fromdatetime);
  }

  @Cacheable(cacheNames = {"todatetime"}, key = "#tdt")
  public String getToDateTime(String todatetime) {
    String res = store.get(todatetime);
    if (res != null) {
      return res;
    }
    return null;
  }

  @Cacheable
  public void setToDateTime(String todatetime) {
    store.put(Constant.TO_DATETIME, todatetime);
  }

  @Cacheable(cacheNames = {"list"}, key = "#listid")
  public List<String> getListObjectId(String context) {
    List<String> res = listStore.get(context.toLowerCase(Locale.ENGLISH));
    if (res != null) {
      return res;
    }
    return null;
  }

  @Cacheable
  public void setListObjectId(String context, List<String> ids) {
    listStore.put(context.toLowerCase(), ids);
  }

  @Cacheable(cacheNames = {"error"}, key = "#erid")
  public Integer getErrorCode(String context) {
    Integer res = errorStore.get(context.toLowerCase(Locale.ENGLISH));
    if (res != null) {
      return res;
    }
    return HttpStatus.OK.value();
  }

  @Cacheable
  public void setErrorCode(String context, int code) {
    errorStore.put(context.toLowerCase(Locale.ENGLISH), code);
  }

  public void clearMap(String context) {
    setNextPageToken(null);
    setListObjectId(context, null);
    setErrorCode(context, HttpStatus.OK.value());
  }

}
