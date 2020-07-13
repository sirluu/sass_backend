package jp.co.japantaxi.mapper.stockholm;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import jp.co.japantaxi.response.MerchantResponse.MerchantCoopApproval;

public interface CooperationApprovalMapper {

  List<MerchantCoopApproval> getMerchantCoopApprovalInfoListByCompanyId(@Param("sfid") String sfid);

  @Select("SELECT\r\n" + "	acc.affiliationstorenamekanji as name,\r\n"
      + "	acc.merchantinfo_contactemail as email,\r\n"
      + "	acc.merchantinfo_phonenumberhyphen as phonenumber,\r\n"
      + "	acc.merchantpayinfo_postalcode as paymentpostalcode,\r\n"
      + "	acc.merchantpayinfo_address1 as paymentaddress,\r\n"
      + "	acc.merchantpayinfo_name as paymentcompanyname,\r\n"
      + "	bm.bankcode as paymentbankcode,\r\n" + "	bm.banknamekana as paymentbankname,\r\n"
      + "	bm.branchnamekana as paymentbranchname,\r\n"
      + "	bankacc.accounttype as paymentdeposittype,\r\n"
      + "	bankacc.accountnumber as  paymentaccountno,\r\n"
      + "	bankacc.accountnamekana as paymentdepositor,\r\n" + "  acc.mertermno as mertermno,\r\n"
      + "FROM accountsync acc\r\n"
      + "LEFT JOIN bankaccountinformationsync bankacc ON acc.sfid = bankacc.account\r\n"
      + "LEFT JOIN bankmastersync bm ON bankacc.accountbank = bm.sfid\r\n"
      + "WHERE acc.sfid=#{accountid} ORDER BY acc.syncedtime DESC limit 1")
  Map<String, Object> getCoopApprovalCompanies(@Param("accountid") String accountid);
}
