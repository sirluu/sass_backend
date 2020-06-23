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
   * try catch: Sentry 鬨ｾ�ｽ｣隰ｳ�ｽｺ邵ｺ蜉ｱ縺顔ｹ晢ｽｩ郢晢ｽｼ鬨ｾ螟り｡咲ｹｧ螳夲ｽ｡蠕娯鴬
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
   * try catch: Sentry 鬨ｾ�ｽ｣隰ｳ�ｽｺ邵ｺ蜉ｱ縺顔ｹ晢ｽｩ郢晢ｽｼ鬨ｾ螟り｡咲ｹｧ螳夲ｽ｡蠕娯鴬
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
      // 郢昜ｻ｣ﾎ帷ｹ晢ｽ｡郢晢ｽｼ郢ｧ�ｽｿ邵ｺ�ｽｫstart_mode邵ｺ蠕娯�醍ｸｺ�ｿｽ陜｣�ｽｴ陷ｷ蛹ｻ�ｿｽ�ｽｯ郢晢ｽｭ郢ｧ�ｽｰ邵ｺ�ｽｫ陞ｳ貅ｯ�ｽ｡譴ｧ繝･陜｣�ｽｱ郢ｧ雋橸ｿｽ�ｽｺ陷牙ｸ呻ｼ�邵ｺ�ｽｦ邵ｲ�ｿｽ�ｽｼ阮吮�磯ｩ包ｽｷ驕假ｽｻ
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
   * @return ResponseEntity<Object> try catch: Sentry 鬨ｾ�ｽ｣隰ｳ�ｽｺ邵ｺ蜉ｱ縺顔ｹ晢ｽｩ郢晢ｽｼ鬨ｾ螟り｡咲ｹｧ螳夲ｽ｡蠕娯鴬
   */
  @PostMapping(value = "/synchronize", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> synchronize(@RequestBody ParameterRequest parameterRequest) {
    List<String> res = new ArrayList<>();

    // 1-1 郢晢ｽｪ郢ｧ�ｽｯ郢ｧ�ｽｨ郢ｧ�ｽｹ郢晏現繝ｱ郢晢ｽｩ郢晢ｽ｡郢晢ｽｼ郢ｧ�ｽｿ郢晢ｽｼ郢昶�壹♂郢晢ｿｽ郢ｧ�ｽｯ
    ResponseEntity<Object> response = validParameter(parameterRequest);
    if (response.getStatusCode().value() == HttpStatus.OK.value()) {
      res.add("Sync process begin at : " + LocalDateTime.now(DateTimeUtil.TIMEZONE_UTC.toZoneId()));
      // 1-2 郢晁�後Ε郢昶悪諞ｾ隲ｷ荵昴Θ郢晢ｽｼ郢晄じﾎ晉ｸｺ�ｽｮ郢晁�後Ε郢昶�壹＆郢晢ｽｼ郢晏ｳｨ繝｡郢ｧ�ｽｧ郢晢ｿｽ郢ｧ�ｽｯ
      BatchStatus batchStatus = batchController.getBatchStatus();
      if (batchStatus == null) {
        res.add(Constant.NORMALCODE.E04 + " >>> batchcode SBAT-001 don't exits !!!");
  	    LOGGER.error(String.format("%s >>> %s", Constant.NORMALCODE.E04,
  			  "batchcode SBAT-001 don't exits !!!"));
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
      }
      // 1-3 郢晁�後Ε郢昶悪諞ｾ隲ｷ荵昴Θ郢晢ｽｼ郢晄じﾎ晉ｸｺ�ｽｮ郢ｧ�ｽｹ郢晢ｿｽ郢晢ｽｼ郢ｧ�ｽｿ郢ｧ�ｽｹ邵ｺ蜷婉LL邵ｺ�ｽｧ邵ｺ繧��ｽ矩￡�ｽｺ髫ｱ髦ｪ笘�郢ｧ�ｿｽ
      if (batchStatus.getStatus() != null && !"".equalsIgnoreCase(batchStatus.getStatus())
          && !batchStatus.getStatus().isEmpty()) {
        // null闔会ｽ･陞滓じ�ｿｽ�ｽｮ陜｣�ｽｴ陷ｷ蛹ｻ�ｿｽ�ｽｯ郢晢ｽｭ郢ｧ�ｽｰ邵ｺ�ｽｫ陞ｳ貅ｯ�ｽ｡譴ｧ繝･陜｣�ｽｱ郢ｧ雋橸ｿｽ�ｽｺ陷牙ｸ呻ｼ�邵ｺ�ｽｦ邵ｲ�ｿｽ�ｽｼ阮吮�磯ｩ包ｽｷ驕假ｽｻ
        res.add(Constant.NORMALCODE.E02 + " >>> batch status is error !!!");
  	    LOGGER.error(String.format("%s >>> %s", Constant.NORMALCODE.E02,
  			  "batch status is error !!!"));
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
      }
      // Set start_mode to Property
      System.setProperty(Constant.START_MODE, parameterRequest.getStartMode().toUpperCase());
      // 郢晁�後Ε郢昶悪諞ｾ隲ｷ荵昴Θ郢晢ｽｼ郢晄じﾎ晉ｸｺ�ｽｮ隘搾ｽｷ陷崎ｼ釆皮ｹ晢ｽｼ郢晏ｳｨ�ｽ定ｭ厄ｽｴ隴�ｽｰ邵ｺ蜷ｶ�ｽ�
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
      // 郢晢ｽｭ郢ｧ�ｽｰ邵ｺ�ｽｫ陞ｳ貅ｯ�ｽ｡譴ｧ繝･陜｣�ｽｱ郢ｧ雋橸ｿｽ�ｽｺ陷牙ｸ吮�郢ｧ荵晢ｿｽ繧�ｿｽ�ｿｽ陞ｳ�ｽｹ�ｿｽ�ｽｼ螢ｹ縺慕ｹ晢ｽｼ郢晢ｿｽ(N00)邵ｲ竏ｬ�ｽｵ�ｽｷ陷崎ｼ釆皮ｹ晢ｽｼ郢晏ｳｨ�ｿｽ竏晢ｿｽ�ｽｦ騾��ｿｽ郢晢ｽ｢郢晢ｽｼ郢晏ｳｨ�ｿｽ�ｿｽ鬮｢蜿･�ｽｧ蛹ｺ蠕玖ｭ趣ｿｽ
      // 鬮｢蜿･�ｽｧ蛹ｺ蠕玖ｭ趣ｿｽ
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
      String toDateTime = DateTimeUtil.getStringFromDate(DateTimeUtil.getDateFromString(DateTimeUtil.getStringFromDate(new Date(), DateTimeUtil.DATE_TIME_FM), DateTimeUtil.DATE_TIME_FM, DateTimeUtil.TIMEZONE_UTC) , DateTimeUtil.DATE_TIME_FM);
      cacheManagerConfig.setToDateTime(toDateTime);

      // 2.SFDC鬨ｾ�ｽ｣隰ｳ�ｽｺ陷�ｽｦ騾��ｿｽ
      if (Constant.STARTMODE.CRON.value.equalsIgnoreCase(startMode) || Constant.STARTMODE.USER.value.equalsIgnoreCase(startMode) || Constant.STARTMODE.MANUAL.value.equalsIgnoreCase(startMode)) {
        // cron�ｿｽ�ｽｼ螢ｹ縺醍ｹ晢ｽｼ郢晢ｽｭ郢晢ｽｳ邵ｺ�ｽｫ郢ｧ蛹ｻ�ｽ矩明�ｽｪ陷肴�奇ｽｵ�ｽｷ陷搾ｿｽ
        // manual�ｿｽ�ｽｼ螢ｽ辟碑恪蜍滂ｽｮ貅ｯ�ｽ｡�ｿｽ
        if (Constant.PROCESSMODE.GETSF.equals(processMode)) {
          // cron+getSF�ｿｽ�ｽｼ蜚ｮF陷ｿ髢��ｽｾ諤懶ｿｽ�ｽｦ騾��ｿｽ邵ｺ�ｽｮ邵ｺ�ｽｿ邵ｺ�ｽｧ隘搾ｽｷ陷榊桁�ｽｼ�ｿｽ2遶擾ｿｽ1邵ｺ�ｽｮ邵ｺ�ｽｿ陞ｳ貅ｯ�ｽ｡魃会ｽｼ莨夲ｽｼ驛�ｿｽ�ｽｪ陷肴�奇ｽｵ�ｽｷ陷榊桁�ｽｼ�ｿｽ
          // manual+getSF�ｿｽ�ｽｼ蜚ｮF陷ｿ髢��ｽｾ諤懶ｿｽ�ｽｦ騾��ｿｽ邵ｺ�ｽｮ邵ｺ�ｽｿ邵ｺ�ｽｧ隘搾ｽｷ陷榊桁�ｽｼ�ｿｽ2遶擾ｿｽ1邵ｺ�ｽｮ邵ｺ�ｽｿ陞ｳ貅ｯ�ｽ｡魃会ｽｼ莨夲ｽｼ蝓溽�碑恪謌奇ｽｵ�ｽｷ陷榊桁�ｽｼ�ｿｽ
          getSF(parameterRequest, batchStatus);
        } else if (Constant.PROCESSMODE.COREDATECREAT.equals(processMode)) {
          // cron+coreDateCreat�ｿｽ�ｽｼ螢ｼ貂戊濤�ｽｹDB騾包ｽｨ郢晢ｿｽ郢晢ｽｼ郢ｧ�ｽｿ陷会ｿｽ陝ｾ�ｽ･邵ｺ�ｽｧ邵ｺ�ｽｮ隘搾ｽｷ陷榊桁�ｽｼ�ｿｽ2-2邵ｺ�ｽｮ邵ｺ�ｽｿ陞ｳ貅ｯ�ｽ｡魃会ｽｼ莨夲ｽｼ驛�ｿｽ�ｽｪ陷肴�奇ｽｵ�ｽｷ陷榊桁�ｽｼ�ｿｽ
          // manual+coreDateCreat�ｿｽ�ｽｼ螢ｼ貂戊濤�ｽｹDB騾包ｽｨ郢晢ｿｽ郢晢ｽｼ郢ｧ�ｽｿ陷会ｿｽ陝ｾ�ｽ･邵ｺ�ｽｧ邵ｺ�ｽｮ隘搾ｽｷ陷榊桁�ｽｼ�ｿｽ2-2邵ｺ�ｽｮ邵ｺ�ｽｿ陞ｳ貅ｯ�ｽ｡魃会ｽｼ莨夲ｽｼ驛�ｿｽ�ｽｪ陷肴�奇ｽｵ�ｽｷ陷榊桁�ｽｼ�ｿｽ
          coreDateCreat(parameterRequest, batchStatus);
        } else if (Constant.PROCESSMODE.CLEAR.equals(processMode)) {
          // cron+clear�ｿｽ�ｽｼ螢ｽ�ｽｴ蜉ｱ�ｼ櫁ｭ厄ｽｿ邵ｺ蝓滂ｽｩ貅ｯ�ｿｽ�ｽｽ邵ｺ�ｽｧ陞ｳ貅ｯ�ｽ｡魃会ｽｼ驛�ｿｽ�ｽｪ陷肴�奇ｽｵ�ｽｷ陷榊桁�ｽｼ�ｿｽ
          // manual+clear�ｿｽ�ｽｼ螢ｽ�ｽｴ蜉ｱ�ｼ櫁ｭ厄ｽｿ邵ｺ蝓滂ｽｩ貅ｯ�ｿｽ�ｽｽ邵ｺ�ｽｧ陞ｳ貅ｯ�ｽ｡魃会ｽｼ驛�ｿｽ�ｽｪ陷肴�奇ｽｵ�ｽｷ陷榊桁�ｽｼ�ｿｽ
          clearRawData();
        } else if (Constant.PROCESSMODE.NULL.equals(processMode)) {
          // cron�ｿｽ�ｽｼ螢ｹ縺醍ｹ晢ｽｼ郢晢ｽｭ郢晢ｽｳ邵ｺ�ｽｫ郢ｧ蛹ｻ�ｽ矩明�ｽｪ陷肴�奇ｽｵ�ｽｷ陷搾ｿｽ
          // manual�ｿｽ�ｽｼ螢ｹ縺醍ｹ晢ｽｼ郢晢ｽｭ郢晢ｽｳ邵ｺ�ｽｫ郢ｧ蛹ｻ�ｽ矩明�ｽｪ陷肴�奇ｽｵ�ｽｷ陷搾ｿｽ
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
      // 3.郢ｧ�ｽｸ郢晢ｽｧ郢晞摩�ｿｽ�ｽｦ騾��ｿｽ驍ｨ繧��ｽｺ�ｿｽ
      // 3-1郢晁�後Ε郢昶悪諞ｾ隲ｷ荵昴Θ郢晢ｽｼ郢晄じﾎ晉ｸｺ�ｽｮ隴厄ｽｴ隴�ｽｰ隴鯉ｽ･隴弱ｅ�ｽ堤ｸｲ讙取ｨ溯舉�ｽｨ隴鯉ｽ･隴弱ｅ�ｿｽ髦ｪ縲定ｭ厄ｽｴ隴�ｽｰ邵ｺ蜷ｶ�ｽ�
      if (!batchStatus.getStatusinfo().isEmpty()) {
        batchController.updateBatchStatus(batchStatus, false, false, true, false);
      } else {
        batchController.updateBatchStatus(batchStatus, false, true, false, false);
      }
      // 3-2 郢ｧ�ｽｸ郢晢ｽｧ郢昜ｹ滂ｽｵ繧��ｽｺ�ｿｽ邵ｺ�ｽｮ霑･�ｽｶ雎補�夲ｽ堤ｹ晢ｽｭ郢ｧ�ｽｰ邵ｺ�ｽｫ陷�ｽｺ陷牙ｸ吮�郢ｧ�ｿｽ 陷��ｿｽ陞ｳ�ｽｹ�ｿｽ�ｽｼ螢ｹ縺慕ｹ晢ｽｼ郢晢ｿｽ(N02)邵ｲ竏ｬ�ｽｵ�ｽｷ陷崎ｼ釆皮ｹ晢ｽｼ郢晏ｳｨ�ｿｽ竏晢ｿｽ�ｽｦ騾��ｿｽ郢晢ｽ｢郢晢ｽｼ郢晏ｳｨ�ｿｽ�ｿｽ鬮｢蜿･�ｽｧ蛹ｺ蠕玖ｭ弱ｅ�ｿｽ竏ｫ�ｽｵ繧��ｽｺ�ｿｽ隴鯉ｽ･隴趣ｿｽ
	  LOGGER.info(String.format("%s >>> Start mode: %s >>> Process mode: %s >>> Sync process end at: %s", 
			  Constant.NORMALCODE.N02,	startMode, processMode, LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
      res.add("Sync process end at : " + LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId()));
      return new ResponseEntity<>(res, HttpStatus.OK);
    }
    return response;
  }

  // 2-1�ｿｽ�ｽｼ驕ｺ�ｿｽ�ｽｻ2-2邵ｺ�ｽｮ邵ｺ�ｽｿ陞ｳ貅ｯ�ｽ｡蠕後�定･搾ｽｷ陷崎ｼ費ｼ�邵ｺ貅ｷ�ｿｽ�ｽｴ陷ｷ蛹ｻ�ｿｽ�ｽｯ陷�ｽｦ騾��ｿｽ郢ｧ蛛ｵ縺帷ｹｧ�ｽｭ郢晢ｿｽ郢晏干笘�郢ｧ蜈ｷ�ｽｼ�ｿｽ
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

  // 2-2 陜難ｽｺ陝ｷ�ｽｹDB騾包ｽｨ郢晢ｿｽ郢晢ｽｼ郢ｧ�ｽｿ陷会ｿｽ陝ｾ�ｽ･邵ｺ�ｽｧ邵ｺ�ｽｮ隘搾ｽｷ陷榊桁�ｽｼ�ｿｽ2-2邵ｺ�ｽｮ邵ｺ�ｽｿ陞ｳ貅ｯ�ｽ｡魃会ｽｼ�ｿｽ
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

  // 雎｢蜉ｱ�ｼ櫁ｭ厄ｽｿ邵ｺ蝓滂ｽｩ貅ｯ�ｿｽ�ｽｽ邵ｺ�ｽｧ陞ｳ貅ｯ�ｽ｡�ｿｽ
  public void clearRawData() {
    accountMapper.truncateAccount();
    appCompanyMapper.truncateAppCompany();
    bankAccountInformationMapper.truncateBankAccountInformation();
    bankMasterMapper.truncateBankMaster();
    fareTableMapper.truncateFareTable();
    systemLinkInforMapper.truncatePaymentSystemLinkInfor();
  }

  // 4.陷茨ｽｱ鬨ｾ螢ｹ縺顔ｹ晢ｽｩ郢晢ｽｼ陷�ｽｦ騾��ｿｽ
  // 郢晁�後Ε郢昶悪諞ｾ隲ｷ荵昴Θ郢晢ｽｼ郢晄じﾎ晉ｸｺ�ｽｮ郢ｧ�ｽｹ郢晢ｿｽ郢晢ｽｼ郢ｧ�ｽｿ郢ｧ�ｽｹ郢ｧ蛛ｵ�ｿｽ諞ｩrror邵ｲ髦ｪ竊楢ｭ厄ｽｴ隴�ｽｰ邵ｺ蜷ｶ�ｽ�
  // 郢晁�後Ε郢昶悪諞ｾ隲ｷ荵昴Θ郢晢ｽｼ郢晄じﾎ晉ｸｺ�ｽｮ郢ｧ�ｽｹ郢晢ｿｽ郢晢ｽｼ郢ｧ�ｽｿ郢ｧ�ｽｹ隲��ｿｽ陜｣�ｽｱ邵ｺ�ｽｫ郢ｧ�ｽｨ郢晢ｽｩ郢晢ｽｼ陷��ｿｽ陞ｳ�ｽｹ郢ｧ蝣､蛹ｳ鬪ｭ�ｽｲ邵ｺ蜷ｶ�ｽ�
  // �ｿｽ�ｽｼ蛹ｻ縺帷ｹ晢ｿｽ郢晢ｽｼ郢ｧ�ｽｿ郢ｧ�ｽｹ隲��ｿｽ陜｣�ｽｱ邵ｺ蠕娯�邵ｺ�ｽｧ邵ｺ�ｽｫ陝�莨懈Β邵ｺ蜷ｶ�ｽ玖撻�ｽｴ陷ｷ蛹ｻ�ｿｽ�ｽｯ髴托ｽｽ髫ｪ蛟･笘�郢ｧ蜈ｷ�ｽｼ�ｿｽ
  public void commonError(String content, BatchStatus batchStatus, Exception ex) {
    // 陷��ｿｽ陞ｳ�ｽｹ�ｿｽ�ｽｼ螢ｹ縺慕ｹ晢ｽｼ郢晢ｿｽ(E03)邵ｲ竏壹♀郢晢ｽｩ郢晢ｽｼ隴鯉ｽ･隴弱ｅ�ｿｽ竏壹♀郢晢ｽｩ郢晢ｽｼ陷��ｿｽ陞ｳ�ｽｹ�ｿｽ�ｽｼ�ｿｽException陷��ｿｽ陞ｳ�ｽｹ�ｿｽ�ｽｼ�ｿｽ
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
