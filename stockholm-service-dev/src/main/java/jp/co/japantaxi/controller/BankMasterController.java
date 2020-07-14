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
import jp.co.japantaxi.mapper.stockholm.BankAccountInformationMapper;
import jp.co.japantaxi.mapper.stockholm.BankMasterMapper;
import jp.co.japantaxi.model.BankAccountInformation;
import jp.co.japantaxi.model.BankMaster;
import jp.co.japantaxi.model.BatchStatus;
import jp.co.japantaxi.model.ParameterRequest;
import jp.co.japantaxi.model.Worker;
import jp.co.japantaxi.utils.Constant;
import jp.co.japantaxi.utils.ConvertDataUtil;
import jp.co.japantaxi.utils.DateTimeUtil;
import jp.co.japantaxi.utils.JsonMapper;
import jp.co.japantaxi.utils.Utility;

@RestController
@RequestMapping("/bankmaster")
public class BankMasterController {

  static final Logger LOGGER = LoggerFactory.getLogger(BankMasterController.class);

  @Autowired
  public CacheManagerConfig cacheManagerConfig;

  @Autowired
  public WorkerController workerController;

  @Autowired
  public SalesforceResponseController salesforceResponseController;

  @Autowired
  public BatchController batchController;

  @Autowired
  public BankMasterMapper bankMasterMapper;

