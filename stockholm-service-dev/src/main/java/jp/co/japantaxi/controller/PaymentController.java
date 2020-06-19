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
       Map<String, PaymentSystemLinkInfor> hashMap = new HashMap<>();
      try {
          List<PaymentSystemLinkInfor> sfPaymentSystemLinkInforList =
              salesforceResponseController.getListPaymentSystemLinkInforFromSalesforce(batchStatus);
          if (!sfPaymentSystemLinkInforList.isEmpty()) {
            List<String> sfPaymentSystemLinkInforIds = Utility.getIdListFromObjectList(sfPaymentSystemLinkInforList);
            List<String> stPaymentSystemLinkInforIds =
                getListPaymentSystemLinkInforIdFromStockholm(sfPaymentSystemLinkInforIds);
           //Add to HashMap
            for (PaymentSystemLinkInfor obj : sfPaymentSystemLinkInforList) {
            	hashMap.put(obj.getSfid(), obj);
            }
            
            List<PaymentSystemLinkInfor> linkInforsToUpdate =
            		getListPaymentSystemLinkInforToUpdate(stPaymentSystemLinkInforIds, hashMap);
            List<PaymentSystemLinkInfor> linkInforsToInsert = new ArrayList<PaymentSystemLinkInfor>(hashMap.values());
            
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
    public void coreDateCreatPaymentSystemLinkInfor(ParameterRequest parareq, BatchStatus batchStatus) {
        List<String> objectIds = new ArrayList<>();
        List<PaymentSystemLinkInfor> objects2Insert = new ArrayList<>();
        List<PaymentSystemLinkInfor> objects2Update = new ArrayList<>();
        List<PaymentSystemLinkInfor> compareList = new ArrayList<>();
        Map<String, PaymentSystemLinkInfor> hashMap = new HashMap<>();
      try {
    	// Read the datasync file once only
        JsonMapper.readDataSync(Constant.PAYMENTSYSTEMLINKINFOR);
        parareq.setLimit(Constant.LIMIT);
        Integer count = linkInforMapper.countPaymentSystemLinkInfor(parareq);
        int offset = count / Constant.LIMIT;
        List<PaymentSystemLinkInfor> objectList = new ArrayList<>();
        for (int i = 0; i <= offset; i++) {
          parareq.setOffset(i*Constant.LIMIT);
          objectList = linkInforMapper.getListPaymentSystemLinkInfor(parareq);
          if (i < offset) {
            LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from {} to {}",
                Constant.PAYMENTSYSTEMLINKINFOR, Constant.PAYMENTSYSTEMLINKINFORSYNC,
                Constant.LIMIT * i, Constant.LIMIT * (i + 1));
          } else if (i == offset) {
            LOGGER.info("CoreDateCreat >>> {} sync to {} >>> from record {} to {}",
                    Constant.PAYMENTSYSTEMLINKINFOR, Constant.PAYMENTSYSTEMLINKINFORSYNC,
                    Constant.LIMIT * offset, count);
          }

          if(objectList.size() == 0) return;
          //Add to HashMap
          for (PaymentSystemLinkInfor obj : objectList) {
          	hashMap.put(obj.getSfid(), obj);
          	objectIds.add(obj.getSfid());
          }
          //Request parameter
          //objectIds = Utility.getIdListFromObjectList(objectList);
          parareq.setIds(Utility.convertList(objectIds));
          parareq.setChecked(true);
          //Insert
          objects2Insert = selectLinkInforSyncList2InsertOrUpdate(hashMap, parareq, true);
          if (!objects2Insert.isEmpty()) {
        	  insertPaymentSystemLinkInforSync(objects2Insert);
          }
          //Reduce SFID to query
          objectIds = new ArrayList<String>(hashMap.keySet());
          parareq.setIds(Utility.convertList(objectIds));
          parareq.setChecked(false);
          
          compareList = linkInforMapper.getListPaymentSystemLinkInforSync(parareq);
          objects2Update = selectLinkInforSyncList2InsertOrUpdate(hashMap, parareq, false);
          objects2Update = getLinkInfoSyncListEdited(objects2Update, compareList, hashMap);
          if (!objects2Update.isEmpty()) {
        	  updatePaymentSystemLinkInforSync(objects2Update);
          }
          parareq.setIds(null);
          hashMap.clear();
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
		Worker worker = workerController.setWorker(Constant.APPCOMPANYSYNC);
		for (int i = 0; i < linkInfors.size(); i++) {
			try {
				linkInforMapper.insertPaymentSystemLinkInforSync(
						ConvertDataUtil.convertPaymentSystemLinkInfor2Sync(linkInfors.get(i), true));
				worker.setSfid(linkInfors.get(i).getAppcompany());
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
		Worker worker = workerController.setWorker(Constant.APPCOMPANYSYNC);
		for (int i = 0; i < linkInfors.size(); i++) {
			try {
				linkInforMapper.updatePaymentSystemLinkInforSync(
						ConvertDataUtil.convertPaymentSystemLinkInfor2Sync(linkInfors.get(i), true));
		        LOGGER.info("PaymentSystemLinkInforSync updating >>> " + linkInfors.get(i).getSfid());
				worker.setSfid(linkInfors.get(i).getAppcompany());
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

    public List<PaymentSystemLinkInfor> getListPaymentSystemLinkInforToUpdate(List<String> stockholmIds,
  		  Map<String, PaymentSystemLinkInfor> hashMap) {
  	List<PaymentSystemLinkInfor> listObjToUpdate = new ArrayList<>();
      if (!stockholmIds.isEmpty()) {
        for (String sfid : stockholmIds) {
      	  listObjToUpdate.add(hashMap.get(sfid));
      	  hashMap.remove(sfid);//Remove update obj
        }
      }
      return listObjToUpdate;
    }
	  
    public List<PaymentSystemLinkInfor> getLinkInforListFromIdList(List<String> ids,
        List<PaymentSystemLinkInfor> objectList) {
      List<PaymentSystemLinkInfor> list = new ArrayList<>();
      for (int i = 0; i < ids.size(); i++) {
        for (int j = 0; j < objectList.size(); j++) {
          if (ids.get(i).equalsIgnoreCase(objectList.get(j).getSfid())) {
            list.add(i, objectList.get(j));
          }
        }
      }
      return list;
    }
    
    public List<PaymentSystemLinkInfor> selectLinkInforSyncList2InsertOrUpdate(Map<String, PaymentSystemLinkInfor> hashMap,
  		  ParameterRequest parareq, boolean rmFlg ) {
  	List<String> listIdToInUp= new ArrayList<>();
  	List<PaymentSystemLinkInfor> listAccToInUp = new ArrayList<>();
      //Get List SFID to insert
  	listIdToInUp = linkInforMapper.getListPaymentSystemLinkInforIds(parareq);
      if (!listIdToInUp.isEmpty()) {
        for (String sfid : listIdToInUp) {
      	  listAccToInUp.add(hashMap.get(sfid));
      	  if (!rmFlg) continue;
      	  hashMap.remove(sfid);//For Reduce memory
        }
      }
      return listAccToInUp;
    }
      
    public List<PaymentSystemLinkInfor> getLinkInfoSyncListEdited( List<PaymentSystemLinkInfor> objectList,
  		  List<PaymentSystemLinkInfor> compareList, Map<String, PaymentSystemLinkInfor> hashMap) {
      List<String> ids2Update =  Utility.compare(convertLinkInfoSyncList(objectList), compareList);
      List<PaymentSystemLinkInfor> listObject2Update = new ArrayList<>();
      if (!ids2Update.isEmpty()) {
        for (String sfid : ids2Update) {
      	  listObject2Update.add(hashMap.get(sfid));
      	  hashMap.remove(sfid);//For Reduce memory
        }
      }
      return listObject2Update;
    }
      
    public static List<PaymentSystemLinkInfor> convertLinkInfoSyncList(
        List<PaymentSystemLinkInfor> objectList) {
      List<PaymentSystemLinkInfor> list = new ArrayList<>();
      PaymentSystemLinkInfor element = new PaymentSystemLinkInfor();
      for (int i = 0; i < objectList.size(); i++) {
        element = ConvertDataUtil.convertPaymentSystemLinkInfor2Sync(objectList.get(i), false);
        list.add(element);
      }
      return list;
    }
    // End PaymentSystemLinkInfor
}
