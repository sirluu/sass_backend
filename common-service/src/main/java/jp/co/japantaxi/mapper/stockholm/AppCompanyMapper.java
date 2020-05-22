package jp.co.japantaxi.mapper.stockholm;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import jp.co.japantaxi.model.AppCompany;
import jp.co.japantaxi.model.ParameterRequest;
import jp.co.japantaxi.response.AppCompanyCoopApprovalBrooklyn;
import jp.co.japantaxi.response.AppCompanyResponse;
import jp.co.japantaxi.response.AppCompanyResponse.AppCompanyList;

public interface AppCompanyMapper {

  AppCompany getAppCompanyById(ParameterRequest id);

  List<AppCompany> getListAppCompanyFromStockholm(ParameterRequest ids);

  List<AppCompany> getListAppCompany2Sync(ParameterRequest startTime);

  List<String> getListAppCompanyIdFromStockholm(ParameterRequest ids);

  List<String> getListAppCompanySyncIdFromStockholm();

  void insertAppCompany(AppCompany appCompany);

  void updateAppCompany(AppCompany appCompany);

  void insertAppCompanySync(AppCompany appCompany);

  void updateAppCompanySync(AppCompany appCompany);

  void truncateAppCompany();

  // Singapore
  List<AppCompanyList> getListAppCompanys(@Param("sfids") List<String> sfids);

  AppCompanyResponse getAppCompanyDetailByCompanyId(@Param("sfid") String sfid);
 
  AppCompanyCoopApprovalBrooklyn getAppCompanyCoopApprovalInfoByCompanyId(@Param("sfid") String sfid);

  AppCompanyResponse.AppCompanyCoopApproval getAppCompanyCoopApprovalByDetailBySfid(@Param("sfid") String sfid); 
  // End Singapore
}