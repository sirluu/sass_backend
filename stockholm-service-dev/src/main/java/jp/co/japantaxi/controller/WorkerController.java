package jp.co.japantaxi.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import jp.co.japantaxi.config.CacheManagerConfig;
import jp.co.japantaxi.config.SalesforceConfig;
import jp.co.japantaxi.mapper.stockholm.AccountMapper;
import jp.co.japantaxi.mapper.stockholm.AppCompanyMapper;
import jp.co.japantaxi.mapper.stockholm.BankAccountInformationMapper;
import jp.co.japantaxi.mapper.stockholm.BankMasterMapper;
import jp.co.japantaxi.mapper.stockholm.FareTableMapper;
import jp.co.japantaxi.mapper.stockholm.PaymentSystemLinkInforMapper;
import jp.co.japantaxi.mapper.stockholm.WorkerMapper;
import jp.co.japantaxi.model.BatchStatus;
import jp.co.japantaxi.model.ParameterRequest;
import jp.co.japantaxi.model.Worker;
import jp.co.japantaxi.utils.Constant;
import jp.co.japantaxi.utils.DateTimeUtil;
import jp.co.japantaxi.utils.Utility;

@RestController
@RequestMapping("/worker")
public class WorkerController {

  static final Logger LOGGER = LoggerFactory.getLogger(WorkerController.class);

  @Autowired
  public AccountMapper accountMapper;

  @Autowired
  public AppCompanyMapper appCompanyMapper;

  @Autowired
  public BankAccountInformationMapper bankAccountInformationMapper;

  @Autowired
  public FareTableMapper fareTableMapper;

  @Autowired
  public BankMasterMapper bankMasterMapper;

  @Autowired
  public PaymentSystemLinkInforMapper systemLinkInforMapper;

  @Autowired
  public WorkerMapper workerMapper;

  @Autowired
  public AccountController accountController;

  @Autowired
  public BankMasterController bankMasterController;

  @Autowired
  public PaymentController paymentController;

  @Autowired
  public AppCompanyController appCompanyController;

  @Autowired
  public BatchController batchController;

  @Autowired
  public SalesforceResponseController salesforceResponseController;

  @Autowired
  public CacheManagerConfig cacheManagerConfig;

  @Autowired
  public SalesforceConfig salesforceConfig;

  public void insertWorker(Worker worker) {
    try {
      workerMapper.insertWorker(worker);
    } catch (Exception e) {
      updateWorker(worker);
    }
  }

  public void updateWorker(Worker worker) {
    workerMapper.updateWorker(worker);
  }

