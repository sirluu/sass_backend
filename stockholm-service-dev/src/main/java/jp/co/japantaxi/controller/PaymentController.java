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
import jp.co.japantaxi.mapper.stockholm.PaymentSystemLinkInforMapper;
import jp.co.japantaxi.model.BatchStatus;
import jp.co.japantaxi.model.ParameterRequest;
import jp.co.japantaxi.model.PaymentSystemLinkInfor;
import jp.co.japantaxi.model.Worker;
import jp.co.japantaxi.utils.Constant;
import jp.co.japantaxi.utils.ConvertDataUtil;
import jp.co.japantaxi.utils.JsonMapper;
import jp.co.japantaxi.utils.Utility;

@RestController
@RequestMapping
public class PaymentController {

	static final Logger LOGGER = LoggerFactory.getLogger(PaymentController.class);

	@Autowired
	public CacheManagerConfig cacheManagerConfig;

	@Autowired
	public WorkerController workerController;

	@Autowired
	public SalesforceResponseController salesforceResponseController;

	@Autowired
	public BatchController batchController;

	@Autowired
	public PaymentSystemLinkInforMapper linkInforMapper;

    /**
     * @param parameterRequest
     * @param batchStatus 
     * try catch: BACK_REG [テーブル名（データ加工後のDB登録時にエラーになったテーブル名）]
     * try catch: Sentry 連携しエラー通知を行う
     */
    public void getSFPaymentSystemLinkInfor(ParameterRequest parameterRequest,
        BatchStatus batchStatus) {
      try {
        int error = cacheManagerConfig.getErrorCode(Constant.PAYMENTSYSTEMLINKINFOR.toLowerCase());
        if (Constant.checkError(error) == null) {
          List<PaymentSystemLinkInfor> sfPaymentSystemLinkInforList =
              salesforceResponseController.getListPaymentSystemLinkInforFromSalesforce(batchStatus);
          if (!sfPaymentSystemLinkInforList.isEmpty()) {
            List<String> sfPaymentSystemLinkInforIds =
                cacheManagerConfig.getListObjectId(Constant.PAYMENTSYSTEMLINKINFOR);
            cacheManagerConfig.clearMap(Constant.PAYMENTSYSTEMLINKINFOR);
            List<String> stPaymentSystemLinkInforIds =
                getListPaymentSystemLinkInforIdFromStockholm(sfPaymentSystemLinkInforIds);
            List<PaymentSystemLinkInfor> linkInforsToInsert =
                getListPaymentSystemLinkInforToInsert(sfPaymentSystemLinkInforIds,
                    stPaymentSystemLinkInforIds, sfPaymentSystemLinkInforList);
            List<PaymentSystemLinkInfor> linkInforsToUpdate =
                getListPaymentSystemLinkInforToUpdate(sfPaymentSystemLinkInforIds,
                    stPaymentSystemLinkInforIds, sfPaymentSystemLinkInforList);
            if (!linkInforsToInsert.isEmpty()) {
              insertPaymentSystemLinkInfor(linkInforsToInsert);
            }
            if (!linkInforsToUpdate.isEmpty()) {
              updatePaymentSystemLinkInfor(linkInforsToUpdate);
            }
          }
          String nptk = cacheManagerConfig.getNextPageToken("next_page_token");
          if (nptk != null) {
            getSFPaymentSystemLinkInfor(parameterRequest, batchStatus);
          }
          cacheManagerConfig.clearNextPageToken();
        }
      } catch (Exception ex) {
        workerController.commonError(Constant.SF_REG + Constant.PAYMENTSYSTEMLINKINFOR, batchStatus,
            ex);
      }
    }
	  
