package jp.co.japantaxi.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jp.co.japantaxi.config.CacheManagerConfig;
import jp.co.japantaxi.mapper.stockholm.AccountMapper;
import jp.co.japantaxi.mapper.stockholm.FareTableMapper;
import jp.co.japantaxi.model.Account;
import jp.co.japantaxi.model.BatchStatus;
import jp.co.japantaxi.model.FareTable;
import jp.co.japantaxi.model.ParameterRequest;
import jp.co.japantaxi.model.Worker;
import jp.co.japantaxi.utils.Constant;
import jp.co.japantaxi.utils.ConvertDataUtil;
import jp.co.japantaxi.utils.DateTimeUtil;
import jp.co.japantaxi.utils.Utility;

@RestController
@RequestMapping
public class AccountController {

  static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

  @Autowired
  public CacheManagerConfig cacheManagerConfig;

  @Autowired
  public WorkerController workerController;

  @Autowired
  public SalesforceResponseController salesforceResponseController;

  @Autowired
  public BatchController batchController;

  @Autowired
  public AccountMapper accountMapper;

  @Autowired
  public FareTableMapper fareTableMapper;

  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [繝�繝ｼ繝悶Ν蜷搾ｼ医ョ繝ｼ繧ｿ蜉�蟾･蠕後�ｮDB逋ｻ骭ｲ譎ゅ↓繧ｨ繝ｩ繝ｼ縺ｫ縺ｪ縺｣縺溘ユ繝ｼ繝悶Ν蜷搾ｼ云
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void getSFAccount(ParameterRequest parameterRequest, BatchStatus batchStatus) {
	Map<String, Account> hashMap = new HashMap<>();
    try {
        // 竭�SalesfoceAPI繧偵さ繝ｼ繝ｫ縺励※荳願ｨ倥が繝悶ず繧ｧ繧ｯ繝域ュ蝣ｱ繧貞叙蠕励☆繧�
        List<Account> sfAccountList =
            salesforceResponseController.getListAccountFromSalesforce(batchStatus);
        // 竭｢API蜿門ｾ励↓謌仙粥縺励◆諠�蝣ｱ繧奪B縺ｫ逋ｻ骭ｲ縲∵峩譁ｰ縺吶ｋ
        if (!sfAccountList.isEmpty()) {
          List<String> sfAccIds = Utility.getIdListFromObjectList(sfAccountList);
          List<String> stAccIds = getListAccountIdFromStockholm(sfAccIds);
          //Add to HashMap
          for (Account obj : sfAccountList) {
          	hashMap.put(obj.getSfid(), obj);
          }
          
          List<Account> accountListToUpdate =
              getListAccountToUpdate(stAccIds, hashMap);
          List<Account> accountListToInsert = new ArrayList<Account>(hashMap.values());
          
          if (!accountListToInsert.isEmpty()) {
            insertAccount(accountListToInsert);
          }
          if (!accountListToUpdate.isEmpty()) {
            updateAccount(accountListToUpdate);
          }
        }
        String nptk = cacheManagerConfig.getNextPageToken("next_page_token");
        if (nptk != null) {
          getSFAccount(parameterRequest, batchStatus);
        }
        cacheManagerConfig.clearNextPageToken();
    } catch (Exception ex) {
      workerController.commonError(Constant.SF_REG + Constant.ACCOUNT, batchStatus, ex);
    }
  }
  
  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [繝�繝ｼ繝悶Ν蜷搾ｼ医ョ繝ｼ繧ｿ蜉�蟾･蠕後�ｮDB逋ｻ骭ｲ譎ゅ↓繧ｨ繝ｩ繝ｼ縺ｫ縺ｪ縺｣縺溘ユ繝ｼ繝悶Ν蜷搾ｼ云
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void coreDateCreatAccount(ParameterRequest parareq, BatchStatus batchStatus) {
	  List<Account> objectList = new ArrayList<>();
      List<String> objectIds = new ArrayList<>();
      List<Account> objects2Insert = new ArrayList<>();
      List<Account> objects2Update = new ArrayList<>();
      List<Account> compareList = new ArrayList<>();
      Map<String, Account> hashMap = new HashMap<>();
    try {
      //parareq.setStartTime(Utility.parseString(parareq.getStartTime()));
      parareq.setLimit(Constant.LIMIT);
      Integer count = accountMapper.countAccount(parareq);
      int size = count / Constant.LIMIT;
      
      for (int i = 0; i <= size; i++) {
        if (i < size) {
          parareq.setOffset(i*Constant.LIMIT);
          objectList = accountMapper.getListAccount(parareq);
          LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from {} to {}",
              Constant.ACCOUNT, Constant.ACCOUNTSYNC,
              Constant.LIMIT * i, Constant.LIMIT * (i + 1));
        } else if (i == size) {
          parareq.setOffset(size*Constant.LIMIT);
          objectList = accountMapper.getListAccount(parareq);
          LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from {} to {}",
              Constant.ACCOUNT, Constant.ACCOUNTSYNC,
              Constant.LIMIT * size, count); 
        }
        if(objectList.size() == 0) return;
        //Add to HashMap
        for (Account obj : objectList) {
        	hashMap.put(obj.getSfid(), obj);
        	objectIds.add(obj.getSfid());
        }
        //Request parameter
        //objectIds = Utility.getIdListFromObjectList(objectList);
        parareq.setIds(Utility.convertList(objectIds));
        parareq.setChecked(true);
        //Insert
        objects2Insert = selectAccountSyncList2InsertOrUpdate(hashMap, parareq, true);
        if (!objects2Insert.isEmpty()) {
          insertAccountSync(objects2Insert);
        }
        //Reduce SFID to query
        objectIds = new ArrayList<String>(hashMap.keySet());
        parareq.setIds(Utility.convertList(objectIds));
        parareq.setChecked(false);
        
        compareList = accountMapper.getListAccountSync(parareq);
        objects2Update = selectAccountSyncList2InsertOrUpdate(hashMap, parareq, false);
        objects2Update = getAccountSyncListEdited(objects2Update, compareList, hashMap);
        if (!objects2Update.isEmpty()) {
        	updateAccountSync(objects2Update);
        }
        parareq.setIds(null);
        hashMap.clear();
      }
    } catch (Exception ex) {
      workerController.commonError(Constant.BACK_REG + Constant.ACCOUNTSYNC, batchStatus, ex);
    }
  }

  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [繝�繝ｼ繝悶Ν蜷搾ｼ医ョ繝ｼ繧ｿ蜉�蟾･蠕後�ｮDB逋ｻ骭ｲ譎ゅ↓繧ｨ繝ｩ繝ｼ縺ｫ縺ｪ縺｣縺溘ユ繝ｼ繝悶Ν蜷搾ｼ云
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void getSFFareTable(ParameterRequest parameterRequest, BatchStatus batchStatus) {
	 Map<String, FareTable> hashMap = new HashMap<>();
    try {
        List<FareTable> sfFareTableList =
            salesforceResponseController.getListFareTableFromSalesforce(batchStatus);
        if (!sfFareTableList.isEmpty()) {
          List<String> sfFareTableIds = Utility.getIdListFromObjectList(sfFareTableList);
          List<String> stFareTableIds = getListFareTableIdFromStockholm(sfFareTableIds);
         //Add to HashMap
          for (FareTable obj : sfFareTableList) {
          	hashMap.put(obj.getSfid(), obj);
          }
          List<FareTable> faretableListToUpdate =
        		  getListFareTableToUpdate(stFareTableIds, hashMap);
          List<FareTable> fareTableListToInsert = new ArrayList<FareTable>(hashMap.values());
          
          if (!fareTableListToInsert.isEmpty()) {
            insertFareTable(fareTableListToInsert);
          }
          if (!faretableListToUpdate.isEmpty()) {
            updateFareTable(faretableListToUpdate);
          }
        }
        String nptk = cacheManagerConfig.getNextPageToken("next_page_token");
        if (nptk != null) {
          getSFFareTable(parameterRequest, batchStatus);
        }
        cacheManagerConfig.clearNextPageToken();
    } catch (Exception ex) {
      workerController.commonError(Constant.SF_REG + Constant.FARETABLE, batchStatus, ex);
    }
  }
    
  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [繝�繝ｼ繝悶Ν蜷搾ｼ医ョ繝ｼ繧ｿ蜉�蟾･蠕後�ｮDB逋ｻ骭ｲ譎ゅ↓繧ｨ繝ｩ繝ｼ縺ｫ縺ｪ縺｣縺溘ユ繝ｼ繝悶Ν蜷搾ｼ云
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void coreDateCreatFareTable(ParameterRequest parareq, BatchStatus batchStatus) {
      List<FareTable> objectList = new ArrayList<>();
      List<String> objectIds = new ArrayList<>();
      List<FareTable> objects2Insert = new ArrayList<>();
      List<FareTable> objects2Update = new ArrayList<>();
      List<FareTable> compareList = new ArrayList<>();
      Map<String, FareTable> hashMap = new HashMap<>();
    try {
      //parareq.setStartTime(Utility.parseString(parareq.getStartTime()));
      parareq.setLimit(Constant.LIMIT);
      Integer count = fareTableMapper.countFareTable(parareq);
      int size = count / Constant.LIMIT;
      for (int i = 0; i <= size; i++) {
        if (i < size) {
          parareq.setOffset(i*Constant.LIMIT);
          objectList = fareTableMapper.getListFareTable(parareq);
          LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from {} to {}",
              Constant.FARETABLE, Constant.FARETABLESYNC,
              Constant.LIMIT * i, Constant.LIMIT * (i + 1));
        } else if (i == size) {
          parareq.setOffset(size*Constant.LIMIT);
          objectList = fareTableMapper.getListFareTable(parareq);
          LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from {} to {}",
              Constant.FARETABLE, Constant.FARETABLESYNC,
              Constant.LIMIT * size, count);
        }
        if(objectList.size() == 0) return;
        //Add to HashMap
        for (FareTable fareT : objectList) {
        	hashMap.put(fareT.getSfid(), fareT);
        	objectIds.add(fareT.getSfid());
        }
        //Request parameter
        //objectIds = Utility.getIdListFromObjectList(objectList);
        parareq.setIds(Utility.convertList(objectIds));
        parareq.setChecked(true);
        //Insert
        objects2Insert = selectFareTableSyncList2InsertOrUpdate(hashMap, parareq, true);
        if (!objects2Insert.isEmpty()) {
          insertFareTableSync(objects2Insert);
        }
        //Reduce SFID to query
        objectIds = new ArrayList<String>(hashMap.keySet());
        parareq.setIds(Utility.convertList(objectIds));
        parareq.setChecked(false);
        
        compareList = fareTableMapper.getListFareTableSync(parareq);
        objects2Update = selectFareTableSyncList2InsertOrUpdate(hashMap, parareq, false);
        objects2Update = getFareTableSyncListEdited(objects2Update, compareList, hashMap);
        if (!objects2Update.isEmpty()) {
          updateFareTableSync(objects2Update);
        }
        parareq.setIds(null);
        hashMap.clear();
      }
    } catch (Exception ex) {
       workerController.commonError(Constant.BACK_REG + Constant.FARETABLESYNC, batchStatus, ex);
    }
  }

