package jp.co.japantaxi.mapper.stockholm;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import jp.co.japantaxi.model.BankAccountInformation;
import jp.co.japantaxi.model.ParameterRequest;
import jp.co.japantaxi.response.MerchantResponse.BankAccountFielsBKMapper;

public interface BankAccountInformationMapper {

  BankAccountInformation getBankAccountInformationById(ParameterRequest id);

  List<BankAccountInformation> getBankAccountByBankIds(ParameterRequest ids);

  List<String> getListBankAccountInformationIdFromStockholm(ParameterRequest ids);

  List<String> getListBankAccountInformationIds(ParameterRequest request);

  List<BankAccountInformation> getListBankAccountInformation(ParameterRequest request);

  List<BankAccountInformation> getListBankAccountInformationSync(ParameterRequest request);

  Integer countBankAccountInformation(ParameterRequest request);

  void insertBankAccountInformation(BankAccountInformation bankAccountInformation);

  void updateBankAccountInformation(BankAccountInformation bankAccountInformation);

  void insertBankAccountInformationSync(BankAccountInformation bankAccountInformation);

  void updateBankAccountInformationSync(BankAccountInformation bankAccountInformation);

  void truncateBankAccountInformation();

  List<BankAccountInformation> getListBankAccInfo();

  List<BankAccountFielsBKMapper> getListBankAccountInfoByAccountId(
      @Param("accountId") String accountId, @Param("accountCategory") String accountCategory);
}