  @Autowired
  public BankAccountInformationMapper bankAccountInforMapper;

  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [繝�繝ｼ繝悶Ν蜷搾ｼ医ョ繝ｼ繧ｿ蜉�蟾･蠕後�ｮDB逋ｻ骭ｲ譎ゅ↓繧ｨ繝ｩ繝ｼ縺ｫ縺ｪ縺｣縺溘ユ繝ｼ繝悶Ν蜷搾ｼ云
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void getSFBankMaster(ParameterRequest parameterRequest, BatchStatus batchStatus) {
	Map<String, BankMaster> hashMap = new HashMap<>();
    try {
        List<BankMaster> sfBankMasterList =
            salesforceResponseController.getListBankMasterFromSalesforce(batchStatus);
        if (!sfBankMasterList.isEmpty()) {
          List<String> sfBankMasterIds = Utility.getIdListFromObjectList(sfBankMasterList);
          List<String> stBankMasterIds = getListBankMasterIdFromStockholm(sfBankMasterIds);
          //Add to HashMap
          for (BankMaster obj : sfBankMasterList) {
          	hashMap.put(obj.getSfid(), obj);
          }
          
          List<BankMaster> bankMasterListToUpdate =
        		  getListBankMasterToUpdate(stBankMasterIds, hashMap);
          List<BankMaster> bankMasterListToInsert = new ArrayList<BankMaster>(hashMap.values());
          
          if (!bankMasterListToInsert.isEmpty()) {
            insertBankMaster(bankMasterListToInsert);
          }
          if (!bankMasterListToUpdate.isEmpty()) {
            updateBankMaster(bankMasterListToUpdate);
          }
        }
        String nptk = cacheManagerConfig.getNextPageToken("next_page_token");
        if (nptk != null) {
          getSFBankMaster(parameterRequest, batchStatus);
        }
        cacheManagerConfig.clearNextPageToken();
    } catch (Exception ex) {
      workerController.commonError(Constant.SF_REG + Constant.BANKMASTER, batchStatus, ex);
    }
  }
  
  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [繝�繝ｼ繝悶Ν蜷搾ｼ医ョ繝ｼ繧ｿ蜉�蟾･蠕後�ｮDB逋ｻ骭ｲ譎ゅ↓繧ｨ繝ｩ繝ｼ縺ｫ縺ｪ縺｣縺溘ユ繝ｼ繝悶Ν蜷搾ｼ云
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void coreDateCreatBankMaster(ParameterRequest parareq, BatchStatus batchStatus) {
	  List<String> objectIds = new ArrayList<>();
      List<BankMaster> objects2Insert = new ArrayList<>();
      List<BankMaster> objects2Update = new ArrayList<>();
      List<BankMaster> compareList = new ArrayList<>();
      Map<String, BankMaster> hashMap = new HashMap<>();
      
    try {
      //parareq.setStartTime(Utility.parseString(parareq.getStartTime()));
      parareq.setLimit(Constant.LIMIT);
      Integer count = bankMasterMapper.countBankMaster(parareq);
      int offset = count / Constant.LIMIT;
      List<BankMaster> objectList = new ArrayList<>();
      for (int i = 0; i <= offset; i++) {
        parareq.setOffset(i*Constant.LIMIT);
        objectList = bankMasterMapper.getListBankMaster(parareq);
        if (i < offset) {
          LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from {} to {}",
              Constant.BANKMASTER, Constant.BANKMASTERSYNC,
              Constant.LIMIT * i, Constant.LIMIT * (i + 1));
        } else if (i == offset) {
          LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to {}",
                  Constant.BANKMASTER, Constant.BANKMASTERSYNC,
                  Constant.LIMIT * offset, count);
        }
        if(objectList.size() == 0) return;
        //Add to HashMap
        for (BankMaster obj : objectList) {
        	hashMap.put(obj.getSfid(), obj);
        	objectIds.add(obj.getSfid());
        }
        //Request parameter
        //objectIds = Utility.getIdListFromObjectList(objectList);
        parareq.setIds(Utility.convertList(objectIds));
        parareq.setChecked(true);
        //Insert
        objects2Insert = selectBankMasterSyncList2InsertOrUpdate(hashMap, parareq, true);
        if (!objects2Insert.isEmpty()) {
        	insertBankMasterSync(objects2Insert);
        	insertWorker(objects2Insert, parareq);
        }
        //Reduce SFID to query
        objectIds = new ArrayList<String>(hashMap.keySet());
        parareq.setIds(Utility.convertList(objectIds));
        parareq.setChecked(false);
        
        compareList =  bankMasterMapper.getListBankMasterSync(parareq);
        objects2Update = selectBankMasterSyncList2InsertOrUpdate(hashMap, parareq, false);
        objects2Update = getBankMasterSyncListEdited(objects2Update, compareList, hashMap);
        if (!objects2Update.isEmpty()) {
        	updateBankMasterSync(objects2Update);
        	updateWorker(objects2Update, parareq);
        }
        parareq.setIds(null);
        hashMap.clear();
      }
    } catch (Exception ex) {
      workerController.commonError(Constant.BACK_REG + Constant.BANKMASTERSYNC, batchStatus, ex);
    }
  }

  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [繝�繝ｼ繝悶Ν蜷搾ｼ医ョ繝ｼ繧ｿ蜉�蟾･蠕後�ｮDB逋ｻ骭ｲ譎ゅ↓繧ｨ繝ｩ繝ｼ縺ｫ縺ｪ縺｣縺溘ユ繝ｼ繝悶Ν蜷搾ｼ云
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void getSFBankAccountInformation(ParameterRequest parameterRequest,
      BatchStatus batchStatus) {
	Map<String, BankAccountInformation> hashMap = new HashMap<>();
    try {
        List<BankAccountInformation> sfBankAccountInformationList =
            salesforceResponseController.getListBankAccountInformationFromSalesforce(batchStatus);
        if (!sfBankAccountInformationList.isEmpty()) {
          List<String> sfBankAccountInformationIds = Utility.getIdListFromObjectList(sfBankAccountInformationList);
          List<String> stBankAccountInformationIds =
              getListBankAccountInformationIdFromStockholm(sfBankAccountInformationIds);
          //Add to HashMap
          for (BankAccountInformation obj : sfBankAccountInformationList) {
          	hashMap.put(obj.getSfid(), obj);
          }
          
          List<BankAccountInformation> bankAccountInformationListToUpdate =
        		  getListBankAccountInformationToUpdate(stBankAccountInformationIds, hashMap);
          List<BankAccountInformation> bankAccountInformationListToInsert = new ArrayList<BankAccountInformation>(hashMap.values());
          
          if (!bankAccountInformationListToInsert.isEmpty()) {
            insertBankAccountInformation(bankAccountInformationListToInsert);
          }
          if (!bankAccountInformationListToUpdate.isEmpty()) {
            updateBankAccountInformation(bankAccountInformationListToUpdate);
          }
        }
        String nptk = cacheManagerConfig.getNextPageToken("next_page_token");
        if (nptk != null) {
          getSFBankAccountInformation(parameterRequest, batchStatus);
        }
        cacheManagerConfig.clearNextPageToken();
    } catch (Exception ex) {
      workerController.commonError(Constant.SF_REG + Constant.BANKACCOUNTINFORMATION, batchStatus,
          ex);
    }
  }
  
  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [繝�繝ｼ繝悶Ν蜷搾ｼ医ョ繝ｼ繧ｿ蜉�蟾･蠕後�ｮDB逋ｻ骭ｲ譎ゅ↓繧ｨ繝ｩ繝ｼ縺ｫ縺ｪ縺｣縺溘ユ繝ｼ繝悶Ν蜷搾ｼ云
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void coreDateCreatBankAccountInformation(ParameterRequest parareq, BatchStatus batchStatus) {
	  List<String> objectIds = new ArrayList<>();
      List<BankAccountInformation> objects2Insert = new ArrayList<>();
      List<BankAccountInformation> objects2Update = new ArrayList<>();
      List<BankAccountInformation> compareList = new ArrayList<>();
      Map<String, BankAccountInformation> hashMap = new HashMap<>();
    try {
      // Read the datasync file once only
      JsonMapper.readDataSync(Constant.BANKACCOUNTINFORMATION);
      parareq.setLimit(Constant.LIMIT);
      Integer count = bankAccountInforMapper.countBankAccountInformation(parareq);
      int offset = count / Constant.LIMIT;
      List<BankAccountInformation> objectList = new ArrayList<>();
      for (int i = 0; i <= offset; i++) {
        parareq.setOffset(i*Constant.LIMIT);
        objectList = bankAccountInforMapper.getListBankAccountInformation(parareq);
        if (i < offset) {
          LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from {} to {}",
              Constant.BANKACCOUNTINFORMATION, Constant.BANKACCOUNTINFORMATIONSYNC,
              Constant.LIMIT * i, Constant.LIMIT * (i + 1));
        } else if (i == offset) {
          LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to {}",
                  Constant.BANKACCOUNTINFORMATION, Constant.BANKACCOUNTINFORMATIONSYNC,
                  Constant.LIMIT * offset, count);
        }
        if(objectList.size() == 0) return;
        //Add to HashMap
        for (BankAccountInformation obj : objectList) {
        	hashMap.put(obj.getSfid(), obj);
        	objectIds.add(obj.getSfid());
        }
        //Request parameter
        //objectIds = Utility.getIdListFromObjectList(objectList);
        parareq.setIds(Utility.convertList(objectIds));
        parareq.setChecked(true);
        //Insert
        objects2Insert = selectBankAccInfoSyncList2InsertOrUpdate(hashMap, parareq, true);
        if (!objects2Insert.isEmpty()) {
        	insertBankAccountInformationSync(objects2Insert);
        }
        //Reduce SFID to query
        objectIds = new ArrayList<String>(hashMap.keySet());
        parareq.setIds(Utility.convertList(objectIds));
        parareq.setChecked(false);
        
        compareList = bankAccountInforMapper.getListBankAccountInformationSync(parareq);
        objects2Update = selectBankAccInfoSyncList2InsertOrUpdate(hashMap, parareq, false);
        objects2Update = getBankAccInfoSyncListEdited(objects2Update, compareList, hashMap);
        if (!objects2Update.isEmpty()) {
        	updateBankAccountInformationSync(objects2Update);
        }
        parareq.setIds(null);
        hashMap.clear();
      }
      } catch (Exception ex) {
        workerController.commonError(Constant.BACK_REG + Constant.BANKACCOUNTINFORMATIONSYNC,
            batchStatus, ex);
      }
  }

