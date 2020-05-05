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
import jp.co.japantaxi.mapper.stockholm.BankAccountInformationMapper;
import jp.co.japantaxi.mapper.stockholm.BankMasterMapper;
import jp.co.japantaxi.model.BankAccountInformation;
import jp.co.japantaxi.model.BankMaster;
import jp.co.japantaxi.model.BatchStatus;
import jp.co.japantaxi.model.ParameterRequest;
import jp.co.japantaxi.model.Worker;
import jp.co.japantaxi.utils.Constant;
import jp.co.japantaxi.utils.ConvertDataUtil;
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
   * try catch: BACK_REG [テーブル名（データ加工後のDB登録時にエラーになったテーブル名）]
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void getSFBankMaster(ParameterRequest parameterRequest, BatchStatus batchStatus) {
    try {
      int error = cacheManagerConfig.getErrorCode(Constant.BANKMASTER.toLowerCase());
      if (Constant.checkError(error) == null) {
        List<BankMaster> sfBankMasterList =
            salesforceResponseController.getListBankMasterFromSalesforce(batchStatus);
        if (!sfBankMasterList.isEmpty()) {
          List<String> sfBankMasterIds = cacheManagerConfig.getListObjectId(Constant.BANKMASTER);
          List<String> stBankMasterIds = getListBankMasterIdFromStockholm();
          List<BankMaster> bankMasterListToInsert =
              getListBankMasterToInsert(sfBankMasterIds, stBankMasterIds, sfBankMasterList);
          List<BankMaster> bankMasterListToUpdate =
              getListBankMasterToUpdate(sfBankMasterIds, stBankMasterIds, sfBankMasterList);
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
      }
    } catch (Exception ex) {
      workerController.commonError(Constant.SF_REG + Constant.BANKMASTER, batchStatus, ex);
    } finally {
    	cacheManagerConfig.clearMap(Constant.BANKMASTER);
	}
  }

  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [テーブル名（データ加工後のDB登録時にエラーになったテーブル名）]
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void coreDateCreatBankMaster(ParameterRequest parameterRequest, BatchStatus batchStatus) {
    try {
      ParameterRequest parareq = new ParameterRequest();
      parareq.setStartTime(Utility.parseString(parameterRequest.getStartTime()));
      List<BankMaster> objectSyncList = bankMasterMapper.getListBankMaster2Sync(parareq);
      List<String> objectIds = new ArrayList<>();

      int size = objectSyncList.size();
      int offset = size / Constant.LIMIT;

      List<BankMaster> syncList = new ArrayList<BankMaster>();
      for (int i = 0; i <= offset; i++) {
      	if (i < offset) {
      		syncList = objectSyncList.subList(Constant.LIMIT * i, Constant.LIMIT * (i + 1));
      		LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to record {}",
      				Constant.BANKMASTER, Constant.BANKMASTERSYNC, Constant.LIMIT * i,
      				Constant.LIMIT * (i + 1));
      	} else if (i == offset) {
      		syncList = objectSyncList.subList(Constant.LIMIT * offset, size);
      		LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to record {}",
      				Constant.BANKMASTER, Constant.BANKMASTERSYNC,
      				Constant.LIMIT * offset, size);
      	}
      	objectIds = getListBankMasterIdFromStockholm(syncList);
      	if (objectIds != null) {
      		List<String> objectSyncIds = getListBankMasterSyncIdFromStockholm();
      		List<BankMaster> objectListToInsert = getListBankMasterToInsert(
      				objectIds, objectSyncIds, syncList);
      		List<BankMaster> objectListToUpdate = getListBankMasterToUpdate(
      				objectIds, objectSyncIds, syncList);
      		if (!objectListToInsert.isEmpty()) {
      			insertBankMasterSync(objectListToInsert);
      		}
      		if (!objectListToUpdate.isEmpty()) {
      			updateBankMasterSync(objectListToUpdate);
      		}
      	}
      }
    } catch (Exception ex) {
      workerController.commonError(Constant.BACK_REG + Constant.BANKMASTERSYNC, batchStatus, ex);
    }
  }

  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [テーブル名（データ加工後のDB登録時にエラーになったテーブル名）]
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void getSFBankAccountInformation(ParameterRequest parameterRequest,
      BatchStatus batchStatus) {
    try {
      int error = cacheManagerConfig.getErrorCode(Constant.BANKACCOUNTINFORMATION.toLowerCase());
      if (Constant.checkError(error) == null) {
        List<BankAccountInformation> sfBankAccountInformationList =
            salesforceResponseController.getListBankAccountInformationFromSalesforce(batchStatus);
        if (!sfBankAccountInformationList.isEmpty()) {
          List<String> sfBankAccountInformationIds =
              cacheManagerConfig.getListObjectId(Constant.BANKACCOUNTINFORMATION);
          List<String> stBankAccountInformationIds = getListBankAccountInformationIdFromStockholm();
          List<BankAccountInformation> bankAccountInformationListToInsert =
              getListBankAccountInformationToInsert(sfBankAccountInformationIds,
                  stBankAccountInformationIds, sfBankAccountInformationList);
          List<BankAccountInformation> bankAccountInformationListToUpdate =
              getListBankAccountInformationToUpdate(sfBankAccountInformationIds,
                  stBankAccountInformationIds, sfBankAccountInformationList);
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
      }
    } catch (Exception ex) {
      workerController.commonError(Constant.SF_REG
          + Constant.BANKACCOUNTINFORMATION, batchStatus, ex);
    } finally {
    	cacheManagerConfig.clearMap(Constant.BANKACCOUNTINFORMATION);
	}
  }

  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [テーブル名（データ加工後のDB登録時にエラーになったテーブル名）]
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void coreDateCreatBankAccountInformation(ParameterRequest parameterRequest, BatchStatus batchStatus) {
	try {
		ParameterRequest parareq = new ParameterRequest();
		parareq.setStartTime(Utility.parseString(parameterRequest.getStartTime()));
		List<BankAccountInformation> objectSyncList = bankAccountInforMapper
				.getListBankAccountInformation2Sync(parareq);
		List<String> objectIds = new ArrayList<>();

		int size = objectSyncList.size();
		int offset = size / Constant.LIMIT;
		// ファイルを1回だけ読み取る
		// Read the datasync file once only
		JsonMapper.readDataSync(Constant.BANKACCOUNTINFORMATION);
		List<BankAccountInformation> syncList = new ArrayList<BankAccountInformation>();
		for (int i = 0; i <= offset; i++) {
			if (i < offset) {
				syncList = objectSyncList.subList(Constant.LIMIT * i, Constant.LIMIT * (i + 1));
				LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to record {}",
						Constant.BANKACCOUNTINFORMATION, Constant.BANKACCOUNTINFORMATIONSYNC, Constant.LIMIT * i,
						Constant.LIMIT * (i + 1));
			} else if (i == offset) {
				syncList = objectSyncList.subList(Constant.LIMIT * offset, size);
				LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to record {}",
						Constant.BANKACCOUNTINFORMATION, Constant.BANKACCOUNTINFORMATIONSYNC,
						Constant.LIMIT * offset, size);
			}
			objectIds = getListBankAccountInformationIdFromStockholm(syncList);
			if (objectIds != null) {
				List<String> objectSyncIds = getListBankAccountInformationSyncIdFromStockholm();
				List<BankAccountInformation> objectListToInsert = getListBankAccountInformationToInsert(
						objectIds, objectSyncIds, syncList);
				List<BankAccountInformation> objectListToUpdate = getListBankAccountInformationToUpdate(
						objectIds, objectSyncIds, syncList);
				if (!objectListToInsert.isEmpty()) {
					insertBankAccountInformationSync(objectListToInsert);
				}
				if (!objectListToUpdate.isEmpty()) {
					updateBankAccountInformationSync(objectListToUpdate);
				}
			}
		}
	} catch (Exception ex) {
		workerController.commonError(Constant.BACK_REG + Constant.BANKACCOUNTINFORMATIONSYNC, batchStatus, ex);
	}
  }

  // Begin BankMaster
  /**
   * @param bankMasterList
   * try catch: Sentry 連携しエラー通知を行う
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
   * try catch: Sentry 連携しエラー通知を行う
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
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void insertBankMasterSync(List<BankMaster> bankMasterList) {
    Worker worker = workerController.setWorker(Constant.BANKMASTERSYNC);
    for (int i = 0; i < bankMasterList.size(); i++) {
      try {
        bankMasterMapper
            .insertBankMasterSync(ConvertDataUtil.convertBankMaster2Sync(bankMasterList.get(i)));
        worker.setSfid(bankMasterList.get(i).getSfid());
        workerController.insertWorker(worker);
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error insert bankmastersync record with id: {} with error => {} ",
            bankMasterList.get(i).getSfid(), e.getMessage());
        Collections.swap(bankMasterList, i + 1, bankMasterList.size() - 1);
      }
    }
  }

  /**
   * @param bankMasterList
   * try catch: Sentry 連携しエラー通知を行う
   */
  public void updateBankMasterSync(List<BankMaster> bankMasterList) {
    Worker worker = workerController.setWorker(Constant.BANKMASTERSYNC);
    for (int i = 0; i < bankMasterList.size(); i++) {
      try {
        bankMasterMapper
            .updateBankMasterSync(ConvertDataUtil.convertBankMaster2Sync(bankMasterList.get(i)));
        worker.setSfid(bankMasterList.get(i).getSfid());
        // Syncテープルに更新場合：承認されたものは未承認変更。（Workerの「syncapproveflg 」に「TRUE」→「FALSE」）
        worker.setSycapproveflg(false);
        workerController.updateWorker(worker);
      } catch (Exception e) {
        LOGGER.error(
            Constant.NORMALCODE.E03
                + " >>> error update bankmastersync record with id: {} with error => {} ",
            bankMasterList.get(i).getSfid(), e.getMessage());
        Collections.swap(bankMasterList, i + 1, bankMasterList.size() - 1);
      }
    }
  }

  public List<BankMaster> getListBankMasterFromStockholm() {
    return bankMasterMapper.getListBankMasterFromStockholm(
        salesforceResponseController.parameterRequest(Constant.BANKMASTER));
  }

  public List<String> getListBankMasterIdFromStockholm() {
    return bankMasterMapper.getListBankMasterIdFromStockholm(
        salesforceResponseController.parameterRequest(Constant.BANKMASTER));
  }

  public List<String> getListBankMasterIdFromStockholm(List<BankMaster> bankMasterList) {
    List<String> listId = new ArrayList<>();
    for (int i = 0; i < bankMasterList.size(); i++) {
      listId.add(bankMasterList.get(i).getSfid());
    }
    return listId;
  }

  public List<String> getListBankMasterSyncIdFromStockholm() {
    return bankMasterMapper.getListBankMasterSyncIdFromStockholm();
  }

  public List<BankMaster> getListBankMasterToInsert(List<String> salesForceIds,
      List<String> stockholmIds, List<BankMaster> sfBankMastertList) {
    List<String> listIdToInsert = new ArrayList<>();
    if (stockholmIds.isEmpty()) {
      listIdToInsert = Utility.intersection(salesForceIds, stockholmIds);
    } else {
      listIdToInsert = Utility.difference(stockholmIds, salesForceIds);
    }
    List<BankMaster> listBankMasterToInsert = new ArrayList<>();
    if (!listIdToInsert.isEmpty()) {
      for (String sfid : listIdToInsert) {
        for (BankMaster bankMaster : sfBankMastertList) {
          if (bankMaster.getSfid().equalsIgnoreCase(sfid)) {
            listBankMasterToInsert.add(bankMaster);
          }
        }
      }
    }
    return listBankMasterToInsert;
  }

  public List<BankMaster> getListBankMasterToUpdate(List<String> salesForceIds,
      List<String> stockholmIds, List<BankMaster> sfBankMasterList) {
    List<String> listIdToUpdate = new ArrayList<>();
    if (!stockholmIds.isEmpty()) {
      listIdToUpdate = Utility.intersection(salesForceIds, stockholmIds);
    }
    List<BankMaster> listBankMasterToUpdate = new ArrayList<>();
    if (!listIdToUpdate.isEmpty()) {
      for (String sfid : listIdToUpdate) {
        for (BankMaster bankMaster : sfBankMasterList) {
          if (bankMaster.getSfid().equalsIgnoreCase(sfid)) {
            listBankMasterToUpdate.add(bankMaster);
          }
        }
      }
    }
    return listBankMasterToUpdate;
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

  public void insertBankAccountInformationSync(
      List<BankAccountInformation> bankAccountInformationList) {
    Worker worker = workerController.setWorker(Constant.BANKACCOUNTINFORMATIONSYNC);
    for (int i = 0; i < bankAccountInformationList.size(); i++) {
      try {
        bankAccountInforMapper.insertBankAccountInformationSync(
            ConvertDataUtil.convertBankAccountInformation2Sync(bankAccountInformationList.get(i)));
        worker.setSfid(bankAccountInformationList.get(i).getSfid());
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
    Worker worker = workerController.setWorker(Constant.BANKACCOUNTINFORMATIONSYNC);
    for (int i = 0; i < bankAccountInformationList.size(); i++) {
      try {
        bankAccountInforMapper.updateBankAccountInformationSync(
            ConvertDataUtil.convertBankAccountInformation2Sync(bankAccountInformationList.get(i)));
        worker.setSfid(bankAccountInformationList.get(i).getSfid());
        // Syncテープルに更新場合：承認されたものは未承認変更。（Workerの「syncapproveflg」に「TRUE」→「FALSE」）
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

  public List<BankAccountInformation> getListBankAccountInformationFromStockholm() {
    return bankAccountInforMapper.getListBankAccountInformationFromStockholm(
        salesforceResponseController.parameterRequest(Constant.BANKACCOUNTINFORMATION));
  }

  public List<String> getListBankAccountInformationIdFromStockholm() {
    return bankAccountInforMapper.getListBankAccountInformationIdFromStockholm(
        salesforceResponseController.parameterRequest(Constant.BANKACCOUNTINFORMATION));
  }

  public List<String> getListBankAccountInformationIdFromStockholm(
      List<BankAccountInformation> bankAccountInforList) {
    List<String> listId = new ArrayList<>();
    for (int i = 0; i < bankAccountInforList.size(); i++) {
      listId.add(bankAccountInforList.get(i).getSfid());
    }
    return listId;
  }

  public List<String> getListBankAccountInformationSyncIdFromStockholm() {
    return bankAccountInforMapper.getListBankAccountInformationSyncIdFromStockholm();
  }

  public List<BankAccountInformation> getListBankAccountInformationToInsert(
      List<String> salesForceIds, List<String> stockholmIds,
      List<BankAccountInformation> sfBankAccountInformationList) {
    List<String> listIdToInsert = new ArrayList<>();
    if (stockholmIds.isEmpty()) {
      listIdToInsert = Utility.intersection(salesForceIds, stockholmIds);
    } else {
      listIdToInsert = Utility.difference(stockholmIds, salesForceIds);
    }
    List<BankAccountInformation> listBankAccountInformationToInsert = new ArrayList<>();
    if (!listIdToInsert.isEmpty()) {
      for (String sfid : listIdToInsert) {
        for (BankAccountInformation bankAccountInformation : sfBankAccountInformationList) {
          if (bankAccountInformation.getSfid().equalsIgnoreCase(sfid)) {
            listBankAccountInformationToInsert.add(bankAccountInformation);
          }
        }
      }
    }
    return listBankAccountInformationToInsert;
  }

  public List<BankAccountInformation> getListBankAccountInformationToUpdate(
      List<String> salesForceIds, List<String> stockholmIds,
      List<BankAccountInformation> sfBankAccountInformationList) {
    List<String> listIdToUpdate = new ArrayList<>();
    if (!stockholmIds.isEmpty()) {
      listIdToUpdate = Utility.intersection(salesForceIds, stockholmIds);
    }
    List<BankAccountInformation> listBankAccountInformationToUpdate = new ArrayList<>();
    if (!listIdToUpdate.isEmpty()) {
      for (String sfid : listIdToUpdate) {
        for (BankAccountInformation bankAccountInformation : sfBankAccountInformationList) {
          if (bankAccountInformation.getSfid().equalsIgnoreCase(sfid)) {
            listBankAccountInformationToUpdate.add(bankAccountInformation);
          }
        }
      }
    }
    return listBankAccountInformationToUpdate;
  }
  // End BankAccountInformation
}
