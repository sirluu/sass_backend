package jp.co.japantaxi.mapper.stockholm;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import jp.co.japantaxi.model.AppCompany;
import jp.co.japantaxi.model.ParameterRequest;
import jp.co.japantaxi.response.AppCompanyCoopApprovalBrooklyn;
import jp.co.japantaxi.response.AppCompanyResponse;
import jp.co.japantaxi.response.AppCompanyResponse.AppCompanyList;

public interface AppCompanyMapper {

  AppCompany getAppCompanyById(ParameterRequest id);

  List<String> getListAppCompanyIdFromStockholm(ParameterRequest ids);

  List<String> getListAppCompanyIds(ParameterRequest request);

  List<AppCompany> getListAppCompany(ParameterRequest request);

  List<AppCompany> getListAppCompanySync(ParameterRequest request);

  Integer countAppCompany(ParameterRequest request);

  void insertAppCompany(AppCompany appCompany);

  void updateAppCompany(AppCompany appCompany);

  void insertAppCompanySync(AppCompany appCompany);

  void updateAppCompanySync(AppCompany appCompany);

  void truncateAppCompany();

  // Singapore
  @Select("select count(a.sfid) FROM appcompany a INNER JOIN worker w on a.sfid = w.sfid")
  int getTotalAppCompanys();

  List<AppCompanyList> getListAppCompanys(@Param("jtxCompanyId") Integer jtxCompanyId,
      @Param("limit") Integer limit, @Param("flg") Boolean flg);

  AppCompanyResponse getAppCompanyDetailByCompanyId(@Param("sfid") String sfid);

  AppCompanyCoopApprovalBrooklyn getAppCompanyCoopApprovalInfoByCompanyId(
      @Param("sfid") String sfid);

  AppCompanyResponse.AppCompanyCoopApproval getAppCompanyCoopApprovalByDetailBySfid(
      @Param("sfid") String sfid);

  @Select("SELECT\r\n" + "    a.sfid as accountid,\r\n" + "        payment.sfid as paymentid,\r\n"
      + "        (select sfid from worker wf WHERE bankacc.sfid = wf.sfid and wf.sycapproveflg = 'F') as bankaccid,\r\n"
      + "        bm.sfid as bmid,\r\n"
      + "        (select sfid from worker wf WHERE netpaybankacc.sfid = wf.sfid and wf.sycapproveflg = 'F') as netpaybankaccid,\r\n"
      + "        netpaybm.sfid as netpaybmid,\r\n" + "    CASE\r\n"
      + "            WHEN (payment.sfid is null OR bankacc.sfid is null OR bm.sfid is null OR netpaybankacc.sfid is null OR netpaybm.sfid is null OR (select count(sfid) from worker wf WHERE a.sfid = wf.sfid and wf.sycapproveflg = 'F') > 0) THEN 0 ELSE 1 END AS isneedapproval\r\n"
      + "    FROM accountsync a\r\n"
      + "        LEFT JOIN paymentsystemlinkinforsync payment ON a.sfid = payment.internetpaymerstore AND payment.sfid = (select sfid from worker wf WHERE payment.sfid = wf.sfid and wf.sycapproveflg = 'F')\r\n"
      + "        LEFT JOIN bankaccountinformationsync bankacc ON a.sfid = bankacc.account AND bankacc.accountcategory = 'ITAB決済支払' AND bankacc.sfid = (select sfid from worker wf WHERE bankacc.sfid = wf.sfid)\r\n"
      + "        LEFT JOIN bankmastersync bm ON bankacc.accountbank = bm.sfid AND bm.sfid = (select sfid from worker wf WHERE bm.sfid = wf.sfid and wf.sycapproveflg = 'F')\r\n"
      + "        \r\n"
      + "            LEFT JOIN bankaccountinformationsync netpaybankacc ON a.sfid = netpaybankacc.account AND netpaybankacc.accountcategory = 'ネット決済支払' AND netpaybankacc.sfid = (select sfid from worker wf WHERE netpaybankacc.sfid = wf.sfid)\r\n"
      + "        LEFT JOIN bankmastersync netpaybm ON netpaybankacc.accountbank = netpaybm.sfid AND netpaybm.sfid = (select sfid from worker wf WHERE netpaybm.sfid = wf.sfid and wf.sycapproveflg = 'F')\r\n"
      + "    WHERE\r\n" + "    a.appcompany='${sfid}'\r\n" + "    AND (CASE\r\n"
      + "            WHEN (payment.sfid is not null OR (select sfid from worker wf WHERE bankacc.sfid = wf.sfid and wf.sycapproveflg = 'F') is not null OR bm.sfid is not null OR (select sfid from worker wf WHERE netpaybankacc.sfid = wf.sfid and wf.sycapproveflg = 'F') is not null OR netpaybm.sfid is not null OR (select count(sfid) from worker wf WHERE a.sfid = wf.sfid and wf.sycapproveflg = 'F') > 0) THEN 0 ELSE 1 END) = 0")
  List<AppCompanyResponse.AppCompanyApproveFlgChild> getListObjectSycApproveFlgChild(
      @Param("sfid") String sfid);

