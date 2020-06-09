package jp.co.japantaxi.mapper.stockholm;

import java.util.List;
import jp.co.japantaxi.model.PaymentSystemLinkInfor;
import jp.co.japantaxi.model.ParameterRequest;

public interface PaymentSystemLinkInforMapper {

  PaymentSystemLinkInfor getPaymentSystemLinkInforById(ParameterRequest id);

  List<PaymentSystemLinkInfor> getListPaymentSystemLinkInforFromStockholm(ParameterRequest ids);

  List<PaymentSystemLinkInfor> getListPaymentSystemLinkInforSyncFromStockholm(ParameterRequest ids);

  List<PaymentSystemLinkInfor> getListPaymentSystemLinkInfor2Sync(ParameterRequest startTime);

  List<String> getListPaymentSystemLinkInforIdFromStockholm(ParameterRequest ids);

  List<String> getListPaymentSystemLinkInforSyncIdFromStockholm();

  void insertPaymentSystemLinkInfor(PaymentSystemLinkInfor linkInforc);

  void updatePaymentSystemLinkInfor(PaymentSystemLinkInfor linkInforc);

  void insertPaymentSystemLinkInforSync(PaymentSystemLinkInfor linkInforc);

  void updatePaymentSystemLinkInforSync(PaymentSystemLinkInfor linkInforc);

  void truncatePaymentSystemLinkInfor();

}