  // Begin BankMaster
  /**
   * @param bankMasterList
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void insertBankMaster(List<BankMaster> bankMasterList) {
    for (int i = 0; i < bankMasterList.size(); i++) {
      try {
        bankMasterMapper.insertBankMaster(bankMasterList.get(i));
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error insert bankmaster record with id: {} with error => {} ",
            bankMasterList.get(i).getSfid(), e.getMessage());
        Collections.swap(bankMasterList, i + 1, bankMasterList.size() - 1);
      }
    }
  }

  /**
   * @param bankMasterList
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void updateBankMaster(List<BankMaster> bankMasterList) {
    for (int i = 0; i < bankMasterList.size(); i++) {
      try {
        bankMasterMapper.updateBankMaster(bankMasterList.get(i));
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error update bankmaster record with id: {} with error => {} ",
            bankMasterList.get(i).getSfid(), e.getMessage());
        Collections.swap(bankMasterList, i + 1, bankMasterList.size() - 1);
      }
    }
  }

  /**
   * @param bankMasterList
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void insertBankMasterSync(List<BankMaster> bankMasterList) {
    for (int i = 0; i < bankMasterList.size(); i++) {
      try {
        bankMasterMapper.insertBankMasterSync(ConvertDataUtil.convertBankMaster2Sync(bankMasterList.get(i), true));
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error insert bankmastersync record with id: {} with error => {} ",
            bankMasterList.get(i).getSfid(), e.getMessage());
        Collections.swap(bankMasterList, i + 1, bankMasterList.size() - 1);
      }
    }
  }

  public void insertWorker(List<BankMaster> bankMasterList, ParameterRequest request) {
	List<BankAccountInformation> accountInformations = new ArrayList<BankAccountInformation>();
	Worker worker = workerController.setWorker(Constant.APPCOMPANYSYNC);
	List<String> ids = Utility.getIdListFromObjectList(bankMasterList);
	request.setIds(Utility.parseList(ids));
	accountInformations = bankAccountInforMapper.getBankAccountByBankIds(request);
	for (int j = 0; j < accountInformations.size(); j++) {
		worker.setSfid(accountInformations.get(j).getAppcompanyid().trim());
		workerController.insertWorker(worker);
	}
  }
  
  /**
   * @param bankMasterList
   * try catch: Sentry 騾｣謳ｺ縺励お繝ｩ繝ｼ騾夂衍繧定｡後≧
   */
  public void updateBankMasterSync(List<BankMaster> bankMasterList) {
	for (int i = 0; i < bankMasterList.size(); i++) {
		try {
			bankMasterMapper.updateBankMasterSync(ConvertDataUtil.convertBankMaster2Sync(bankMasterList.get(i), true));
			LOGGER.info("BankMasterSync updating >>> " + bankMasterList.get(i).getSfid());
		} catch (Exception e) {
			LOGGER.error(Constant.NORMALCODE.E03 + " >>> error update bankmastersync record with id: {} with error => {} ",
					bankMasterList.get(i).getSfid(), e.getMessage());
			Collections.swap(bankMasterList, i + 1, bankMasterList.size() - 1);
		}
	}
  }

