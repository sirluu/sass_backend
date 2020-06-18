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
import jp.co.japantaxi.mapper.stockholm.AppCompanyMapper;
import jp.co.japantaxi.model.AppCompany;
import jp.co.japantaxi.model.BatchStatus;
import jp.co.japantaxi.model.ParameterRequest;
import jp.co.japantaxi.model.Worker;
import jp.co.japantaxi.utils.Constant;
import jp.co.japantaxi.utils.ConvertDataUtil;
import jp.co.japantaxi.utils.DateTimeUtil;
import jp.co.japantaxi.utils.JsonMapper;
import jp.co.japantaxi.utils.Utility;

@RestController
@RequestMapping
public class AppCompanyController {

  static final Logger LOGGER = LoggerFactory.getLogger(AppCompanyController.class);

  @Autowired
  public CacheManagerConfig cacheManagerConfig;

  @Autowired
  public WorkerController workerController;

  @Autowired
  public SalesforceResponseController salesforceResponseController;

  @Autowired
  public BatchController batchController;

  @Autowired
  public AppCompanyMapper appCompanyMapper;

  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [テーブル名（データ加工後のDB登録時にエラーになったテーブル名）]
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void getSFAppCompany(ParameterRequest parameterRequest, BatchStatus batchStatus) {
	Map<String, AppCompany> hashMap = new HashMap<>();
    try {
        List<AppCompany> sfAppCompanyList =
            salesforceResponseController.getListAppCompanyFromSalesforce(batchStatus);
        if (!sfAppCompanyList.isEmpty()) {
          List<String> sfAppCompanyIds = Utility.getIdListFromObjectList(sfAppCompanyList);
          List<String> stAppCompanyIds = getListAppCompanyIdFromStockholm(sfAppCompanyIds);
        //Add to HashMap
          for (AppCompany obj : sfAppCompanyList) {
          	hashMap.put(obj.getSfid(), obj);
          }
          
          List<AppCompany> appCompanysToUpdate =
        		  getListAppCompanyToUpdate(stAppCompanyIds, hashMap);
          List<AppCompany> appCompanysToInsert = new ArrayList<AppCompany>(hashMap.values());
          
          if (!appCompanysToInsert.isEmpty()) {
            insertAppCompany(appCompanysToInsert);
          }
          if (!appCompanysToUpdate.isEmpty()) {
            updateAppCompany(appCompanysToUpdate);
          }
        }
        String nptk = cacheManagerConfig.getNextPageToken("next_page_token");
        if (nptk != null) {
            getSFAppCompany(parameterRequest, batchStatus);
        }
        cacheManagerConfig.clearNextPageToken();
    } catch (Exception ex) {
      workerController.commonError(Constant.SF_REG + Constant.APPCOMPANY, batchStatus, ex);
    }
  }
  
  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [テーブル名（データ加工後のDB登録時にエラーになったテーブル名）]
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void coreDateCreatAppCompany(ParameterRequest parareq, BatchStatus batchStatus) {
	  List<AppCompany> objectList = new ArrayList<>();
      List<String> objectIds = new ArrayList<>();
      List<AppCompany> objects2Insert = new ArrayList<>();
      List<AppCompany> objects2Update = new ArrayList<>();
      List<AppCompany> compareList = new ArrayList<>();
      Map<String, AppCompany> hashMap = new HashMap<>();
    try {
      // Read the datasync file once only
      JsonMapper.readDataSync(Constant.APPCOMPANY);
      parareq.setLimit(Constant.LIMIT);
      Integer count = appCompanyMapper.countAppCompany(parareq);
      int size = count / Constant.LIMIT;
      
      for (int i = 0; i <= size; i++) {
        if (i < size) {
          parareq.setOffset(i*Constant.LIMIT);
          objectList = appCompanyMapper.getListAppCompany(parareq);
          LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from {} to {}",
              Constant.APPCOMPANY, Constant.APPCOMPANYSYNC,
              Constant.LIMIT * i, Constant.LIMIT * (i + 1));
        } else if (i == size) {
          parareq.setOffset(size*Constant.LIMIT);
          objectList = appCompanyMapper.getListAppCompany(parareq);
          LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from {} to {}",
              Constant.APPCOMPANY, Constant.APPCOMPANYSYNC,
              Constant.LIMIT * size, count);
        }
        if(objectList.size() == 0) return;
        //Add to HashMap
        for (AppCompany obj : objectList) {
        	hashMap.put(obj.getSfid(), obj);
        	objectIds.add(obj.getSfid());
        }
        //Request parameter
        //objectIds = Utility.getIdListFromObjectList(objectList);
        parareq.setIds(Utility.convertList(objectIds));
        parareq.setChecked(true);
        //Insert
        objects2Insert = selectAppCompanySyncList2InsertOrUpdate(hashMap, parareq, true);
        if (!objects2Insert.isEmpty()) {
        	insertAppCompanySync(objects2Insert);
        }
        //Reduce SFID to query
        objectIds = new ArrayList<String>(hashMap.keySet());
        parareq.setIds(Utility.convertList(objectIds));
        parareq.setChecked(false);
        
        compareList = appCompanyMapper.getListAppCompanySync(parareq);
        objects2Update = selectAppCompanySyncList2InsertOrUpdate(hashMap, parareq, false);
        objects2Update = getAppCompanySyncListEdited(objects2Update, compareList, hashMap);
        if (!objects2Update.isEmpty()) {
        	updateAppCompanySync(objects2Update);
        }
        parareq.setIds(null);
        hashMap.clear();
      }
    } catch (Exception ex) {
      workerController.commonError(Constant.BACK_REG + Constant.APPCOMPANYSYNC, batchStatus, ex);
    }
  }

  // Begin AppCompany
  /**
   * @param appCompanies
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void insertAppCompany(List<AppCompany> appCompanies) {
    for (int i = 0; i < appCompanies.size(); i++) {
      try {
        appCompanyMapper.insertAppCompany(appCompanies.get(i));
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error insert AppCompany record with id: {} with error => {} ",
            appCompanies.get(i).getSfid(), e.getMessage());
          Collections.swap(appCompanies, i + 1, appCompanies.size() - 1);
      }
    }
  }

  /**
   * @param appCompanies
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void updateAppCompany(List<AppCompany> appCompanies) {
    for (int i = 0; i < appCompanies.size(); i++) {
      try {
        appCompanyMapper.updateAppCompany(appCompanies.get(i));
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error update AppCompany record with id: {} with error => {} ",
            appCompanies.get(i).getSfid(), e.getMessage());
          Collections.swap(appCompanies, i + 1, appCompanies.size() - 1);
      }
    }
  }

  /**
   * @param appCompanies
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void insertAppCompanySync(List<AppCompany> appCompanies) {
    Worker worker = workerController.setWorker(Constant.APPCOMPANYSYNC);
    for (int i = 0; i < appCompanies.size(); i++) {
      try {
        appCompanyMapper
            .insertAppCompanySync(ConvertDataUtil.convertAppCompany2Sync(appCompanies.get(i), true));
        worker.setSfid(appCompanies.get(i).getSfid());
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error insert appcompanysync record with id: {} with error => {} ",
            appCompanies.get(i).getSfid(), e.getMessage());
        Collections.swap(appCompanies, i + 1, appCompanies.size() - 1);
      }
      workerController.insertWorker(worker);
    }
  }

  /**
   * @param appCompanies
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void updateAppCompanySync(List<AppCompany> appCompanies) {
    if (!appCompanies.isEmpty()) {
      Worker worker = workerController.setWorker(Constant.APPCOMPANYSYNC);
      for (int i = 0; i < appCompanies.size(); i++) {
        try {
          appCompanyMapper
              .updateAppCompanySync(ConvertDataUtil.convertAppCompany2Sync(appCompanies.get(i), true));
          LOGGER.info("AppCompanySync updating >>> " + appCompanies.get(i).getSfid());
          worker.setSfid(appCompanies.get(i).getSfid());
          // Syncテープルに更新場合：承認されたものは未承認変更。（Workerの「sycapproveflg」に「TRUE」→「FALSE」）
          worker.setSycapproveflg(false);
          workerController.updateWorker(worker);
        } catch (Exception e) {
          LOGGER.error(
              Constant.NORMALCODE.E03
                  + " >>> error update appcompanysync record with id: {} with error => {} ",
              appCompanies.get(i).getSfid(), e.getMessage());
          Collections.swap(appCompanies, i + 1, appCompanies.size() - 1);
        }
      }      
    }
  }

  public List<String> getListAppCompanyIdFromStockholm(List<String> objectIds) {
    ParameterRequest parareq = new ParameterRequest();
    parareq.setIds(Utility.parseList(objectIds));
    return appCompanyMapper.getListAppCompanyIdFromStockholm(parareq);
  }

  public List<AppCompany> getListAppCompanyToUpdate(List<String> stockholmIds,
		  Map<String, AppCompany> hashMap) {
	List<AppCompany> listObjToUpdate = new ArrayList<>();
    if (!stockholmIds.isEmpty()) {
      for (String sfid : stockholmIds) {
    	  listObjToUpdate.add(hashMap.get(sfid));
    	  hashMap.remove(sfid);//Remove update obj
      }
    }
    return listObjToUpdate;
  }
  
  public List<AppCompany> getAppCompanyListFromIdList(List<String> ids,
      List<AppCompany> objectList) {
    List<AppCompany> list = new ArrayList<>();
    for (int i = 0; i < ids.size(); i++) {
      for (int j = 0; j < objectList.size(); j++) {
        if (ids.get(i).equalsIgnoreCase(objectList.get(j).getSfid())) {
          list.add(i, objectList.get(j));
        }
      }
    }
    return list;
  }
  
  public List<AppCompany> selectAppCompanySyncList2InsertOrUpdate(Map<String, AppCompany> hashMap,
		  ParameterRequest parareq, boolean rmFlg ) {
	List<String> listIdToInUp= new ArrayList<>();
	List<AppCompany> listAccToInUp = new ArrayList<>();
    //Get List SFID to insert
	listIdToInUp = appCompanyMapper.getListAppCompanyIds(parareq);
    if (!listIdToInUp.isEmpty()) {
      for (String sfid : listIdToInUp) {
    	  listAccToInUp.add(hashMap.get(sfid));
    	  if (!rmFlg) continue;
    	  hashMap.remove(sfid);//For Reduce memory
      }
    }
    return listAccToInUp;
  }
    
  public List<AppCompany> getAppCompanySyncListEdited( List<AppCompany> objectList,
		  List<AppCompany> compareList, Map<String, AppCompany> hashMap) {
    List<String> ids2Update =  Utility.compare(convertAppcompanySyncList(objectList), compareList);
    List<AppCompany> listObject2Update = new ArrayList<>();
    if (!ids2Update.isEmpty()) {
      for (String sfid : ids2Update) {
    	  listObject2Update.add(hashMap.get(sfid));
    	  hashMap.remove(sfid);//For Reduce memory
      }
    }
    return listObject2Update;
  }
  
  public static List<AppCompany> convertAppcompanySyncList(List<AppCompany> appCompanyList){
    List<AppCompany> list = new ArrayList<>();
    AppCompany element = new AppCompany();
    for (int i = 0; i < appCompanyList.size(); i++) {
      element = ConvertDataUtil.convertAppCompany2Sync(appCompanyList.get(i), false);
      element.setInvaliddate(DateTimeUtil.getDateFromString(DateTimeUtil.getStringFromDate(appCompanyList.get(i).getInvaliddate(), DateTimeUtil.DD_FM_S), DateTimeUtil.DD_FM_S));
      element.setRequeststartdate(DateTimeUtil.getDateFromString(DateTimeUtil.getStringFromDate(appCompanyList.get(i).getRequeststartdate(), DateTimeUtil.DD_FM_S), DateTimeUtil.DD_FM_S));
      list.add(element);
    }
    return list;
  }
  // End AppCompany
}
