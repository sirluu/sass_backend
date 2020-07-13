package jp.co.japantaxi.mapper.stockholm;

import java.util.List;
import jp.co.japantaxi.model.BankMaster;
import jp.co.japantaxi.model.ParameterRequest;

public interface BankMasterMapper {

  BankMaster getBankMasterById(ParameterRequest id);

  List<String> getListBankMasterIdFromStockholm(ParameterRequest ids);

  List<String> getListBankMasterIds(ParameterRequest request);

  List<BankMaster> getListBankMaster(ParameterRequest request);

  List<BankMaster> getListBankMasterSync(ParameterRequest request);

  Integer countBankMaster(ParameterRequest request);

  void insertBankMaster(BankMaster bankMaster);

  void updateBankMaster(BankMaster bankMaster);

  void insertBankMasterSync(BankMaster bankMaster);

  void updateBankMasterSync(BankMaster bankMaster);

  void truncateBankMaster();

}