  public List<String> getListBankMasterIdFromStockholm(List<String> objectIds) {
    ParameterRequest parareq = new ParameterRequest();
    parareq.setIds(Utility.parseList(objectIds));
    return bankMasterMapper.getListBankMasterIdFromStockholm(parareq);
  }
  
  public List<BankMaster> getListBankMasterToUpdate(List<String> stockholmIds,
		  Map<String, BankMaster> hashMap) {
	List<BankMaster> listObjToUpdate = new ArrayList<>();
    if (!stockholmIds.isEmpty()) {
      for (String sfid : stockholmIds) {
    	  listObjToUpdate.add(hashMap.get(sfid));
    	  hashMap.remove(sfid);//Remove update obj
      }
    }
    return listObjToUpdate;
  }
  
  public List<BankMaster> getBankMasterListFromIdList(List<String> ids,
      List<BankMaster> objectList) {
    List<BankMaster> list = new ArrayList<>();
    for (int i = 0; i < ids.size(); i++) {
      for (int j = 0; j < objectList.size(); j++) {
        if (ids.get(i).equalsIgnoreCase(objectList.get(j).getSfid())) {
          list.add(i, objectList.get(j));
        }
      }
    }
    return list;
  }

  public List<BankMaster> selectBankMasterSyncList2InsertOrUpdate(Map<String, BankMaster> hashMap,
		  ParameterRequest parareq, boolean rmFlg ) {
	List<String> listIdToInUp= new ArrayList<>();
	List<BankMaster> listAccToInUp = new ArrayList<>();
    //Get List SFID to insert
	listIdToInUp = bankMasterMapper.getListBankMasterIds(parareq);
    if (!listIdToInUp.isEmpty()) {
      for (String sfid : listIdToInUp) {
    	  listAccToInUp.add(hashMap.get(sfid));
    	  if (!rmFlg) continue;
    	  hashMap.remove(sfid);//For Reduce memory
      }
    }
    return listAccToInUp;
  }
    
  public List<BankMaster> getBankMasterSyncListEdited( List<BankMaster> objectList,
		  List<BankMaster> compareList, Map<String, BankMaster> hashMap) {
    List<String> ids2Update =  Utility.compare(convertBankMasterSyncList(objectList), compareList);
    List<BankMaster> listObject2Update = new ArrayList<>();
    if (!ids2Update.isEmpty()) {
      for (String sfid : ids2Update) {
    	  listObject2Update.add(hashMap.get(sfid));
    	  hashMap.remove(sfid);//For Reduce memory
      }
    }
    return listObject2Update;
  }
  