  // Begin FareTable
  /**
   * @param fareTableList
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void insertFareTable(List<FareTable> fareTableList) {
    for (int i = 0; i < fareTableList.size(); i++) {
      try {
        fareTableMapper.insertFareTable(fareTableList.get(i));
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error insert fareTable record with id: {} with error => {} ",
            fareTableList.get(i).getSfid(), e.getMessage());
        Collections.swap(fareTableList, i + 1, fareTableList.size() - 1);
      }
    }
  }

  /**
   * @param fareTableList
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void updateFareTable(List<FareTable> fareTableList) {
    for (int i = 0; i < fareTableList.size(); i++) {
      try {
        fareTableMapper.updateFareTable(fareTableList.get(i));
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error update fareTable record with id: {} with error => {} ",
            fareTableList.get(i).getSfid(), e.getMessage());
        Collections.swap(fareTableList, i + 1, fareTableList.size() - 1);
      }
    }
  }

  /**
   * @param fareTableList
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void insertFareTableSync(List<FareTable> fareTableList) {
    Worker worker = workerController.setWorker(Constant.APPCOMPANYSYNC);
    for (int i = 0; i < fareTableList.size(); i++) {
      try {
        fareTableMapper
            .insertFareTableSync(ConvertDataUtil.convertFareTable2Sync(fareTableList.get(i), true));
        worker.setSfid(fareTableList.get(i).getAppcompany().trim());
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error insert fareTablesync record with id: {} with error => {} ",
            fareTableList.get(i).getSfid(), e.getMessage());
        Collections.swap(fareTableList, i + 1, fareTableList.size() - 1);
      }
      workerController.insertWorker(worker);
    }
  }

  /**
   * @param fareTableList
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void updateFareTableSync(List<FareTable> fareTableList) {
    Worker worker = workerController.setWorker(Constant.APPCOMPANYSYNC);
    for (int i = 0; i < fareTableList.size(); i++) {
      try {
        fareTableMapper
            .updateFareTableSync(ConvertDataUtil.convertFareTable2Sync(fareTableList.get(i), true));
        LOGGER.info("FareTableSync updating >>> " + fareTableList.get(i).getSfid());
        worker.setSfid(fareTableList.get(i).getAppcompany());
        // Sync繝�繝ｼ繝励Ν縺ｫ譖ｴ譁ｰ蝣ｴ蜷茨ｼ壽価隱阪＆繧後◆繧ゅ�ｮ縺ｯ譛ｪ謇ｿ隱榊､画峩縲ゑｼ�Worker縺ｮ縲茎ycapproveflg縲阪↓縲卦RUE縲坂�偵�熊ALSE縲搾ｼ�
        worker.setSycapproveflg(false);
        workerController.updateWorker(worker);
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error update fareTablesync record with id: {} with error => {} ",
            fareTableList.get(i).getSfid(), e.getMessage());
        Collections.swap(fareTableList, i + 1, fareTableList.size() - 1);
      }
    }
  }

  public List<String> getListFareTableIdFromStockholm(List<String> objectIds) {
    ParameterRequest parareq = new ParameterRequest();
    parareq.setIds(Utility.parseList(objectIds));
    return fareTableMapper.getListFareTableIdFromStockholm(parareq);
  }

  public List<FareTable> getListFareTableToUpdate(List<String> stockholmIds, 
		  Map<String, FareTable> hashMap) {
    		List<FareTable> listObjToUpdate = new ArrayList<>();
    	    if (!stockholmIds.isEmpty()) {
    	      for (String sfid : stockholmIds) {
    	    	  listObjToUpdate.add(hashMap.get(sfid));
    	    	  hashMap.remove(sfid);//Remove update obj
    	      }
    	    }
    return listObjToUpdate;
  }
  
  public List<FareTable> getFareTableListFromIdList(List<String> ids,
      List<FareTable> objectList) {
    List<FareTable> list = new ArrayList<>();
    for (int i = 0; i < ids.size(); i++) {
      for (int j = 0; j < objectList.size(); j++) {
        if (ids.get(i).equalsIgnoreCase(objectList.get(j).getSfid())) {
          list.add(i, objectList.get(j));
        }
      }
    }
    return list;
  }
  
  public List<FareTable> selectFareTableSyncList2InsertOrUpdate(Map<String, FareTable> hashMap,
		  ParameterRequest parareq,  boolean rmFlg ) {
    List<String> listIdToInUp= new ArrayList<>();
	List<FareTable> listObjToInUp = new ArrayList<>();
    //Get List SFID to insert
	listIdToInUp = fareTableMapper.getListFareTableIds(parareq);
    if (!listIdToInUp.isEmpty()) {
      for (String sfid : listIdToInUp) {
    	  listObjToInUp.add(hashMap.get(sfid));
    	  if (!rmFlg) continue;
    	  hashMap.remove(sfid);//For Reduce memory
      }
    }
    return listObjToInUp;
  }
    
  public List<FareTable> getFareTableSyncListEdited(List<FareTable> objectList,
		  List<FareTable> compareList, Map<String, FareTable> hashMap) {
	    List<String> ids2Update =  Utility.compare(convertFareTableSyncList(objectList), compareList);
	    List<FareTable> listObject2Update = new ArrayList<>();
	    if (!ids2Update.isEmpty()) {
	      for (String sfid : ids2Update) {
	    	  listObject2Update.add(hashMap.get(sfid));
	    	  hashMap.remove(sfid);//For Reduce memory
	      }
	    }
	    return listObject2Update;
  }
  
  public static List<FareTable> convertFareTableSyncList(List<FareTable> objectList){
    List<FareTable> list = new ArrayList<>();
    FareTable element = new FareTable();
    for (int i = 0; i < objectList.size(); i++) {
      element = ConvertDataUtil.convertFareTable2Sync(objectList.get(i), false);
      list.add(element);
    }
    return list;
  }
  // End FareTable

  // Begin Account
  /**
   * @param accountList
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void insertAccount(List<Account> accountList) {
    for (int i = 0; i < accountList.size(); i++) {
      try {
        accountMapper.insertAccount(accountList.get(i));
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error insert account record with id: {} with error => {} ",
            accountList.get(i).getSfid(), e.getMessage());
        Collections.swap(accountList, i + 1, accountList.size() - 1);
      }
    }
  }

  /**
   * @param accountList
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void updateAccount(List<Account> accountList) {
    for (int i = 0; i < accountList.size(); i++) {
      try {
        accountMapper.updateAccount(accountList.get(i));
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error update account record with id: {} with error => {} ",
            accountList.get(i).getSfid(), e.getMessage());
        Collections.swap(accountList, i + 1, accountList.size() - 1);
      }
    }
  }

  /**
   * @param accountList
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void insertAccountSync(List<Account> accountList) {
    Worker worker = workerController.setWorker(Constant.APPCOMPANYSYNC);
    for (int i = 0; i < accountList.size(); i++) {
      try {
        accountMapper.insertAccountSync(ConvertDataUtil.convertAccount2Sync(accountList.get(i), true));
        worker.setSfid(accountList.get(i).getAppcompany().trim());
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error insert accountsync record with id: {} with error => {} ",
            accountList.get(i).getSfid(), e.getMessage());
        Collections.swap(accountList, i + 1, accountList.size() - 1);   
      }
      workerController.insertWorker(worker);
    }
  }

  /**
   * @param accountList
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void updateAccountSync(List<Account> accountList) {
    Worker worker = workerController.setWorker(Constant.APPCOMPANYSYNC);
    for (int i = 0; i < accountList.size(); i++) {
      try {
        accountMapper.updateAccountSync(ConvertDataUtil.convertAccount2Sync(accountList.get(i), true));
        LOGGER.info("AccountSync updating >>> " + accountList.get(i).getSfid());
        worker.setSfid(accountList.get(i).getAppcompany());
        // Sync繝�繝ｼ繝励Ν縺ｫ譖ｴ譁ｰ蝣ｴ蜷茨ｼ壽価隱阪＆繧後◆繧ゅ�ｮ縺ｯ譛ｪ謇ｿ隱榊､画峩縲ゑｼ�Worker縺ｮ縲茎ycapproveflg縲阪↓縲卦RUE縲坂�偵�熊ALSE縲搾ｼ�
        worker.setSycapproveflg(false);
        workerController.updateWorker(worker);
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error update accountsync record with id: {} with error => {} ",
            accountList.get(i).getSfid(), e.getMessage());
        Collections.swap(accountList, i + 1, accountList.size() - 1);
      }
    }
  }

  public List<String> getListAccountIdFromStockholm(List<String> objectIds) {
    ParameterRequest parareq = new ParameterRequest();
    parareq.setIds(Utility.parseList(objectIds));
    return accountMapper.getListAccountIdFromStockholm(parareq);
  }

  public List<Account> getListAccountToUpdate(List<String> stockholmIds,
		  Map<String, Account> hashMap) {
	List<Account> listObjToUpdate = new ArrayList<>();
    if (!stockholmIds.isEmpty()) {
      for (String sfid : stockholmIds) {
    	  listObjToUpdate.add(hashMap.get(sfid));
    	  hashMap.remove(sfid);//Remove update obj
      }
    }
    return listObjToUpdate;
  }
  
  public List<Account> getAccountListFromIdList(List<String> ids,
      List<Account> objectList) {
    List<Account> list = new ArrayList<>();
    for (int i = 0; i < ids.size(); i++) {
      for (int j = 0; j < objectList.size(); j++) {
        if (ids.get(i).equalsIgnoreCase(objectList.get(j).getSfid())) {
          list.add(i, objectList.get(j));
        }
      }
    }
    return list;
  }
  
  /**
   * 
   * @param objectList
   * @param parareq
   * @return
   */
  public List<Account> selectAccountSyncList2InsertOrUpdate(Map<String, Account> hashMap,
		  ParameterRequest parareq, boolean rmFlg ) {
	List<String> listIdToInUp= new ArrayList<>();
	List<Account> listAccToInUp = new ArrayList<>();
    //Get List SFID to insert
	listIdToInUp = accountMapper.getListAccountIds(parareq);
    if (!listIdToInUp.isEmpty()) {
      for (String sfid : listIdToInUp) {
    	  listAccToInUp.add(hashMap.get(sfid));
    	  if (!rmFlg) continue;
    	  hashMap.remove(sfid);//For Reduce memory
      }
    }
    return listAccToInUp;
  }
    
