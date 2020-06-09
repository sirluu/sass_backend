package jp.co.japantaxi.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    try {
      int error = cacheManagerConfig.getErrorCode(Constant.APPCOMPANY.toLowerCase());
      if (Constant.checkError(error) == null) {
        List<AppCompany> sfAppCompanyList =
            salesforceResponseController.getListAppCompanyFromSalesforce(batchStatus);
        if (!sfAppCompanyList.isEmpty()) {
          List<String> sfAppCompanyIds = cacheManagerConfig.getListObjectId(Constant.APPCOMPANY);
          List<String> stAppCompanyIds = getListAppCompanyIdFromStockholm();
          List<AppCompany> appCompanysToInsert =
              getListAppCompanyToInsert(sfAppCompanyIds, stAppCompanyIds, sfAppCompanyList);
          if (!appCompanysToInsert.isEmpty()) {
            insertAppCompany(appCompanysToInsert);
          }
          ParameterRequest parareq = new ParameterRequest();
          parareq.setStartTime(Utility.parseString(parameterRequest.getStartTime()));
          List<AppCompany> stAppCompanyList = appCompanyMapper.getListAppCompany2Sync(parareq);
          int size = stAppCompanyList.size();
          int offset = size / Constant.LIMIT;
          List<AppCompany> syncList = new ArrayList<AppCompany>();
          List<AppCompany> appCompanies = new ArrayList<AppCompany>();
          for (int i = 0; i <= offset; i++) {
            if (i < offset) {
              syncList = stAppCompanyList.subList(Constant.LIMIT * i, Constant.LIMIT * (i + 1));
              LOGGER.info("Checking updated data >>> from record {} to record {}", Constant.LIMIT * i,
                  Constant.LIMIT * (i + 1));
            } else if (i == offset) {
              syncList = stAppCompanyList.subList(Constant.LIMIT * offset, size);
              LOGGER.info("Checking updated data >>> from record {} to record {}",
                  Constant.LIMIT * offset, size);
            }
            appCompanies = getListAppCompanyToUpdate(sfAppCompanyList, syncList);
            if (!appCompanies.isEmpty()) {
              updateAppCompany(appCompanies);
            }
          }
        }
        String nptk = cacheManagerConfig.getNextPageToken("next_page_token");
        if (nptk != null) {
          getSFAppCompany(parameterRequest, batchStatus);
        }
      }
    } catch (Exception ex) {
      workerController.commonError(Constant.SF_REG + Constant.APPCOMPANY, batchStatus, ex);
    } finally {
      cacheManagerConfig.clearMap(Constant.APPCOMPANY);
    }
  }

  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [テーブル名（データ加工後のDB登録時にエラーになったテーブル名）]
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void coreDateCreatAppCompany(ParameterRequest parameterRequest, BatchStatus batchStatus) {
    try {
      ParameterRequest parareq = new ParameterRequest();
      parareq.setStartTime(Utility.parseString(parameterRequest.getStartTime()));
      List<AppCompany> objectSyncList = appCompanyMapper.getListAppCompany2Sync(parareq);
      List<String> objectIds = new ArrayList<>();

      int size = objectSyncList.size();
      int offset = size / Constant.LIMIT;
      // ファイルを1回だけ読み取る
      // Read the datasync file once only
      JsonMapper.readDataSync(Constant.APPCOMPANY);
      List<AppCompany> syncList = new ArrayList<>();
      List<AppCompany> brooklynList = new ArrayList<AppCompany>();
      for (int i = 0; i <= offset; i++) {
        if (i < offset) {
          syncList = objectSyncList.subList(Constant.LIMIT * i, Constant.LIMIT * (i + 1));
          LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to record {}",
              Constant.APPCOMPANY, Constant.APPCOMPANYSYNC, Constant.LIMIT * i,
              Constant.LIMIT * (i + 1));
        } else if (i == offset) {
          syncList = objectSyncList.subList(Constant.LIMIT * offset, size);
          LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to record {}",
              Constant.APPCOMPANY, Constant.APPCOMPANYSYNC, Constant.LIMIT * offset, size);
        }
        objectIds = getListAppCompanyIdFromStockholm(syncList);
        if (objectIds != null) {
          List<String> objectSyncIds = getListAppCompanySyncIdFromStockholm();
          List<AppCompany> objectListToInsert =
              getListAppCompanyToInsert(objectIds, objectSyncIds, syncList);
          if (!objectListToInsert.isEmpty()) {
            insertAppCompanySync(objectListToInsert);
          }
        }
        parareq.setIds(Utility.parseList(objectIds));
        brooklynList = appCompanyMapper.getListAppCompanySyncFromStockholm(parareq);
        List<AppCompany> objectListToUpdate = getListAppCompanyToUpdateSync(syncList, brooklynList);
        if (!objectListToUpdate.isEmpty()) {
          updateAppCompanySync(objectListToUpdate);
        }
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
        LOGGER.info("updateAppCompany >>> " + appCompanies.get(i).getSfid());
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
          LOGGER.info("updateAppCompanySync >>> " + appCompanies.get(i).getSfid());
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

  public List<AppCompany> getListAppCompanyFromStockholm(String context) {
    return appCompanyMapper.getListAppCompanyFromStockholm(
        salesforceResponseController.parameterRequest(context));
  }

  public List<String> getListAppCompanyIdFromStockholm() {
    return appCompanyMapper.getListAppCompanyIdFromStockholm(
        salesforceResponseController.parameterRequest(Constant.APPCOMPANY));
  }

  public List<String> getListAppCompanyIdFromStockholm(List<AppCompany> appCompanyList) {
    List<String> listId = new ArrayList<>();
    for (int i = 0; i < appCompanyList.size(); i++) {
      listId.add(appCompanyList.get(i).getSfid());
    }
    return listId;
  }

  public List<String> getListAppCompanySyncIdFromStockholm() {
    return appCompanyMapper.getListAppCompanySyncIdFromStockholm();
  }

  public List<AppCompany> getListAppCompanyToInsert(List<String> salesForceIds,
      List<String> stockholmIds, List<AppCompany> appCompanies) {
    List<String> listIdToInsert = new ArrayList<>();
    if (stockholmIds.isEmpty()) {
      listIdToInsert = Utility.intersection(salesForceIds, stockholmIds);
    } else {
      listIdToInsert = Utility.difference(stockholmIds, salesForceIds);
    }
    List<AppCompany> appCompaniesToInsert = new ArrayList<>();
    if (!listIdToInsert.isEmpty()) {
      for (String sfid : listIdToInsert) {
        for (AppCompany appCompany : appCompanies) {
          if (appCompany.getSfid().equalsIgnoreCase(sfid)) {
            appCompaniesToInsert.add(appCompany);
          }
        }
      }
    }
    return appCompaniesToInsert;
  }

  public List<AppCompany> getListAppCompanyToUpdate(List<AppCompany> sfAppCompanyList, List<AppCompany> stAppCompanyList) {
    List<String> listIdToUpdate = Utility.compare(updateAppList(sfAppCompanyList), updateAppList(stAppCompanyList));
    List<AppCompany> listAppCompanyToUpdate = new ArrayList<>();
    if (!listIdToUpdate.isEmpty()) {
      for (String sfid : listIdToUpdate) {
        for (AppCompany appCompany : sfAppCompanyList) {
          if (appCompany.getSfid().equalsIgnoreCase(sfid)) {
            listAppCompanyToUpdate.add(appCompany);
          }
        }
      }
    }
    return listAppCompanyToUpdate;
  }

  public List<AppCompany> getListAppCompanyToUpdateSync(
      List<AppCompany> sfAppCompanyList,
      List<AppCompany> stAppCompanyList) {
    List<String> listIdToUpdate =
        Utility.compare(updateAppCompanyListSync(sfAppCompanyList), stAppCompanyList);
    List<AppCompany> listAppCompanyToUpdate = new ArrayList<>();
    if (!listIdToUpdate.isEmpty()) {
      for (String sfid : listIdToUpdate) {
        for (AppCompany bbb : sfAppCompanyList) {
          if (bbb.getSfid().equalsIgnoreCase(sfid)) {
            listAppCompanyToUpdate.add(bbb);
          }
        }
      }
    }
    return listAppCompanyToUpdate;
  }
  
  public static List<AppCompany> updateAppList(List<AppCompany> appCompanyList){
    List<AppCompany> list = new ArrayList<>();
    AppCompany element = new AppCompany();
    for (int i = 0; i < appCompanyList.size(); i++) {
      element = appCompanyList.get(i);
      element.setInvaliddate(DateTimeUtil.getDateFromString(DateTimeUtil.getStringFromDate(
          appCompanyList.get(i).getInvaliddate(), DateTimeUtil.DATE_FM_S), DateTimeUtil.DATE_FM_S));
      element.setRequeststartdate(DateTimeUtil.getDateFromString(DateTimeUtil
          .getStringFromDate(appCompanyList.get(i).getRequeststartdate(), DateTimeUtil.DATE_FM_S),
          DateTimeUtil.DATE_FM_S));
      list.add(element);
    }
    return list;
  }
  
  public static List<AppCompany> updateAppCompanyListSync(List<AppCompany> appCompanyList){
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