  public static List<BankMaster> convertBankMasterSyncList(List<BankMaster> objectList){
    List<BankMaster> list = new ArrayList<>();
    BankMaster element = new BankMaster();
    for (int i = 0; i < objectList.size(); i++) {
      element = ConvertDataUtil.convertBankMaster2Sync(objectList.get(i), false);
      list.add(element);
    }
    return list;
  }
  // End BankMaster

  // Begin BankAccountInformation
  public void insertBankAccountInformation(
      List<BankAccountInformation> bankAccountInformationList) {
    for (int i = 0; i < bankAccountInformationList.size(); i++) {
      try {
        bankAccountInforMapper.insertBankAccountInformation(bankAccountInformationList.get(i));
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error insert bankaccountinformation record with id: {} with error => {} ",
            bankAccountInformationList.get(i).getSfid(), e.getMessage());
        Collections.swap(bankAccountInformationList, i + 1, bankAccountInformationList.size() - 1);
      }
    }
  }

  public void updateBankAccountInformation(
      List<BankAccountInformation> bankAccountInformationList) {
    for (int i = 0; i < bankAccountInformationList.size(); i++) {
      try {
        bankAccountInforMapper.updateBankAccountInformation(bankAccountInformationList.get(i));
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error update bankaccountinformation record with id: {} with error => {} ",
            bankAccountInformationList.get(i).getSfid(), e.getMessage());
        Collections.swap(bankAccountInformationList, i + 1, bankAccountInformationList.size() - 1);
      }
    }
  }

  public void updateWorker(List<BankMaster> bankMasterList, ParameterRequest request) {
	List<BankAccountInformation> accountInformations = new ArrayList<BankAccountInformation>();
	Worker worker = workerController.setWorker(Constant.APPCOMPANYSYNC);
    List<String> ids = Utility.getIdListFromObjectList(bankMasterList);
    request.setIds(Utility.parseList(ids));
    accountInformations = bankAccountInforMapper.getBankAccountByBankIds(request);
	for (int j = 0; j < accountInformations.size(); j++) {
		worker.setSfid(accountInformations.get(j).getAppcompanyid());
		// Sync繝�繝ｼ繝励Ν縺ｫ譖ｴ譁ｰ蝣ｴ蜷茨ｼ壽価隱阪＆繧後◆繧ゅ�ｮ縺ｯ譛ｪ謇ｿ隱榊､画峩縲ゑｼ�Worker縺ｮ縲茎yncapproveflg 縲阪↓縲卦RUE縲坂�偵�熊ALSE縲搾ｼ�
		worker.setSycapproveflg(false);
		workerController.updateWorker(worker);
	}
  }
  
  public void insertBankAccountInformationSync(
      List<BankAccountInformation> bankAccountInformationList) {
    Worker worker = workerController.setWorker(Constant.APPCOMPANYSYNC);
    for (int i = 0; i < bankAccountInformationList.size(); i++) {
      try {
        bankAccountInforMapper.insertBankAccountInformationSync(
            ConvertDataUtil.convertBankAccountInformation2Sync(bankAccountInformationList.get(i), true));
        worker.setSfid(bankAccountInformationList.get(i).getAppcompanyid().trim());
        workerController.insertWorker(worker);
      } catch (Exception e) {
        LOGGER.error(Constant.NORMALCODE.E03
            + " >>> error insert bankaccountinformationsync record with id: {} with error => {} ",
            bankAccountInformationList.get(i).getSfid(), e.getMessage());
        Collections.swap(bankAccountInformationList, i + 1, bankAccountInformationList.size() - 1);
      }
    }
  }

  public void updateBankAccountInformationSync(
      List<BankAccountInformation> bankAccountInformationList) {
    Worker worker = workerController.setWorker(Constant.APPCOMPANYSYNC);
    for (int i = 0; i < bankAccountInformationList.size(); i++) {
      try {
        bankAccountInforMapper.updateBankAccountInformationSync(
            ConvertDataUtil.convertBankAccountInformation2Sync(bankAccountInformationList.get(i), true));
        LOGGER.info("BankAccountInformationSync updating >>> " + bankAccountInformationList.get(i).getSfid());
        worker.setSfid(bankAccountInformationList.get(i).getAppcompanyid());
        // Sync繝�繝ｼ繝励Ν縺ｫ譖ｴ譁ｰ蝣ｴ蜷茨ｼ壽価隱阪＆繧後◆繧ゅ�ｮ縺ｯ譛ｪ謇ｿ隱榊､画峩縲ゑｼ�Worker縺ｮ縲茎yncapproveflg縲阪↓縲卦RUE縲坂�偵�熊ALSE縲搾ｼ�
        worker.setSycapproveflg(false);
        workerController.updateWorker(worker);
      } catch (Exception e) {
        LOGGER.error(Constant.NORMALCODE.E03
            + " >>> error update bankaccountinformationsync record with id: {} with error => {} ",
            bankAccountInformationList.get(i).getSfid(), e.getMessage());
        Collections.swap(bankAccountInformationList, i + 1, bankAccountInformationList.size() - 1);
      }
    }
  }

  public List<String> getListBankAccountInformationIdFromStockholm(List<String> objectIds) {
    ParameterRequest parareq = new ParameterRequest();
    parareq.setIds(Utility.parseList(objectIds));
    return bankAccountInforMapper.getListBankAccountInformationIdFromStockholm(parareq);
  }

  public List<BankAccountInformation> getListBankAccountInformationToUpdate(List<String> stockholmIds,
		  Map<String, BankAccountInformation> hashMap) {
	List<BankAccountInformation> listObjToUpdate = new ArrayList<>();
    if (!stockholmIds.isEmpty()) {
      for (String sfid : stockholmIds) {
    	  listObjToUpdate.add(hashMap.get(sfid));
    	  hashMap.remove(sfid);//Remove update obj
      }
    }
    return listObjToUpdate;
  }
   
  public List<BankAccountInformation> getBankAccInfoListFromIdList(List<String> ids,
      List<BankAccountInformation> objectList) {
    List<BankAccountInformation> list = new ArrayList<>();
    for (int i = 0; i < ids.size(); i++) {
      for (int j = 0; j < objectList.size(); j++) {
        if (ids.get(i).equalsIgnoreCase(objectList.get(j).getSfid())) {
          list.add(i, objectList.get(j));
        }
      }
    }
    return list;
  }
  
  public List<BankAccountInformation> selectBankAccInfoSyncList2InsertOrUpdate(Map<String, BankAccountInformation> hashMap,
		  ParameterRequest parareq,  boolean rmFlg ) {
	  List<String> listIdToInUp= new ArrayList<>();
		List<BankAccountInformation> listObjToInUp = new ArrayList<>();
	    //Get List SFID to insert
		listIdToInUp = bankAccountInforMapper.getListBankAccountInformationIds(parareq);
	    if (!listIdToInUp.isEmpty()) {
	      for (String sfid : listIdToInUp) {
	    	  listObjToInUp.add(hashMap.get(sfid));
	    	  if (!rmFlg) continue;
	    	  hashMap.remove(sfid);//For Reduce memory
	      }
	    }
	    return listObjToInUp;
  }
    
  public List<BankAccountInformation> getBankAccInfoSyncListEdited(List<BankAccountInformation> objectList,
		  List<BankAccountInformation> compareList, Map<String, BankAccountInformation> hashMap) {
	    List<String> ids2Update =  Utility.compare(convertBankAccInfoSyncList(objectList), compareList);
	    List<BankAccountInformation> listObject2Update = new ArrayList<>();
	    if (!ids2Update.isEmpty()) {
	      for (String sfid : ids2Update) {
	    	  listObject2Update.add(hashMap.get(sfid));
	    	  hashMap.remove(sfid);//For Reduce memory
	      }
	    }
	    return listObject2Update;
  }
  
  public static List<BankAccountInformation> convertBankAccInfoSyncList(List<BankAccountInformation> objectList){
    List<BankAccountInformation> list = new ArrayList<>();
    BankAccountInformation element = new BankAccountInformation();
    for (int i = 0; i < objectList.size(); i++) {
      element = ConvertDataUtil.convertBankAccountInformation2Sync(objectList.get(i), false);
      element.setInvaliddate(DateTimeUtil.getDateFromString(DateTimeUtil.getStringFromDate(objectList.get(i).getInvaliddate(), DateTimeUtil.DD_FM_S), DateTimeUtil.DD_FM_S));
      list.add(element);
    }
    return list;
  }
  // End BankAccountInformation
}