  public List<Account> getAccountSyncListEdited( List<Account> objectList,
		  List<Account> compareList, Map<String, Account> hashMap) {
    List<String> ids2Update =  Utility.compare(convertAccountSyncList(objectList), compareList);
    List<Account> listObject2Update = new ArrayList<>();
    if (!ids2Update.isEmpty()) {
      for (String sfid : ids2Update) {
    	  listObject2Update.add(hashMap.get(sfid));
    	  hashMap.remove(sfid);//For Reduce memory
      }
    }
    return listObject2Update;
  }

  public static List<Account> convertAccountSyncList(List<Account> objectList){
    List<Account> list = new ArrayList<>();
    Account element = new Account();
    for (int i = 0; i < objectList.size(); i++) {
      element = ConvertDataUtil.convertAccount2Sync(objectList.get(i), false);
      element.setInvaliddate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(objectList.get(i).getInvaliddate(), DateTimeUtil.DATE_FM_S),
          DateTimeUtil.DATE_FM_S));
      element.setMerchantoperinforcontractenddate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(objectList.get(i).getMerchantoperinforcontractenddate(), DateTimeUtil.DATE_FM_S),
          DateTimeUtil.DATE_FM_S));
      element.setMerchantoperinforguaranteeenddate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(objectList.get(i).getMerchantoperinforguaranteeenddate(), DateTimeUtil.DATE_FM_S),
          DateTimeUtil.DATE_FM_S));
      element.setMerchantoperinforacceptancedate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(objectList.get(i).getMerchantoperinforacceptancedate(), DateTimeUtil.DATE_FM_S),
          DateTimeUtil.DATE_FM_S));
      element.setMerchantoperinforstartdate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(objectList.get(i).getMerchantoperinforstartdate(), DateTimeUtil.DATE_FM_S),
          DateTimeUtil.DATE_FM_S));
      element.setMerchantreprinforbirthday(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(objectList.get(i).getMerchantreprinforbirthday(), DateTimeUtil.DATE_FM_S),
          DateTimeUtil.DATE_FM_S));
      element.setMerchantcontrinforlicenseacquisitiondate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(objectList.get(i).getMerchantcontrinforlicenseacquisitiondate(), DateTimeUtil.DATE_FM_S),
          DateTimeUtil.DATE_FM_S));
      list.add(element);
    }
    return list;
  }
  // End Account
}
