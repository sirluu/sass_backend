package jp.co.japantaxi.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.japantaxi.config.CacheManagerConfig;
import jp.co.japantaxi.config.SalesforceConfig;
import jp.co.japantaxi.model.Account;
import jp.co.japantaxi.model.AppCompany;
import jp.co.japantaxi.model.BankAccountInformation;
import jp.co.japantaxi.model.BankMaster;
import jp.co.japantaxi.model.BatchStatus;
import jp.co.japantaxi.model.FareTable;
import jp.co.japantaxi.model.ParameterRequest;
import jp.co.japantaxi.model.PaymentSystemLinkInfor;
import jp.co.japantaxi.utils.Constant;
import jp.co.japantaxi.utils.ConvertDataUtil;
import jp.co.japantaxi.utils.JsonMapper;

@RestController
@RequestMapping
public class SalesforceResponseController {

  static final Logger LOGGER = LoggerFactory.getLogger(SalesforceResponseController.class);

  private ObjectMapper mapper = JsonMapper.newMapper();

  @Autowired
  private SalesforceConfig salesforceConfig;

  @Autowired
  private CacheManagerConfig cacheManagerConfig;

  @Autowired
  private BatchController batchController;

  @Autowired
  private RestTemplate restTemplate;
  
  @Autowired
  public WorkerController workerController;
  
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  public ParameterRequest parameterRequest(String context) {
    ParameterRequest parameterRequest = new ParameterRequest();
    List<String> listResponse = cacheManagerConfig.getListObjectId(context);
    if (!listResponse.isEmpty()) {
      String ids = "";
      for (String sfid : listResponse) {
        ids = ids + "'" + sfid + "',";
      }
      ids = ids.substring(0, ids.length() - 1);
      parameterRequest.setIds(ids);
    }
    return parameterRequest;
  }

  /**
   * try catch: Sentry 連携しエラー通知を行う
   * 
   * @param context
   * @return JSONArray
   */
  public JSONArray callSalesforce(String context, BatchStatus batchStatus) {
    JSONArray jsonArray = new JSONArray();
    restTemplate.getMessageConverters().add(0,
        new StringHttpMessageConverter(StandardCharsets.UTF_8));
    HttpEntity<String> entity = new HttpEntity<>(salesforceConfig.getHeaders());
    String uri = cacheManagerConfig.getUri("uri") + context + "?startTime="
        + cacheManagerConfig.getFromDateTime(Constant.FROM_DATETIME) + "&endTime="
        + cacheManagerConfig.getToDateTime(Constant.TO_DATETIME);
    String nptk = cacheManagerConfig.getNextPageToken("next_page_token");
    if (nptk != null) {
      uri = uri + "&nextPageToken=" + nptk;
    }
    ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
    try {
      response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
    } catch (HttpStatusCodeException  e) {
      if (Constant.checkError(e.getRawStatusCode()) == Constant.ERRORCODE.UNAUTHORIZED) {
		cacheManagerConfig.setToken(null);
	    LOGGER.warn("Token expired or invalid >>> need reset token !!!");
      }
      cacheManagerConfig.setErrorCode(context, e.getRawStatusCode());
      // 内容：コード(E00)、取得オブジェクト名、SFエラーコード、SFエラー内容 400,404,415,500
	  LOGGER.error(String.format("%s >>> %s >>> ErrorCode: %s >>> Error: %s",
	      	Constant.NORMALCODE.E00, Constant.API_GET + context, e.getRawStatusCode(), e.getMessage()));
	  StringBuilder sb = new StringBuilder();
      sb.append(batchStatus.getStatusinfo());
      sb.append("\n");
      sb.append(Constant.API_GET + context);
      batchStatus.setStatusinfo(sb.toString());
      batchController.updateBatchStatus(batchStatus, false, false, true, false);
      return null;
    }
    String res = response.getBody().toString();
    JSONObject bodyJsonObject = new JSONObject(res);
    if (!bodyJsonObject.isEmpty()) {
      jsonArray = bodyJsonObject.getJSONArray("results");
      try {
        String nextPageToken = bodyJsonObject.get("nextPageToken").toString().replace("{}", "");
        if (nextPageToken != null) {
          LOGGER.info("Call {} SalesforceAPI with nextPageToken: {} ", context, nextPageToken);
          cacheManagerConfig.setNextPageToken(nextPageToken);
        }
      } catch (Exception e) {
        cacheManagerConfig.setNextPageToken(null);
      }
      // 内容：コード(N01)取得オブジェクト名、取得開始、取得終了時間、成功
	  LOGGER.info(String.format("%s >>> call salesforce %s API: from %s to %s >>> %s",
		  Constant.NORMALCODE.N01, context, cacheManagerConfig.getFromDateTime(Constant.FROM_DATETIME),
		  cacheManagerConfig.getToDateTime(Constant.TO_DATETIME), response.getStatusCode()));
    }
    return jsonArray;
  }