  /**
   * @param parameterRequest
   * @param batchStatus
   * try catch: BACK_REG [テーブル名（データ加工後のDB登録時にエラーになったテーブル名）] 
   * try catch: Sentry 連携しエラー通知を行う
   */
	public void coreDateCreatPaymentSystemLinkInfor(ParameterRequest parameterRequest, BatchStatus batchStatus) {
      try {
        ParameterRequest parareq = new ParameterRequest();
        parareq.setStartTime(Utility.parseString(parameterRequest.getStartTime()));
        List<PaymentSystemLinkInfor> objectSyncList =
            linkInforMapper.getListPaymentSystemLinkInfor2Sync(parareq);
        List<String> objectIds = new ArrayList<>();

        int size = objectSyncList.size();
        int offset = size / Constant.LIMIT;
        // ファイルを1回だけ読み取る
        // Read the datasync file once only
        JsonMapper.readDataSync(Constant.PAYMENTSYSTEMLINKINFOR);
        List<PaymentSystemLinkInfor> syncList = new ArrayList<>();
        List<PaymentSystemLinkInfor> brooklynList = new ArrayList<PaymentSystemLinkInfor>();
        for (int i = 0; i <= offset; i++) {
          if (i < offset) {
            syncList = objectSyncList.subList(Constant.LIMIT * i, Constant.LIMIT * (i + 1));
            LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to record {}",
                Constant.PAYMENTSYSTEMLINKINFOR, Constant.PAYMENTSYSTEMLINKINFORSYNC,
                Constant.LIMIT * i, Constant.LIMIT * (i + 1));
          } else if (i == offset) {
            syncList = objectSyncList.subList(Constant.LIMIT * offset, size);
            LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to record {}",
                Constant.PAYMENTSYSTEMLINKINFOR, Constant.PAYMENTSYSTEMLINKINFORSYNC,
                Constant.LIMIT * offset, size);
          }
          objectIds = Utility.getIdListFromObjetcList(syncList);
          if (objectIds != null) {
            List<String> objectSyncIds = getListPaymentSystemLinkInforSyncIdFromStockholm();
            List<PaymentSystemLinkInfor> objectListToInsert =
                getListPaymentSystemLinkInforToInsert(objectIds, objectSyncIds, syncList);
            if (!objectListToInsert.isEmpty()) {
              insertPaymentSystemLinkInforSync(objectListToInsert);
            }
          }
          parareq.setIds(Utility.parseList(objectIds));
          brooklynList = linkInforMapper.getListPaymentSystemLinkInforSyncFromStockholm(parareq);
          List<PaymentSystemLinkInfor> objectListToUpdate = getListPaymentSystemLinkInforToUpdateSync(syncList, brooklynList);
          if (!objectListToUpdate.isEmpty()) {
            updatePaymentSystemLinkInforSync(objectListToUpdate);
          }
        }
      } catch (Exception ex) {
        workerController.commonError(Constant.BACK_REG + Constant.PAYMENTSYSTEMLINKINFORSYNC, batchStatus, ex);
      }
	}

	// Begin BankAccountInformation
	/**
	 * @param linkInfors try catch: Sentry 連携しエラー通知を行う
	 */
	public void insertPaymentSystemLinkInfor(List<PaymentSystemLinkInfor> linkInfors) {
		for (int i = 0; i < linkInfors.size(); i++) {
			try {
				linkInforMapper.insertPaymentSystemLinkInfor(linkInfors.get(i));
			} catch (Exception e) {
				LOGGER.error(
						Constant.NORMALCODE.E03
								+ " >>> error insert paymentsystemlinkinfor record with id: {} with error => {} ",
						linkInfors.get(i).getSfid(), e.getMessage());
				Collections.swap(linkInfors, i + 1, linkInfors.size() - 1);
			}
		}
	}

	/**
	 * @param linkInfors try catch: Sentry 連携しエラー通知を行う
	 */
	public void updatePaymentSystemLinkInfor(List<PaymentSystemLinkInfor> linkInfors) {
		for (int i = 0; i < linkInfors.size(); i++) {
			try {
				linkInforMapper.updatePaymentSystemLinkInfor(linkInfors.get(i));
			} catch (Exception e) {
				LOGGER.error(
						Constant.NORMALCODE.E03
								+ " >>> error update paymentsystemlinkinfor record with id: {} with error => {} ",
						linkInfors.get(i).getSfid(), e.getMessage());
				Collections.swap(linkInfors, i + 1, linkInfors.size() - 1);
			}
		}
	}

	/**
	 * @param linkInfors try catch: Sentry 連携しエラー通知を行う
	 */
	public void insertPaymentSystemLinkInforSync(List<PaymentSystemLinkInfor> linkInfors) {
		Worker worker = workerController.setWorker(Constant.PAYMENTSYSTEMLINKINFORSYNC);
		for (int i = 0; i < linkInfors.size(); i++) {
			try {
				linkInforMapper.insertPaymentSystemLinkInforSync(
						ConvertDataUtil.convertPaymentSystemLinkInfor2Sync(linkInfors.get(i), true));
				worker.setSfid(linkInfors.get(i).getSfid());
			} catch (Exception e) {
				LOGGER.error(
						Constant.NORMALCODE.E03
								+ " >>> error insert paymentsystemlinkinforsync record with id: {} with error => {} ",
						linkInfors.get(i).getSfid(), e.getMessage());
				Collections.swap(linkInfors, i + 1, linkInfors.size() - 1);
			}
			workerController.insertWorker(worker);
		}
	}

	/**
	 * @param linkInfors try catch: Sentry 連携しエラー通知を行う
	 */
	public void updatePaymentSystemLinkInforSync(List<PaymentSystemLinkInfor> linkInfors) {
		Worker worker = workerController.setWorker(Constant.PAYMENTSYSTEMLINKINFORSYNC);
		for (int i = 0; i < linkInfors.size(); i++) {
			try {
				linkInforMapper.updatePaymentSystemLinkInforSync(
						ConvertDataUtil.convertPaymentSystemLinkInfor2Sync(linkInfors.get(i), true));
		        LOGGER.info("PaymentSystemLinkInforSync updating >>> " + linkInfors.get(i).getSfid());
				worker.setSfid(linkInfors.get(i).getSfid());
				// Syncテープルに更新場合：承認されたものは未承認変更。（Workerの「sycapproveflg」に「TRUE」→「FALSE」）
				worker.setSycapproveflg(false);
				workerController.updateWorker(worker);
			} catch (Exception e) {
				LOGGER.error(
						Constant.NORMALCODE.E03
								+ " >>> error update paymentsystemlinkinforsync record with id: {} with error => {} ",
						linkInfors.get(i).getSfid(), e.getMessage());
				Collections.swap(linkInfors, i + 1, linkInfors.size() - 1);
			}
		}
	}

    public List<String> getListPaymentSystemLinkInforIdFromStockholm(List<String> objectIds) {
      ParameterRequest parareq = new ParameterRequest();
      parareq.setIds(Utility.parseList(objectIds));
      return linkInforMapper.getListPaymentSystemLinkInforIdFromStockholm(parareq);
    }

	public List<String> getListPaymentSystemLinkInforSyncIdFromStockholm() {
		return linkInforMapper.getListPaymentSystemLinkInforSyncIdFromStockholm();
	}

	public List<PaymentSystemLinkInfor> getListPaymentSystemLinkInforToInsert(List<String> salesForceIds,
			List<String> stockholmIds, List<PaymentSystemLinkInfor> sfPaymentSystemLinkInforList) {
		List<String> listIdToInsert = new ArrayList<>();
		if (stockholmIds.isEmpty()) {
			listIdToInsert = Utility.intersection(salesForceIds, stockholmIds);
		} else {
			listIdToInsert = Utility.difference(stockholmIds, salesForceIds);
		}
		List<PaymentSystemLinkInfor> listPaymentSystemLinkInforToInsert = new ArrayList<>();
		if (!listIdToInsert.isEmpty()) {
			for (String sfid : listIdToInsert) {
				for (PaymentSystemLinkInfor linkInfor : sfPaymentSystemLinkInforList) {
					if (linkInfor.getSfid().equalsIgnoreCase(sfid)) {
						listPaymentSystemLinkInforToInsert.add(linkInfor);
					}
				}
			}
		}
		return listPaymentSystemLinkInforToInsert;
	}

    public List<PaymentSystemLinkInfor> getListPaymentSystemLinkInforToUpdate(
        List<String> salesForceIds, List<String> stockholmIds,
        List<PaymentSystemLinkInfor> sfPaymentSystemLinkInforList) {
      List<String> listIdToUpdate = new ArrayList<>();
      if (!stockholmIds.isEmpty()) {
        listIdToUpdate = Utility.intersection(salesForceIds, stockholmIds);
      }
      List<PaymentSystemLinkInfor> listPaymentSystemLinkInforToUpdate = new ArrayList<>();
      if (!listIdToUpdate.isEmpty()) {
        for (String sfid : listIdToUpdate) {
          for (PaymentSystemLinkInfor linkInfor : sfPaymentSystemLinkInforList) {
            if (linkInfor.getSfid().equalsIgnoreCase(sfid)) {
              listPaymentSystemLinkInforToUpdate.add(linkInfor);
            }
          }
        }
      }
      return listPaymentSystemLinkInforToUpdate;
    }
	  
    public List<PaymentSystemLinkInfor> getListPaymentSystemLinkInforToUpdateSync(
        List<PaymentSystemLinkInfor> sflinkInforList, List<PaymentSystemLinkInfor> stlinkInforList) {
      List<String> listIdToUpdate =
          Utility.compare(updatePaymentSystemLinkInforListSync(sflinkInforList), stlinkInforList);
      List<PaymentSystemLinkInfor> listPaymentSystemLinkInforToUpdate = new ArrayList<>();
      if (!listIdToUpdate.isEmpty()) {
        for (String sfid : listIdToUpdate) {
          for (PaymentSystemLinkInfor bbb : sflinkInforList) {
            if (bbb.getSfid().equalsIgnoreCase(sfid)) {
              listPaymentSystemLinkInforToUpdate.add(bbb);
            }
          }
        }
      }
      return listPaymentSystemLinkInforToUpdate;
    }
	  
    public static List<PaymentSystemLinkInfor> updatePaymentSystemLinkInforListSync(
        List<PaymentSystemLinkInfor> linkInforList) {
      List<PaymentSystemLinkInfor> list = new ArrayList<>();
      PaymentSystemLinkInfor element = new PaymentSystemLinkInfor();
      for (int i = 0; i < linkInforList.size(); i++) {
        element = ConvertDataUtil.convertPaymentSystemLinkInfor2Sync(linkInforList.get(i), false);
        list.add(element);
      }
      return list;
    }
	// End PaymentSystemLinkInfor
}