  @Select("SELECT\r\n" + "		app.sfid as appid,f.sfid as faretableid,\r\n"
      + "		bankacc.sfid as bankaccid,\r\n" + "		bm.sfid as bmid,\r\n"
      + "		bankacc2.sfid as bankacc2id,\r\n" + "		bm2.sfid as bm2id\r\n"
      + "		FROM\r\n" + "		appcompanysync app\r\n"
      + "			LEFT JOIN faretablesync f ON app.faretable = f.sfid\r\n"
      + "			LEFT JOIN bankaccountinformationsync bankacc ON app.bankaccount = bankacc.sfid AND bankacc.accountcategory = '口座引落'\r\n"
      + "			LEFT OUTER JOIN bankmastersync bm ON bankacc.accountbank = bm.sfid\r\n"
      + "			LEFT JOIN bankaccountinformationsync bankacc2 ON app.bankaccount2 = bankacc2.sfid AND bankacc2.accountcategory = 'その他（空転補填・タクエボ等）支払'\r\n"
      + "			LEFT OUTER JOIN bankmastersync bm2 ON bankacc2.accountbank = bm2.sfid\r\n"
      + "			LEFT JOIN accountsync acc ON app.account = acc.sfid\r\n" + "		WHERE\r\n"
      + "			app.sfid=#{sfid}")
  Map<String, String> getIdUpdateStatusWork(@Param("sfid") String sfid);

  @Select("SELECT\r\n"
      + "a.sfid as accountid,payment.sfid as paymentid,bankacc.sfid as bankaccid,bm.sfid as bmid,netpaybankacc.sfid as netpaybankaccid,netpaybm.sfid as netpaybmid\r\n"
      + "FROM accountsync a\r\n"
      + "	LEFT JOIN paymentsystemlinkinforsync payment ON a.sfid = payment.internetpaymerstore\r\n"
      + "	LEFT JOIN bankaccountinformationsync bankacc ON a.sfid = bankacc.account AND bankacc.accountcategory = 'ITAB決済支払'\r\n"
      + "	LEFT JOIN bankmastersync bm ON bankacc.accountbank = bm.sfid AND bm.sfid = (select sfid from worker wf WHERE bm.sfid = wf.sfid and wf.sycapproveflg = 'F')\r\n"
      + "	LEFT JOIN bankaccountinformationsync netpaybankacc ON a.sfid = netpaybankacc.account AND netpaybankacc.accountcategory = 'ネット決済支払'\r\n"
      + "	LEFT JOIN bankmastersync netpaybm ON netpaybankacc.accountbank = netpaybm.sfid AND netpaybm.sfid = (select sfid from worker wf WHERE netpaybm.sfid = wf.sfid and wf.sycapproveflg = 'F')\r\n"
      + "WHERE\r\n" + "a.appcompany=#{sfid}")
  List<Map<String, String>> getAccountIdByAppCompanyId(@Param("sfid") String sfid);
  // End Singapore
}
