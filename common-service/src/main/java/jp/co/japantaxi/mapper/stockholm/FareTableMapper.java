package jp.co.japantaxi.mapper.stockholm;

import java.util.List;
import jp.co.japantaxi.model.FareTable;
import jp.co.japantaxi.model.ParameterRequest;

public interface FareTableMapper {

  FareTable getFareTableById(ParameterRequest id);

  List<String> getListFareTableIdFromStockholm(ParameterRequest ids);

  List<String> getListFareTableIds(ParameterRequest request);

  List<FareTable> getListFareTable(ParameterRequest request);

  List<FareTable> getListFareTableSync(ParameterRequest request);

  Integer countFareTable(ParameterRequest request);

  void insertFareTable(FareTable fareTable);

  void updateFareTable(FareTable fareTable);

  void insertFareTableSync(FareTable fareTable);

  void updateFareTableSync(FareTable fareTable);

  void truncateFareTable();

}
