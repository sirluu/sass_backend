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
   * try catch: BACK_REG [テーブル名（データ加工後のDB登録時にエラーになったテーブル名）]
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void getSFAccount(ParameterRequest parameterRequest, BatchStatus batchStatus) {
    try {
      int error = cacheManagerConfig.getErrorCode(Constant.ACCOUNT.toLowerCase());
      if (Constant.checkError(error) == null) {
        //①SalesfoceAPIをコールして上記オブジェクト情報を取得する
        List<Account> sfAccountList =
            salesforceResponseController.getListAccountFromSalesforce(batchStatus);
        //③API取得に成功した情報をDBに登録、更新する
        if (!sfAccountList.isEmpty()) {
          List<String> sfAccIds = cacheManagerConfig.getListObjectId(Constant.ACCOUNT);
          List<String> stAccIds = getListAccountIdFromStockholm();
          List<Account> accountListToInsert =
              getListAccountToInsert(sfAccIds, stAccIds, sfAccountList);
          if (!accountListToInsert.isEmpty()) {
            insertAccount(accountListToInsert);
          }
          ParameterRequest parareq = new ParameterRequest();
          parareq.setStartTime(Utility.parseString(parameterRequest.getStartTime()));
          List<Account> stAccountList = accountMapper.getListAccount2Sync(parareq);
          int size = stAccountList.size();
          int offset = size / Constant.LIMIT;
          List<Account> syncList = new ArrayList<Account>();
          List<Account> accountListToUpdate = new ArrayList<Account>();
          for (int i = 0; i <= offset; i++) {
            if (i < offset) {
              syncList = stAccountList.subList(Constant.LIMIT * i, Constant.LIMIT * (i + 1));
              LOGGER.info("Checking updated data >>> from record {} to record {}", Constant.LIMIT * i,
                  Constant.LIMIT * (i + 1));
            } else if (i == offset) {
              syncList = stAccountList.subList(Constant.LIMIT * offset, size);
              LOGGER.info("Checking updated data >>> from record {} to record {}",
                  Constant.LIMIT * offset, size);
            }
            accountListToUpdate = getListAccountToUpdate(sfAccountList, syncList);
            if (!accountListToUpdate.isEmpty()) {
              updateAccount(accountListToUpdate);
            }
          }
        }
        String nptk = cacheManagerConfig.getNextPageToken("next_page_token");
        if (nptk != null) {
          getSFAccount(parameterRequest, batchStatus);
        }
      }
    } catch (Exception ex) {
      workerController.commonError(Constant.SF_REG + Constant.ACCOUNT, batchStatus, ex);
    } finally {
      cacheManagerConfig.clearMap(Constant.ACCOUNT);
    }
  }

  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [テーブル名（データ加工後のDB登録時にエラーになったテーブル名）]
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void coreDateCreatAccount(ParameterRequest parameterRequest, BatchStatus batchStatus) {
    try {
      ParameterRequest parareq = new ParameterRequest();
      parareq.setStartTime(Utility.parseString(parameterRequest.getStartTime()));
      List<Account> objectSyncList = accountMapper.getListAccount2Sync(parareq);
      List<String> objectIds = new ArrayList<>();

      int size = objectSyncList.size();
      int offset = size / Constant.LIMIT;

      List<Account> syncList = new ArrayList<>();
      List<Account> brooklynList = new ArrayList<Account>();
      for (int i = 0; i <= offset; i++) {
      	if (i < offset) {
      		syncList = objectSyncList.subList(Constant.LIMIT * i, Constant.LIMIT * (i + 1));
      		LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to record {}",
      				Constant.ACCOUNT, Constant.ACCOUNTSYNC, Constant.LIMIT * i,
      				Constant.LIMIT * (i + 1));
      	} else if (i == offset) {
      		syncList = objectSyncList.subList(Constant.LIMIT * offset, size);
      		LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to record {}",
      				Constant.ACCOUNT, Constant.ACCOUNTSYNC,
      				Constant.LIMIT * offset, size);
      	}
        objectIds = getListAccountIdFromStockholm(syncList);
        if (objectIds != null) {
          List<String> objectSyncIds = getListAccountSyncIdFromStockholm();
          List<Account> objectListToInsert =
              getListAccountToInsert(objectIds, objectSyncIds, syncList);
          if (!objectListToInsert.isEmpty()) {
            insertAccountSync(objectListToInsert);
          }
        }
        parareq.setIds(Utility.parseList(objectIds));
        brooklynList = accountMapper.getListAccountSyncFromStockholm(parareq);
        List<Account> objectListToUpdate = getListAccountToUpdateSync(syncList, brooklynList);
        if (!objectListToUpdate.isEmpty()) {
          updateAccountSync(objectListToUpdate);
        }
      }
    } catch (Exception ex) {
      workerController.commonError(Constant.BACK_REG + Constant.ACCOUNTSYNC, batchStatus, ex);
    }
  }

  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [テーブル名（データ加工後のDB登録時にエラーになったテーブル名）]
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void getSFFareTable(ParameterRequest parameterRequest, BatchStatus batchStatus) {
    try {
      int error = cacheManagerConfig.getErrorCode(Constant.FARETABLE.toLowerCase());
      if (Constant.checkError(error) == null) {
        List<FareTable> sfFareTableList =
            salesforceResponseController.getListFareTableFromSalesforce(batchStatus);
        if (!sfFareTableList.isEmpty()) {
          List<String> sfFareTableIds = cacheManagerConfig.getListObjectId(Constant.FARETABLE);
          List<String> stFareTableIds = getListFareTableIdFromStockholm();
          List<FareTable> fareTableListToInsert =
              getListFareTableToInsert(sfFareTableIds, stFareTableIds, sfFareTableList);
          if (!fareTableListToInsert.isEmpty()) {
            insertFareTable(fareTableListToInsert);
          }
          ParameterRequest parareq = new ParameterRequest();
          parareq.setStartTime(Utility.parseString(parameterRequest.getStartTime()));
          List<FareTable> stFareTableList = fareTableMapper.getListFareTable2Sync(parareq);
          int size = stFareTableList.size();
          int offset = size / Constant.LIMIT;
          List<FareTable> syncList = new ArrayList<FareTable>();
          List<FareTable> fareTables = new ArrayList<FareTable>();
          for (int i = 0; i <= offset; i++) {
            if (i < offset) {
              syncList = stFareTableList.subList(Constant.LIMIT * i, Constant.LIMIT * (i + 1));
              LOGGER.info("Checking updated data >>> from record {} to record {}", Constant.LIMIT * i,
                  Constant.LIMIT * (i + 1));
            } else if (i == offset) {
              syncList = stFareTableList.subList(Constant.LIMIT * offset, size);
              LOGGER.info("Checking updated data >>> from record {} to record {}",
                  Constant.LIMIT * offset, size);
            }
            fareTables = getListFareTableToUpdate(sfFareTableList, syncList);
            if (!fareTables.isEmpty()) {
              updateFareTable(fareTables);
            }
          }
        }
        String nptk = cacheManagerConfig.getNextPageToken("next_page_token");
        if (nptk != null) {
          getSFFareTable(parameterRequest, batchStatus);
        }
      }
    } catch (Exception ex) {
      workerController.commonError(Constant.SF_REG + Constant.FARETABLE, batchStatus, ex);
    } finally {
      cacheManagerConfig.clearMap(Constant.FARETABLE);
    }
  }

  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [テーブル名（データ加工後のDB登録時にエラーになったテーブル名）]
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void coreDateCreatFareTable(ParameterRequest parameterRequest, BatchStatus batchStatus) {
    try {
      ParameterRequest parareq = new ParameterRequest();
      parareq.setStartTime(Utility.parseString(parameterRequest.getStartTime()));
      List<FareTable> objectSyncList = fareTableMapper.getListFareTable2Sync(parareq);
      List<String> objectIds = new ArrayList<>();

      int size = objectSyncList.size();
      int offset = size / Constant.LIMIT;

      List<FareTable> syncList = new ArrayList<FareTable>();
      List<FareTable> brooklynList = new ArrayList<FareTable>();
      for (int i = 0; i <= offset; i++) {
      	if (i < offset) {
      		syncList = objectSyncList.subList(Constant.LIMIT * i, Constant.LIMIT * (i + 1));
      		LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to record {}",
      				Constant.FARETABLE, Constant.FARETABLESYNC, Constant.LIMIT * i,
      				Constant.LIMIT * (i + 1));
      	} else if (i == offset) {
      		syncList = objectSyncList.subList(Constant.LIMIT * offset, size);
      		LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to record {}",
      				Constant.FARETABLE, Constant.FARETABLESYNC,
      				Constant.LIMIT * offset, size);
      	}
      	objectIds = getListFareTableIdFromStockholm(syncList);
        if (objectIds != null) {
          List<String> objectSyncIds = getListFareTableSyncIdFromStockholm();
          List<FareTable> objectListToInsert =
              getListFareTableToInsert(objectIds, objectSyncIds, syncList);
          if (!objectListToInsert.isEmpty()) {
            insertFareTableSync(objectListToInsert);
          }
        }
        parareq.setIds(Utility.parseList(objectIds));
        brooklynList = fareTableMapper.getListFareTableSyncFromStockholm(parareq);
        List<FareTable> objectListToUpdate = getListFareTableToUpdateSync(syncList, brooklynList);
        if (!objectListToUpdate.isEmpty()) {
          updateFareTableSync(objectListToUpdate);
        }
      }
    } catch (Exception ex) {
       workerController.commonError(Constant.BACK_REG + Constant.FARETABLESYNC, batchStatus, ex);
    }
  }

  // Begin FareTable
  /**
   * @param fareTableList
   * try catch: Sentry 連携しエラー通知を行う
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
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void updateFareTable(List<FareTable> fareTableList) {
    for (int i = 0; i < fareTableList.size(); i++) {
      try {
        fareTableMapper.updateFareTable(fareTableList.get(i));
        LOGGER.info("updateFareTable >>> " + fareTableList.get(i).getSfid());
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
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void insertFareTableSync(List<FareTable> fareTableList) {
    Worker worker = workerController.setWorker(Constant.FARETABLESYNC);
    for (int i = 0; i < fareTableList.size(); i++) {
      try {
        fareTableMapper
            .insertFareTableSync(ConvertDataUtil.convertFareTable2Sync(fareTableList.get(i), true));
        worker.setSfid(fareTableList.get(i).getSfid());
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
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void updateFareTableSync(List<FareTable> fareTableList) {
    Worker worker = workerController.setWorker(Constant.FARETABLESYNC);
    for (int i = 0; i < fareTableList.size(); i++) {
      try {
        fareTableMapper
            .updateFareTableSync(ConvertDataUtil.convertFareTable2Sync(fareTableList.get(i), true));
        LOGGER.info("updateFareTableSync >>> " + fareTableList.get(i).getSfid());
        worker.setSfid(fareTableList.get(i).getSfid());
        // Syncテープルに更新場合：承認されたものは未承認変更。（Workerの「sycapproveflg」に「TRUE」→「FALSE」）
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

  public List<FareTable> getListFareTableFromStockholm(String context) {
    return fareTableMapper.getListFareTableFromStockholm(
        salesforceResponseController.parameterRequest(context));
  }

  public List<String> getListFareTableIdFromStockholm() {
    return fareTableMapper.getListFareTableIdFromStockholm(
        salesforceResponseController.parameterRequest(Constant.FARETABLE));
  }

  public List<String> getListFareTableIdFromStockholm(List<FareTable> fareTableList) {
    List<String> listId = new ArrayList<>();
    for (int i = 0; i < fareTableList.size(); i++) {
      listId.add(fareTableList.get(i).getSfid());
    }
    return listId;
  }

  public List<String> getListFareTableSyncIdFromStockholm() {
    return fareTableMapper.getListFareTableSyncIdFromStockholm();
  }

  public List<FareTable> getListFareTableToInsert(List<String> salesForceIds,
      List<String> stockholmIds, List<FareTable> sfFareTableList) {
    List<String> listIdToInsert = new ArrayList<>();
    if (stockholmIds.isEmpty()) {
      listIdToInsert = Utility.intersection(salesForceIds, stockholmIds);
    } else {
      listIdToInsert = Utility.difference(stockholmIds, salesForceIds);
    }
    List<FareTable> listFareTableToInsert = new ArrayList<>();
    if (!listIdToInsert.isEmpty()) {
      for (String sfid : listIdToInsert) {
        for (FareTable fareTable : sfFareTableList) {
          if (fareTable.getSfid().equalsIgnoreCase(sfid)) {
            listFareTableToInsert.add(fareTable);
          }
        }
      }
    }
    return listFareTableToInsert;
  }

  public List<FareTable> getListFareTableToUpdate(List<FareTable> sfFareTableList, List<FareTable> stFareTableList) {
    List<String> listIdToUpdate = Utility.compare(sfFareTableList, stFareTableList);
    List<FareTable> listFareTableToUpdate = new ArrayList<>();
    if (!listIdToUpdate.isEmpty()) {
      for (String sfid : listIdToUpdate) {
        for (FareTable fareTable : sfFareTableList) {
          if (fareTable.getSfid().equalsIgnoreCase(sfid)) {
            listFareTableToUpdate.add(fareTable);
          }
        }
      }
    }
    return listFareTableToUpdate;
  }

  public List<FareTable> getListFareTableToUpdateSync(
      List<FareTable> sfFareTableList,
      List<FareTable> stFareTableList) {
    List<String> listIdToUpdate =
        Utility.compare(updateFareTableListSync(sfFareTableList), stFareTableList);
    List<FareTable> listFareTableToUpdate = new ArrayList<>();
    if (!listIdToUpdate.isEmpty()) {
      for (String sfid : listIdToUpdate) {
        for (FareTable fareTable : sfFareTableList) {
          if (fareTable.getSfid().equalsIgnoreCase(sfid)) {
            listFareTableToUpdate.add(fareTable);
          }
        }
      }
    }
    return listFareTableToUpdate;
  }
  
  public static List<FareTable> updateFareTableListSync(List<FareTable> fareTableList){
    List<FareTable> list = new ArrayList<>();
    FareTable element = new FareTable();
    for (int i = 0; i < fareTableList.size(); i++) {
      element = ConvertDataUtil.convertFareTable2Sync(fareTableList.get(i), false);
      list.add(element);
    }
    return list;
  }
  // End FareTable

  // Begin Account
  /**
   * @param accountList
   * try catch: Sentry 連携しエラー通知を行う
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
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void updateAccount(List<Account> accountList) {
    for (int i = 0; i < accountList.size(); i++) {
      try {
        accountMapper.updateAccount(accountList.get(i));
        LOGGER.info("updateAccount >>> " + accountList.get(i).getSfid());
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
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void insertAccountSync(List<Account> accountList) {
    Worker worker = workerController.setWorker(Constant.ACCOUNTSYNC);
    for (int i = 0; i < accountList.size(); i++) {
      try {
        accountMapper.insertAccountSync(ConvertDataUtil.convertAccount2Sync(accountList.get(i), true));
        worker.setSfid(accountList.get(i).getSfid());
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
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void updateAccountSync(List<Account> accountList) {
    Worker worker = workerController.setWorker(Constant.ACCOUNTSYNC);
    for (int i = 0; i < accountList.size(); i++) {
      try {
        accountMapper.updateAccountSync(ConvertDataUtil.convertAccount2Sync(accountList.get(i), true));
        LOGGER.info("updateAccountSync >>> " + accountList.get(i).getSfid());
        worker.setSfid(accountList.get(i).getSfid());
        // Syncテープルに更新場合：承認されたものは未承認変更。（Workerの「sycapproveflg」に「TRUE」→「FALSE」）
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

  public List<Account> getListAccountFromStockholm(String context) {
    return accountMapper.getListAccountFromStockholm(
        salesforceResponseController.parameterRequest(context));
  }

  public List<String> getListAccountIdFromStockholm() {
    return accountMapper.getListAccountIdFromStockholm(
        salesforceResponseController.parameterRequest(Constant.ACCOUNT));
  }

  public List<String> getListAccountIdFromStockholm(List<Account> accountList) {
    List<String> listId = new ArrayList<>();
    for (int i = 0; i < accountList.size(); i++) {
      listId.add(accountList.get(i).getSfid());
    }
    return listId;
  }

  public List<String> getListAccountSyncIdFromStockholm() {
    return accountMapper.getListAccountSyncIdFromStockholm();
  }

  public List<Account> getListAccountToInsert(List<String> salesForceIds, List<String> stockholmIds,
      List<Account> sfAccountList) {
    List<String> listIdToInsert = new ArrayList<>();
    if (stockholmIds.isEmpty()) {
      listIdToInsert = Utility.intersection(salesForceIds, stockholmIds);
    } else {
      listIdToInsert = Utility.difference(stockholmIds, salesForceIds);
    }
    List<Account> listAccountToInsert = new ArrayList<>();
    if (!listIdToInsert.isEmpty()) {
      for (String sfid : listIdToInsert) {
        for (Account account : sfAccountList) {
          if (account.getSfid().equalsIgnoreCase(sfid)) {
            listAccountToInsert.add(account);
          }
        }
      }
    }
    return listAccountToInsert;
  }

  public List<Account> getListAccountToUpdate(List<Account> sfAccountList, List<Account> stAccountList) {
    List<String> listIdToUpdate = Utility.compare(updateAccList(sfAccountList), updateAccList(stAccountList));
    List<Account> listAccountToUpdate = new ArrayList<>();
    if (!listIdToUpdate.isEmpty()) {
      for (String sfid : listIdToUpdate) {
        for (Account account : sfAccountList) {
          if (account.getSfid().equalsIgnoreCase(sfid)) {
            listAccountToUpdate.add(account);
          }
        }
      }
    }
    return listAccountToUpdate;
  }

  public List<Account> getListAccountToUpdateSync(
      List<Account> sfAccountList,
      List<Account> stAccountList) {
    List<String> listIdToUpdate =
        Utility.compare(updateAccountListSync(sfAccountList), stAccountList);
    List<Account> listAccountToUpdate = new ArrayList<>();
    if (!listIdToUpdate.isEmpty()) {
      for (String sfid : listIdToUpdate) {
        for (Account acc : sfAccountList) {
          if (acc.getSfid().equalsIgnoreCase(sfid)) {
            listAccountToUpdate.add(acc);
          }
        }
      }
    }
    return listAccountToUpdate;
  }
  
  public static List<Account> updateAccList(List<Account> accList) {
    List<Account> list = new ArrayList<>();
    Account element = new Account();
    for (int i = 0; i < accList.size(); i++) {
      element = accList.get(i);
      element.setInvaliddate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(accList.get(i).getInvaliddate(), DateTimeUtil.DD_FM_S),
          DateTimeUtil.DD_FM_S));
      element.setMerchantoperinforcontractenddate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(accList.get(i).getMerchantoperinforcontractenddate(), DateTimeUtil.DD_FM_S),
          DateTimeUtil.DD_FM_S));
      element.setMerchantoperinforguaranteeenddate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(accList.get(i).getMerchantoperinforguaranteeenddate(), DateTimeUtil.DD_FM_S),
          DateTimeUtil.DD_FM_S));
      element.setMerchantoperinforacceptancedate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(accList.get(i).getMerchantoperinforacceptancedate(), DateTimeUtil.DD_FM_S),
          DateTimeUtil.DD_FM_S));
      element.setMerchantoperinforstartdate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(accList.get(i).getMerchantoperinforstartdate(), DateTimeUtil.DD_FM_S),
          DateTimeUtil.DD_FM_S));
      element.setMerchantreprinforbirthday(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(accList.get(i).getMerchantreprinforbirthday(), DateTimeUtil.DD_FM_S),
          DateTimeUtil.DD_FM_S));
      element.setMerchantcontrinforlicenseacquisitiondate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(accList.get(i).getMerchantcontrinforlicenseacquisitiondate(), DateTimeUtil.DD_FM_S),
          DateTimeUtil.DD_FM_S));
      list.add(element);
    }
    return list;
  }

  public static List<Account> updateAccountListSync(List<Account> accList){
    List<Account> list = new ArrayList<>();
    Account element = new Account();
    for (int i = 0; i < accList.size(); i++) {
      element = ConvertDataUtil.convertAccount2Sync(accList.get(i), false);
      element.setInvaliddate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(accList.get(i).getInvaliddate(), DateTimeUtil.DD_FM_S),
          DateTimeUtil.DD_FM_S));
      element.setMerchantoperinforcontractenddate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(accList.get(i).getMerchantoperinforcontractenddate(), DateTimeUtil.DD_FM_S),
          DateTimeUtil.DD_FM_S));
      element.setMerchantoperinforguaranteeenddate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(accList.get(i).getMerchantoperinforguaranteeenddate(), DateTimeUtil.DD_FM_S),
          DateTimeUtil.DD_FM_S));
      element.setMerchantoperinforacceptancedate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(accList.get(i).getMerchantoperinforacceptancedate(), DateTimeUtil.DD_FM_S),
          DateTimeUtil.DD_FM_S));
      element.setMerchantoperinforstartdate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(accList.get(i).getMerchantoperinforstartdate(), DateTimeUtil.DD_FM_S),
          DateTimeUtil.DD_FM_S));
      element.setMerchantreprinforbirthday(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(accList.get(i).getMerchantreprinforbirthday(), DateTimeUtil.DD_FM_S),
          DateTimeUtil.DD_FM_S));
      element.setMerchantcontrinforlicenseacquisitiondate(DateTimeUtil.getDateFromString(
          DateTimeUtil.getStringFromDate(accList.get(i).getMerchantcontrinforlicenseacquisitiondate(), DateTimeUtil.DD_FM_S),
          DateTimeUtil.DD_FM_S));
      list.add(element);
    }
    return list;
  }
  // End Account
}
