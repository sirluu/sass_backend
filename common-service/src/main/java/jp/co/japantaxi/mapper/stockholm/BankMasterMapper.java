package jp.co.japantaxi.mapper.stockholm;

import java.util.List;
import jp.co.japantaxi.model.BankMaster;
import jp.co.japantaxi.model.ParameterRequest;

public interface BankMasterMapper {

  BankMaster getBankMasterById(ParameterRequest id);

  List<BankMaster> getListBankMasterFromStockholm(ParameterRequest ids);

  List<BankMaster> getListBankMaster2Sync(ParameterRequest startTime);

  List<String> getListBankMasterIdFromStockholm(ParameterRequest ids);

  List<String> getListBankMasterSyncIdFromStockholm();

  void insertBankMaster(BankMaster bankMaster);

  void updateBankMaster(BankMaster bankMaster);

  void insertBankMasterSync(BankMaster bankMaster);

  void updateBankMasterSync(BankMaster bankMaster);

  void truncateBankMaster();

}