  /**
   * @param context
   * @param batchStatus
   * @return List<Object>
   */
  public List<Object> getListObjectFromSalesforce(String context, BatchStatus batchStatus) {
    List<Object> listObj = new ArrayList<>();
    int error = cacheManagerConfig.getErrorCode(context.toLowerCase());
    if (Constant.checkError(error) == null) {
      JSONArray jsonArray = callSalesforce(context, batchStatus);
      if (jsonArray != null) {
          listObj = JsonMapper.toList(jsonArray);
          batchController.updateBatchStatus(batchStatus, false, false, false, true);
          cacheManagerConfig.setListObjectId(context, getListObjectIdFromSalesforce(context, listObj));
      }
    } else {
        // 内容：コード(E00)、取得オブジェクト名、SFエラーコード、SFエラー内容 400,404,415,500
      LOGGER.error(String.format("%s >>> %s >>> error at %s >>> ErrorCode: %s",
          	Constant.NORMALCODE.E00, Constant.API_GET, context, error));
  	  StringBuilder sb = new StringBuilder();
      sb.append(batchStatus.getStatusinfo());
      sb.append("\n");
      sb.append(Constant.API_GET + context);
      batchStatus.setStatusinfo(sb.toString());
      batchController.updateBatchStatus(batchStatus, false, false, true, false);
    }
    return listObj;
  }

  public List<String> getListObjectIdFromSalesforce(String context, List<Object> listResponse) {
    List<String> resList = new ArrayList<>();
    if (!listResponse.isEmpty()) {
      JSONObject jsonObject = new JSONObject();
      for (Object object : listResponse) {
        try {
          jsonObject = new JSONObject(mapper.writeValueAsString(object));
          resList.add(jsonObject.get("id").toString());
        } catch (JsonProcessingException e) {
          LOGGER.error(
              Constant.NORMALCODE.E03 + " >>> error getList" + context + "IdFromSalesforce: {} ",
              e.getMessage());
        }
      }
    }
    return resList;
  }

  /**
   * @param batchStatus
   * @return List<Object> try catch: API-GET[オブジェクト名（取得エラーになったオブジェクト名）] try catch: Sentry
   *         連携しエラー通知を行う
   */
  public List<Account> getListAccountFromSalesforce(BatchStatus batchStatus) {
    List<Account> resList = new ArrayList<>();
    Account account = new Account();
    List<Object> listResponse = getListObjectFromSalesforce(Constant.ACCOUNT, batchStatus);
    if (!listResponse.isEmpty()) {
      for (Object object : listResponse) {
        try {
          account = ConvertDataUtil
              .convertJsonObjectToAccount(new JSONObject(mapper.writeValueAsString(object)));
          resList.add(account);
        } catch (JsonProcessingException e) {
          workerController.commonError(Constant.API_GET + Constant.ACCOUNT, batchStatus, e);
        }
      }
    }
    return resList;
  }

  /**
   * @param batchStatus
   * @return List<Object> try catch: API-GET[オブジェクト名（取得エラーになったオブジェクト名）] try catch: Sentry
   *         連携しエラー通知を行う
   */
  public List<FareTable> getListFareTableFromSalesforce(BatchStatus batchStatus) {
    List<FareTable> resList = new ArrayList<>();
    FareTable fareTable = new FareTable();
    List<Object> listResponse = getListObjectFromSalesforce(Constant.FARETABLE, batchStatus);
    if (!listResponse.isEmpty()) {
      for (Object object : listResponse) {
        try {
          fareTable = ConvertDataUtil
              .convertJsonObjectToFareTable(new JSONObject(mapper.writeValueAsString(object)));
          resList.add(fareTable);
        } catch (JsonProcessingException e) {
          workerController.commonError(Constant.API_GET + Constant.FARETABLE, batchStatus, e);
        }
      }
    }
    return resList;
  }

