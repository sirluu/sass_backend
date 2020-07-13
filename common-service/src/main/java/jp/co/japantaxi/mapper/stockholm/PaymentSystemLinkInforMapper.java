package jp.co.japantaxi.mapper.stockholm;

import java.util.List;
import jp.co.japantaxi.model.PaymentSystemLinkInfor;
import jp.co.japantaxi.model.ParameterRequest;

public interface PaymentSystemLinkInforMapper {

  PaymentSystemLinkInfor getPaymentSystemLinkInforById(ParameterRequest id);

  List<String> getListPaymentSystemLinkInforIdFromStockholm(ParameterRequest ids);

  List<String> getListPaymentSystemLinkInforIds(ParameterRequest request);

  List<PaymentSystemLinkInfor> getListPaymentSystemLinkInfor(ParameterRequest request);

  List<PaymentSystemLinkInfor> getListPaymentSystemLinkInforSync(ParameterRequest request);

  Integer countPaymentSystemLinkInfor(ParameterRequest request);

  void insertPaymentSystemLinkInfor(PaymentSystemLinkInfor linkInforc);

  void updatePaymentSystemLinkInfor(PaymentSystemLinkInfor linkInforc);

  void insertPaymentSystemLinkInforSync(PaymentSystemLinkInfor linkInforc);

  void updatePaymentSystemLinkInforSync(PaymentSystemLinkInfor linkInforc);

  void truncatePaymentSystemLinkInfor();

}
