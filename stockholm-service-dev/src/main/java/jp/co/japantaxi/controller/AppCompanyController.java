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
          List<AppCompany> appCompanysToUpdate =
              getListAppCompanyToUpdate(sfAppCompanyIds, stAppCompanyIds, sfAppCompanyList);
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
      List<AppCompany> syncList = new ArrayList<AppCompany>();
      for (int i = 0; i <= offset; i++) {
      	if (i < offset) {
      		syncList = objectSyncList.subList(Constant.LIMIT * i, Constant.LIMIT * (i + 1));
      		LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to record {}",
      				Constant.APPCOMPANY, Constant.APPCOMPANYSYNC, Constant.LIMIT * i,
      				Constant.LIMIT * (i + 1));
      	} else if (i == offset) {
      		syncList = objectSyncList.subList(Constant.LIMIT * offset, size);
      		LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to record {}",
      				Constant.APPCOMPANY, Constant.APPCOMPANYSYNC,
      				Constant.LIMIT * offset, size);
      	}
      	objectIds = getListAppCompanyIdFromStockholm(syncList);
      	if (objectIds != null) {
      		List<String> objectSyncIds = getListAppCompanySyncIdFromStockholm();
      		List<AppCompany> objectListToInsert = getListAppCompanyToInsert(
      				objectIds, objectSyncIds, syncList);
      		List<AppCompany> objectListToUpdate = getListAppCompanyToUpdate(
      				objectIds, objectSyncIds, syncList);
      		if (!objectListToInsert.isEmpty()) {
      			insertAppCompanySync(objectListToInsert);
      		}
      		if (!objectListToUpdate.isEmpty()) {
      			updateAppCompanySync(objectListToUpdate);
      		}
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
            .insertAppCompanySync(ConvertDataUtil.convertAppCompany2Sync(appCompanies.get(i)));
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
    Worker worker = workerController.setWorker(Constant.APPCOMPANYSYNC);
    for (int i = 0; i < appCompanies.size(); i++) {
      try {
        appCompanyMapper
            .updateAppCompanySync(ConvertDataUtil.convertAppCompany2Sync(appCompanies.get(i)));
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

  public List<AppCompany> getListAppCompanyFromStockholm() {
    return appCompanyMapper.getListAppCompanyFromStockholm(
        salesforceResponseController.parameterRequest(Constant.APPCOMPANY));
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

  public List<AppCompany> getListAppCompanyToUpdate(List<String> salesForceIds,
      List<String> stockholmIds, List<AppCompany> appCompanies) {
    List<String> listIdToUpdate = new ArrayList<>();
    if (!stockholmIds.isEmpty()) {
      listIdToUpdate = Utility.intersection(salesForceIds, stockholmIds);
    }
    List<AppCompany> appCompaniesSyncToUpdate = new ArrayList<>();
    if (!listIdToUpdate.isEmpty()) {
      for (String sfid : listIdToUpdate) {
        for (AppCompany appCompany : appCompanies) {
          if (appCompany.getSfid().equalsIgnoreCase(sfid)) {
            appCompaniesSyncToUpdate.add(appCompany);
          }
        }
      }
    }
    return appCompaniesSyncToUpdate;
  }
  // End AppCompany
}