  /**
   * @param batchStatus
   * @return List<Object> try catch: API-GET[オブジェクト名（取得エラーになったオブジェクト名）] try catch: Sentry
   *         連携しエラー通知を行う
   */
  public List<BankMaster> getListBankMasterFromSalesforce(BatchStatus batchStatus) {
    List<BankMaster> resList = new ArrayList<>();
    BankMaster bankMaster = new BankMaster();
    List<Object> listResponse = getListObjectFromSalesforce(Constant.BANKMASTER, batchStatus);
    if (!listResponse.isEmpty()) {
      for (Object object : listResponse) {
        try {
          bankMaster = ConvertDataUtil
              .convertJsonObjectToBankMaster(new JSONObject(mapper.writeValueAsString(object)));
          resList.add(bankMaster);
        } catch (JsonProcessingException e) {
          workerController.commonError(Constant.API_GET + Constant.BANKMASTER, batchStatus, e);
        }
      }
    }
    return resList;
  }

  /**
   * @param batchStatus
   * @return List<Object> try catch: API-GET[オブジェクト名（取得エラーになったオブジェクト名）] try catch: Sentry
   *         連携しエラー通知を行う
   */
  public List<BankAccountInformation> getListBankAccountInformationFromSalesforce(
      BatchStatus batchStatus) {
    List<BankAccountInformation> resList = new ArrayList<>();
    BankAccountInformation bankAccountInfor = new BankAccountInformation();
    List<Object> listResponse =
        getListObjectFromSalesforce(Constant.BANKACCOUNTINFORMATION, batchStatus);
    if (!listResponse.isEmpty()) {
      for (Object object : listResponse) {
        try {
          bankAccountInfor = ConvertDataUtil.convertJsonObjectToBankAccountInformation(
              new JSONObject(mapper.writeValueAsString(object)));
          resList.add(bankAccountInfor);
        } catch (JsonProcessingException e) {
          workerController.commonError(Constant.API_GET
              + Constant.BANKACCOUNTINFORMATION, batchStatus, e);
        }
      }
    }
    return resList;
  }

  /**
   * @param batchStatus
   * @return List<Object> try catch: API-GET[オブジェクト名（取得エラーになったオブジェクト名）] try catch: Sentry
   *         連携しエラー通知を行う
   */
  public List<AppCompany> getListAppCompanyFromSalesforce(BatchStatus batchStatus) {
    List<AppCompany> resList = new ArrayList<>();
    AppCompany appCompany = new AppCompany();
    List<Object> listResponse = getListObjectFromSalesforce(Constant.APPCOMPANY, batchStatus);
    if (!listResponse.isEmpty()) {
      for (Object object : listResponse) {
        try {
          appCompany = ConvertDataUtil
              .convertJsonObjectToAppCompany(new JSONObject(mapper.writeValueAsString(object)));
          resList.add(appCompany);
        } catch (JsonProcessingException e) {
          workerController.commonError(Constant.API_GET + Constant.APPCOMPANY, batchStatus, e);
        }
      }
    }
    return resList;
  }

  /**
   * @param batchStatus
   * @return List<Object> try catch: API-GET[オブジェクト名（取得エラーになったオブジェクト名）] try catch: Sentry
   *         連携しエラー通知を行う
   */
  public List<PaymentSystemLinkInfor> getListPaymentSystemLinkInforFromSalesforce(
      BatchStatus batchStatus) {
    List<PaymentSystemLinkInfor> resList = new ArrayList<>();
    PaymentSystemLinkInfor linkInfor = new PaymentSystemLinkInfor();
    List<Object> listResponse =
        getListObjectFromSalesforce(Constant.PAYMENTSYSTEMLINKINFOR, batchStatus);
    if (!listResponse.isEmpty()) {
      for (Object object : listResponse) {
        try {
          linkInfor = ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(
              new JSONObject(mapper.writeValueAsString(object)));
          resList.add(linkInfor);
        } catch (JsonProcessingException e) {
          workerController.commonError(Constant.API_GET
              + Constant.PAYMENTSYSTEMLINKINFOR, batchStatus, e);
        }
      }
    }
    return resList;
  }

}