  public Worker setWorker(String tableName) {
    Worker worker = new Worker();
    worker.setDeleteflg(false);
    worker.setSycapproveflg(false);
    worker.setSyncedtime(Timestamp.valueOf(LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
    worker.setTablename(tableName);
    return worker;
  }

  /**
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   * 
   * @param parameterRequest
   * @return ResponseEntity<Object>
   */
  public ResponseEntity<Object> validStarttime(ParameterRequest parameterRequest) {
    List<String> res = new ArrayList<>();
    try {
      if ((!DateTimeUtil.isValid(parameterRequest.getStartTime()))) {
        res.add("No request body starttime param !!!");
		LOGGER.error(String.format("%s >>> %s", Constant.NORMALCODE.E01,
				"error request body starttime param !!!"));
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      res.add(String.format("%s >>> %s", Constant.NORMALCODE.E01,
    		"error request body starttime param !!!"));
	  LOGGER.error(String.format("%s >>> %s", Constant.NORMALCODE.E01,
			"error request body starttime param !!!"));
      return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  /**
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   * 
   * @param parameterRequest
   * @return ResponseEntity<Object>
   */
  public ResponseEntity<Object> validParameter(ParameterRequest parameterRequest) {
    List<String> res = new ArrayList<>();
    JsonNode accessToken = salesforceConfig.getAccessToken();
    try {
      if ("invalid_grant".equalsIgnoreCase(accessToken.get("error").asText())) {
    	LOGGER.error(String.format("%s >>> %s", "Error authen to salesforce",
    			accessToken.get("error_description").asText()));    	
        res.add("Error authen to salesforce: " + accessToken.get("error_description").asText());
        return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
      }
    } catch (Exception e) {
      LOGGER.info("Valid account login to salesforce success");
      CacheManagerConfig.store.put("token", accessToken.get("access_token").asText());
      CacheManagerConfig.store.put("uri", accessToken.get("instance_url").asText() + "/services/apexrest/stockholm/");
    }
    String stMode = "";
    try {
      // 繝代Λ繝｡繝ｼ繧ｿ縺ｫstart_mode縺後↑縺�蝣ｴ蜷医�ｯ繝ｭ繧ｰ縺ｫ螳溯｡梧ュ蝣ｱ繧貞�ｺ蜉帙＠縺ｦ縲�ｼ薙∈驕ｷ遘ｻ
      stMode = parameterRequest.getStartMode();
      if (!Constant.STARTMODE.MANUAL.value.equalsIgnoreCase(stMode.toUpperCase())
    	  && !Constant.STARTMODE.CRON.value.equalsIgnoreCase(stMode.toUpperCase())
          && !Constant.STARTMODE.USER.value.equalsIgnoreCase(stMode.toUpperCase())) {
        res.add(Constant.NORMALCODE.E01 + " >>> no request body startMode param  is CRON, USER or MANUAL !!!");
  	    LOGGER.error(String.format("%s >>> %s", Constant.NORMALCODE.E01,
  			  "error request body startMode param is CRON, USER or MANUAL !!!"));
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      res.add(Constant.NORMALCODE.E01 + " >>> error request body startMode param !!!");
      LOGGER.error(String.format("%s >>> %s", Constant.NORMALCODE.E01,
  			  "error request body startMode param !!!"));
      return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }
    try {
      String pcMode = parameterRequest.getProcessMode();
      if (!Constant.PROCESSMODE.CLEAR.value.equalsIgnoreCase(pcMode.toUpperCase())
          && !Constant.PROCESSMODE.COREDATECREAT.value.equalsIgnoreCase(pcMode.toUpperCase())
          && !Constant.PROCESSMODE.GETSF.value.equalsIgnoreCase(pcMode.toUpperCase())) {
  	    LOGGER.error(String.format("%s >>> %s", Constant.NORMALCODE.E01,
  			  "error request body processMode param is CLEAR or GETSF or COREDATECREAT!!!"));
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
      }
      if ((!Constant.PROCESSMODE.CLEAR.value.equalsIgnoreCase(pcMode.toUpperCase()) && !Constant.STARTMODE.CRON.value.equalsIgnoreCase(stMode.toUpperCase()))
			&& (!Constant.PROCESSMODE.CLEAR.value.equalsIgnoreCase(pcMode.toUpperCase()) && !Constant.STARTMODE.USER.value.equalsIgnoreCase(stMode.toUpperCase()))) {
		return validStarttime(parameterRequest);
      }
    } catch (Exception e) {
      LOGGER.info(" >>> no request body processMode param !!!");
      if (!Constant.STARTMODE.CRON.value.equalsIgnoreCase(stMode.toUpperCase()) && !Constant.STARTMODE.USER.value.equalsIgnoreCase(stMode.toUpperCase())) {
		return validStarttime(parameterRequest);
      }
    }
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  /**
   * @param parameterRequest
   * @return ResponseEntity<Object> try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  @PostMapping(value = "/synchronize", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> synchronize(@RequestBody ParameterRequest parameterRequest) {
    List<String> res = new ArrayList<>();

    // 1-1 繝ｪ繧ｯ繧ｨ繧ｹ繝医ヱ繝ｩ繝｡繝ｼ繧ｿ繝ｼ繝√ぉ繝�繧ｯ
    ResponseEntity<Object> response = validParameter(parameterRequest);
    if (response.getStatusCode().value() == HttpStatus.OK.value()) {
      res.add("Sync process begin at : " + LocalDateTime.now(DateTimeUtil.TIMEZONE_UTC.toZoneId()));
      // 1-2 繝舌ャ繝∫憾諷九ユ繝ｼ繝悶Ν縺ｮ繝舌ャ繝√さ繝ｼ繝峨メ繧ｧ繝�繧ｯ
      BatchStatus batchStatus = batchController.getBatchStatus();
      if (batchStatus == null) {
        res.add(Constant.NORMALCODE.E04 + " >>> batchcode SBAT-001 don't exits !!!");
  	    LOGGER.error(String.format("%s >>> %s", Constant.NORMALCODE.E04,
  			  "batchcode SBAT-001 don't exits !!!"));
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
      }
      // 1-3 繝舌ャ繝∫憾諷九ユ繝ｼ繝悶Ν縺ｮ繧ｹ繝�繝ｼ繧ｿ繧ｹ縺君ULL縺ｧ縺ゅｋ遒ｺ隱阪☆繧�
      if (batchStatus.getStatus() != null && !"".equalsIgnoreCase(batchStatus.getStatus())
          && !batchStatus.getStatus().isEmpty()) {
        // null莉･螟悶�ｮ蝣ｴ蜷医�ｯ繝ｭ繧ｰ縺ｫ螳溯｡梧ュ蝣ｱ繧貞�ｺ蜉帙＠縺ｦ縲�ｼ薙∈驕ｷ遘ｻ
        res.add(Constant.NORMALCODE.E02 + " >>> batch status is error !!!");
  	    LOGGER.error(String.format("%s >>> %s", Constant.NORMALCODE.E02,
  			  "batch status is error !!!"));
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
      }
      // Set start_mode to Property
      System.setProperty(Constant.START_MODE, parameterRequest.getStartMode().toUpperCase());
      // 繝舌ャ繝∫憾諷九ユ繝ｼ繝悶Ν縺ｮ襍ｷ蜍輔Δ繝ｼ繝峨ｒ譖ｴ譁ｰ縺吶ｋ
      String startMode = System.getProperty(Constant.START_MODE).toUpperCase();
      batchController.updateBatchStatus(batchStatus, true, false, false, false);

      Constant.PROCESSMODE processMode = Constant.PROCESSMODE.NULL;
      try {
        String pcMode = parameterRequest.getProcessMode();
        processMode = Constant.checkProcessMode(pcMode.toString().trim().toUpperCase());
        batchStatus.setStartupmode(startMode.toLowerCase());
      } catch (Exception e) {
        LOGGER.info(" >>> no request process_mode param !!!");
      }
      // 繝ｭ繧ｰ縺ｫ螳溯｡梧ュ蝣ｱ繧貞�ｺ蜉帙☆繧九�ょ��螳ｹ�ｼ壹さ繝ｼ繝�(N00)縲∬ｵｷ蜍輔Δ繝ｼ繝峨�∝�ｦ逅�繝｢繝ｼ繝峨��髢句ｧ区律譎�
      // 髢句ｧ区律譎�
	  LOGGER.info(String.format("%s >>> Start mode: %s >>> Process mode: %s >>> Sync process begin at: %s", 
			  Constant.NORMALCODE.N00,	startMode, processMode, LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
      // If cron, set back start time to parameterRequest
	  if (Constant.STARTMODE.CRON.value.equalsIgnoreCase(startMode) || Constant.STARTMODE.USER.value.equalsIgnoreCase(startMode)) {
		try {
			String fromDateTime = DateTimeUtil.getStringFromTimestamp(batchStatus.getFromdatetime(), DateTimeUtil.DATE_TIME_FM);
			if (DateTimeUtil.isValid(fromDateTime)) {
			parameterRequest.setStartTime(fromDateTime);
		  } else {
		     res.add(String.format("%s >>> %s", "Error startime format", fromDateTime));
		  	 LOGGER.error(String.format("%s >>> %s", "Error startime format", fromDateTime));
		     return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
		  }
		} catch (Exception e) {
	      res.add(String.format("%s >>> %s", "Error get startime from DB", e.getMessage()));
	  	  LOGGER.error(String.format("%s >>> %s", "Error get startime from DB", e.getMessage()));
	      return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
		}
      }
      cacheManagerConfig.setFromDateTime(parameterRequest.getStartTime());
      String toDateTime = DateTimeUtil.getStringFromDate(DateTimeUtil.getDateFromString(DateTimeUtil.getStringFromDate(new Date(), DateTimeUtil.DATE_TIME_FM), DateTimeUtil.DATE_TIME_FM, DateTimeUtil.TIMEZONE_GMT) , DateTimeUtil.DATE_TIME_FM);
      cacheManagerConfig.setToDateTime(toDateTime);

      // 2.SFDC騾｣謳ｺ蜃ｦ逅�
      if (Constant.STARTMODE.CRON.value.equalsIgnoreCase(startMode) || Constant.STARTMODE.USER.value.equalsIgnoreCase(startMode) || Constant.STARTMODE.MANUAL.value.equalsIgnoreCase(startMode)) {
        // cron�ｼ壹け繝ｼ繝ｭ繝ｳ縺ｫ繧医ｋ閾ｪ蜍戊ｵｷ蜍�
        // manual�ｼ壽焔蜍募ｮ溯｡�
        if (Constant.PROCESSMODE.GETSF.equals(processMode)) {
          // cron+getSF�ｼ售F蜿門ｾ怜�ｦ逅�縺ｮ縺ｿ縺ｧ襍ｷ蜍包ｼ�2竏�1縺ｮ縺ｿ螳溯｡鯉ｼ会ｼ郁�ｪ蜍戊ｵｷ蜍包ｼ�
          // manual+getSF�ｼ售F蜿門ｾ怜�ｦ逅�縺ｮ縺ｿ縺ｧ襍ｷ蜍包ｼ�2竏�1縺ｮ縺ｿ螳溯｡鯉ｼ会ｼ域焔蜍戊ｵｷ蜍包ｼ�
          getSF(parameterRequest, batchStatus);
        } else if (Constant.PROCESSMODE.COREDATECREAT.equals(processMode)) {
          // cron+coreDateCreat�ｼ壼渕蟷ｹDB逕ｨ繝�繝ｼ繧ｿ蜉�蟾･縺ｧ縺ｮ襍ｷ蜍包ｼ�2-2縺ｮ縺ｿ螳溯｡鯉ｼ会ｼ郁�ｪ蜍戊ｵｷ蜍包ｼ�
          // manual+coreDateCreat�ｼ壼渕蟷ｹDB逕ｨ繝�繝ｼ繧ｿ蜉�蟾･縺ｧ縺ｮ襍ｷ蜍包ｼ�2-2縺ｮ縺ｿ螳溯｡鯉ｼ会ｼ郁�ｪ蜍戊ｵｷ蜍包ｼ�
          coreDateCreat(parameterRequest, batchStatus);
        } else if (Constant.PROCESSMODE.CLEAR.equals(processMode)) {
          // cron+clear�ｼ壽ｴ励＞譖ｿ縺域ｩ溯�ｽ縺ｧ螳溯｡鯉ｼ郁�ｪ蜍戊ｵｷ蜍包ｼ�
          // manual+clear�ｼ壽ｴ励＞譖ｿ縺域ｩ溯�ｽ縺ｧ螳溯｡鯉ｼ郁�ｪ蜍戊ｵｷ蜍包ｼ�
          clearRawData();
        } else if (Constant.PROCESSMODE.NULL.equals(processMode)) {
          // cron�ｼ壹け繝ｼ繝ｭ繝ｳ縺ｫ繧医ｋ閾ｪ蜍戊ｵｷ蜍�
          // manual�ｼ壹け繝ｼ繝ｭ繝ｳ縺ｫ繧医ｋ閾ｪ蜍戊ｵｷ蜍�
          getSF(parameterRequest, batchStatus);
          coreDateCreat(parameterRequest, batchStatus);
        }
      }

      if ((Constant.STARTMODE.CRON.value.equalsIgnoreCase(startMode) && !Constant.PROCESSMODE.CLEAR.equals(processMode))
			|| (Constant.STARTMODE.USER.value.equalsIgnoreCase(startMode) && !Constant.PROCESSMODE.CLEAR.equals(processMode))) {
		batchStatus.setFromdatetime(DateTimeUtil.getTimestampFromString(toDateTime, DateTimeUtil.DATE_TIME_FM));
		batchController.updateBatchStatus(batchStatus, false, false, false, false);
      }
      if (processMode != null) {
		batchStatus.setStartupmode(startMode.toLowerCase() + "+" + processMode.value.toLowerCase());
	  }
      // 3.繧ｸ繝ｧ繝門�ｦ逅�邨ゆｺ�
      // 3-1繝舌ャ繝∫憾諷九ユ繝ｼ繝悶Ν縺ｮ譖ｴ譁ｰ譌･譎ゅｒ縲檎樟蝨ｨ譌･譎ゅ�阪〒譖ｴ譁ｰ縺吶ｋ
      if (!batchStatus.getStatusinfo().isEmpty()) {
        batchController.updateBatchStatus(batchStatus, false, false, true, false);
      } else {
        batchController.updateBatchStatus(batchStatus, false, true, false, false);
      }
      // 3-2 繧ｸ繝ｧ繝也ｵゆｺ�縺ｮ迥ｶ豕√ｒ繝ｭ繧ｰ縺ｫ蜃ｺ蜉帙☆繧� 蜀�螳ｹ�ｼ壹さ繝ｼ繝�(N02)縲∬ｵｷ蜍輔Δ繝ｼ繝峨�∝�ｦ逅�繝｢繝ｼ繝峨��髢句ｧ区律譎ゅ�∫ｵゆｺ�譌･譎�
	  LOGGER.info(String.format("%s >>> Start mode: %s >>> Process mode: %s >>> Sync process end at: %s", 
			  Constant.NORMALCODE.N02,	startMode, processMode, LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
      res.add("Sync process end at : " + LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
      return new ResponseEntity<>(res, HttpStatus.OK);
    }
    return response;
  }

  // 2-1�ｼ遺�ｻ2-2縺ｮ縺ｿ螳溯｡後〒襍ｷ蜍輔＠縺溷�ｴ蜷医�ｯ蜃ｦ逅�繧偵せ繧ｭ繝�繝励☆繧具ｼ�
  public void getSF(ParameterRequest parameterRequest, BatchStatus batchStatus) {
    LOGGER.info("Account getSF process begin at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    accountController.getSFAccount(parameterRequest, batchStatus);
    LOGGER.info("Account getSF process end at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    LOGGER.info("FareTable getSF process begin at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    accountController.getSFFareTable(parameterRequest, batchStatus);
    LOGGER.info("FareTable getSF process end at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    LOGGER.info("BankAccountInformation getSF process begin at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    bankMasterController.getSFBankAccountInformation(parameterRequest, batchStatus);
    LOGGER.info("BankAccountInformation getSF process end at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    LOGGER.info("BankMaster getSF process begin at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    bankMasterController.getSFBankMaster(parameterRequest, batchStatus);
    LOGGER.info("BankMaster getSF process end at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    LOGGER.info("AppCompany getSF process begin at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    appCompanyController.getSFAppCompany(parameterRequest, batchStatus);
    LOGGER.info("AppCompany getSF process end at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    LOGGER.info("PaymentSystemLinkInfor getSF process begin at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    paymentController.getSFPaymentSystemLinkInfor(parameterRequest, batchStatus);
    LOGGER.info("PaymentSystemLinkInfor getSF process end at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
  }

  // 2-2 蝓ｺ蟷ｹDB逕ｨ繝�繝ｼ繧ｿ蜉�蟾･縺ｧ縺ｮ襍ｷ蜍包ｼ�2-2縺ｮ縺ｿ螳溯｡鯉ｼ�
  public void coreDateCreat(ParameterRequest parameterRequest, BatchStatus batchStatus) {
	//Convert time
	parameterRequest.setStartTime(Utility.parseString(parameterRequest.getStartTime()));
    LOGGER.info("Account coreDateCreat process begin at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    accountController.coreDateCreatAccount(parameterRequest, batchStatus);
    LOGGER.info("Account coreDateCreat process end at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    LOGGER.info("FareTable coreDateCreat process begin at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    accountController.coreDateCreatFareTable(parameterRequest, batchStatus);
    LOGGER.info("FareTable coreDateCreat process end at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    LOGGER.info("BankAccountInformation coreDateCreat process begin at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    bankMasterController.coreDateCreatBankAccountInformation(parameterRequest, batchStatus);
    LOGGER.info("BankAccountInformation coreDateCreat process end at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    LOGGER.info("BankMaster coreDateCreat process begin at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    bankMasterController.coreDateCreatBankMaster(parameterRequest, batchStatus);
    LOGGER.info("BankMaster coreDateCreat process end at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    LOGGER.info("AppCompany coreDateCreat process begin at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    appCompanyController.coreDateCreatAppCompany(parameterRequest, batchStatus);
    LOGGER.info("AppCompany coreDateCreat process end at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    LOGGER.info("PaymentSystemLinkInfor coreDateCreat process begin at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
    paymentController.coreDateCreatPaymentSystemLinkInfor(parameterRequest, batchStatus);
    LOGGER.info("PaymentSystemLinkInfor coreDateCreat process end at : {} ",
        LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
  }

  // 豢励＞譖ｿ縺域ｩ溯�ｽ縺ｧ螳溯｡�
  public void clearRawData() {
    accountMapper.truncateAccount();
    appCompanyMapper.truncateAppCompany();
    bankAccountInformationMapper.truncateBankAccountInformation();
    bankMasterMapper.truncateBankMaster();
    fareTableMapper.truncateFareTable();
    systemLinkInforMapper.truncatePaymentSystemLinkInfor();
  }

  // 4.蜈ｱ騾壹お繝ｩ繝ｼ蜃ｦ逅�
  // 繝舌ャ繝∫憾諷九ユ繝ｼ繝悶Ν縺ｮ繧ｹ繝�繝ｼ繧ｿ繧ｹ繧偵�憩rror縲阪↓譖ｴ譁ｰ縺吶ｋ
  // 繝舌ャ繝∫憾諷九ユ繝ｼ繝悶Ν縺ｮ繧ｹ繝�繝ｼ繧ｿ繧ｹ諠�蝣ｱ縺ｫ繧ｨ繝ｩ繝ｼ蜀�螳ｹ繧堤匳骭ｲ縺吶ｋ
  // �ｼ医せ繝�繝ｼ繧ｿ繧ｹ諠�蝣ｱ縺後☆縺ｧ縺ｫ蟄伜惠縺吶ｋ蝣ｴ蜷医�ｯ霑ｽ險倥☆繧具ｼ�
  public void commonError(String content, BatchStatus batchStatus, Exception ex) {
    // 蜀�螳ｹ�ｼ壹さ繝ｼ繝�(E03)縲√お繝ｩ繝ｼ譌･譎ゅ�√お繝ｩ繝ｼ蜀�螳ｹ�ｼ�Exception蜀�螳ｹ�ｼ�
    LOGGER.error(String.format("%s >>> %s >>> exception: %s",
    	  Constant.NORMALCODE.E03, content, ex.getMessage()));
    StringBuilder sb = new StringBuilder();
    sb.append(batchStatus.getStatusinfo());
    sb.append("\n");
    sb.append(content);
    batchStatus.setStatusinfo(sb.toString());
    batchController.updateBatchStatus(batchStatus, false, false, true, false);
  }
}
