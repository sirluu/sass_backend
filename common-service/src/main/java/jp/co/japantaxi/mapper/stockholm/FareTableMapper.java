package jp.co.japantaxi.mapper.stockholm;

import java.util.List;
import jp.co.japantaxi.model.FareTable;
import jp.co.japantaxi.model.ParameterRequest;

public interface FareTableMapper {

  FareTable getFareTableById(ParameterRequest id);

  List<FareTable> getListFareTableFromStockholm(ParameterRequest ids);

  List<FareTable> getListFareTableSyncFromStockholm(ParameterRequest ids);

  List<FareTable> getListFareTable2Sync(ParameterRequest startTime);

  List<String> getListFareTableIdFromStockholm(ParameterRequest ids);

  List<String> getListFareTableSyncIdFromStockholm();

  void insertFareTable(FareTable fareTable);

  void updateFareTable(FareTable fareTable);

  void insertFareTableSync(FareTable fareTable);

  void updateFareTableSync(FareTable fareTable);

  void truncateFareTable();

}
