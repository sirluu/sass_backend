package jp.co.japantaxi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.japantaxi.utils.JsonMapper;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SalesforceConfig {

  final Logger LOGGER = LoggerFactory.getLogger(SalesforceConfig.class);

  @Value("${sf.version}")
  private String version;

  @Value("${security.oauth2.client.client_id}")
  private String clientId;

  @Value("${security.oauth2.client.client_secret}")
  private String clientSecret;

  @Value("${security.oauth2.client.grant-type}")
  private String grantType;

  @Value("${security.oauth2.client.username}")
  private String username;

  @Value("${security.oauth2.client.password}")
  private String password;

  @Value("${security.oauth2.client.access_token_uri}")
  private String accessTokenUri;

  @Autowired
  private CacheManagerConfig cacheManagerConfig;

  public JsonNode getAccessToken() {
    JsonNode accessToken = null;
    CloseableHttpClient httpclient = HttpClients.createDefault();
    try {
      List<NameValuePair> loginParams = new ArrayList<>();
      loginParams.add(new BasicNameValuePair("client_id", clientId));
      loginParams.add(new BasicNameValuePair("client_secret", clientSecret));
      loginParams.add(new BasicNameValuePair("grant_type", grantType));
      loginParams.add(new BasicNameValuePair("username", username));
      loginParams.add(new BasicNameValuePair("password", password));
      HttpPost post = new HttpPost(accessTokenUri);
      post.setEntity(new UrlEncodedFormEntity(loginParams));
      HttpResponse loginResponse = httpclient.execute(post);
      ObjectMapper mapper = JsonMapper.newMapper();
      accessToken = mapper.readValue(loginResponse.getEntity().getContent(), JsonNode.class);
    } catch (IOException e) {
      LOGGER.error("IOException: {} ", e.getMessage());
    } finally {
      if (httpclient != null) {
        try {
          httpclient.close();
        } catch (IOException e) {
          LOGGER.error("IOException: {} ", e.getMessage());
        }
      }
    }
    return accessToken;
  }

  public HttpHeaders getHeaders() {
    String token = cacheManagerConfig.getToken("token");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept-Version", version);
    headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
    headers.setBearerAuth(token);
    return headers;
  }
}
