package jp.co.japantaxi.utils;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import jp.co.japantaxi.model.Account;
import jp.co.japantaxi.model.AppCompany;
import jp.co.japantaxi.model.BankAccountInformation;
import jp.co.japantaxi.model.BankMaster;
import jp.co.japantaxi.model.FareTable;
import jp.co.japantaxi.model.PaymentSystemLinkInfor;

@RunWith(SpringRunner.class)
public class ConvertDataUtilTest {

  @TestConfiguration
  public static class ConvertDataUtilTestConfiguration {
    @Bean
    ConvertDataUtil cdUtil() {
      return new ConvertDataUtil();
    }
  }

  @Test
  public void test_convertJsonObjectToAccount() {
    Account expected = new Account();
    expected.setAffiliationstorenamealphabetic("teststring");
    expected.setAffiliationstorenamekana("teststring");
    expected.setAffiliationstorenamekanji("teststring");
    expected.setAppcompany("teststring");
    expected.setCorporatenumber("teststring");
    expected
        .setInvaliddate(DateTimeUtil.getDateFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setInvaliddetailreason("teststring");
    expected.setInvalidreason("teststring");
    expected.setIrisid("teststring");
    expected.setKeirino(1234);
    expected.setMerchantcontrinforapplicationcars(Utility.parseInt("teststring"));
    expected.setMerchantcontrinforapplicationdate(
        DateTimeUtil.getTimestampFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setMerchantcontrinforisfacetofacesettlement(Utility.parseBoolean("teststring"));
    expected.setMerchantcontrinforisregistry(Utility.parseBoolean("teststring"));
    expected.setMerchantcontrinforlicenseacquisitiondate(
        DateTimeUtil.getDateFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setMerchantcontrinforlicensename("teststring");
    expected.setMerchantcontrinforlicensenumber("teststring");
    expected.setMerchantcontrinfortabletmodel("teststring");
    expected.setMerchantdeliveryinforaddress("teststring");
    expected.setMerchantdeliveryinforcontactname("teststring");
    expected.setMerchantdeliveryinfordate(
        DateTimeUtil.getTimestampFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setMerchantdeliveryinforgakkencustomernumber("teststring");
    expected.setMerchantdeliveryinformemo("teststring");
    expected.setMerchantdeliveryinforname("teststring");
    expected.setMerchantdeliveryinforphone("teststring");
    expected.setMerchantdeliveryinforpostalcode("teststring");
    expected.setMerchantdeliveryinforprefecturecode("teststring");
    expected.setMerchantinfoaddress("teststring");
    expected.setMerchantinfoaddresskana("teststring");
    expected.setMerchantinfocontactemail("teststring");
    expected.setMerchantinfocontactname("teststring");
    expected.setMerchantinfoerroraccountbank("teststring");
    expected.setMerchantinfoerroraccountbranch("teststring");
    expected.setMerchantinfoerroraccountkana("teststring");
    expected.setMerchantinfoerroraccountname("teststring");
    expected.setMerchantinfoerroraccountnumber("teststring");
    expected.setMerchantinfoerroraccounttype("teststring");
    expected.setMerchantinfoerrorinquiryemail("teststring");
    expected.setMerchantinfoerrorinquiryname("teststring");
    expected.setMerchantinfoerrorinquiryphone("teststring");
    expected.setMerchantinfoinquiryemail("teststring");
    expected.setMerchantinfoinquiryname("teststring");
    expected.setMerchantinfoinquiryphone("teststring");
    expected.setMerchantinfoisrepresentative(Utility.parseBoolean("teststring"));
    expected.setMerchantinfometerlist("teststring");
    expected.setMerchantinfopaymentcontactname("teststring");
    expected.setMerchantinfophonenumberhyphen("teststring");
    expected.setMerchantinfopostalcode("teststring");
    expected.setMerchantinfoprefecturecode("teststring");
    expected.setMerchantinforadename("teststring");
    expected.setMerchantinforemarks("teststring");
    expected.setMerchantinfoserviceid("teststring");
    expected.setMerchantinfowarningemail("teststring");
    expected.setMerchantoperinforacceptancedate(
        DateTimeUtil.getDateFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setMerchantoperinforcontractenddate(
        DateTimeUtil.getDateFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setMerchantoperinforguaranteeenddate(
        DateTimeUtil.getDateFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setMerchantoperinformemo("teststring");
    expected.setMerchantoperinforstartdate(
        DateTimeUtil.getDateFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setMerchantpayinfoaddress1("teststring");
    expected.setMerchantpayinfoaddress2("teststring");
    expected.setMerchantpayinfocontactname("teststring");
    expected.setMerchantpayinfoitabbankaccount("teststring");
    expected.setMerchantpayinfonetbankaccount("teststring");
    expected.setMerchantpayinfoname("teststring");
    expected.setMerchantpayinfopostalcode("teststring");
    expected.setMerchantreprinforaddress("teststring");
    expected.setMerchantreprinforbirthday(
        DateTimeUtil.getDateFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setMerchantreprinforfullnamekana("teststring");
    expected.setMerchantreprinforfullnamekanji("teststring");
    expected.setMerchantreprinformemo("teststring");
    expected.setMerchantreprinforphone("teststring");
    expected.setMerchantreprinforpostalcode("teststring");
    expected.setMerchantreprinforprefecturecode("teststring");
    expected.setMerchantreprinforsex("teststring");
    expected.setMerchantstoreinforaddress("teststring");
    expected.setMerchantstoreinforaddresskana("teststring");
    expected.setMerchantstoreinformemo("teststring");
    expected.setMerchantstoreinforphone("teststring");
    expected.setMerchantstoreinforpostalcode("teststring");
    expected.setMerchantstoreinforprefecturecode("teststring");
    expected.setMerchantstoreinforstorenamealphabet("teststring");
    expected.setMerchantstoreinforstorenamehalfwidthkana("teststring");
    expected.setMerchantstoreinforstorenamekanji("teststring");
    expected.setMertermno("teststring");
    expected.setSfid("a04N000000KgpbjIAB");
    expected.setWebsite("teststring");
    expected.setAppcompanyid(Utility.parseInt("12345"));
    expected.setJpporganizationid("teststring");

    JSONObject actual = new JSONObject();
    actual.put("website", "teststring");
    actual.put("sfid", "a04N000000KgpbjIAB");
    actual.put("merTermNo", "teststring");
    actual.put("mSIStoreNameKanji", "teststring");
    actual.put("mSIStoreNameHalfwidthKana", "teststring");
    actual.put("mSIStoreNameAlphabet", "teststring");
    actual.put("mSIPrefectureCode", "teststring");
    actual.put("mSIPostalCode", "teststring");
    actual.put("mSIPhone", "teststring");
    actual.put("mSIMemo", "teststring");
    actual.put("mSIAddressKana", "teststring");
    actual.put("mSIAddress", "teststring");
    actual.put("mRISex", "teststring");
    actual.put("mRIPrefectureCode", "teststring");
    actual.put("mRIPostalCode", "teststring");
    actual.put("mRIPhone", "teststring");
    actual.put("mRIMemo", "teststring");
    actual.put("mRIFullNameKanji", "teststring");
    actual.put("mRIFullNameKana", "teststring");
    actual.put("mRIBirthday", "teststring");
    actual.put("mRIAddress", "teststring");
    actual.put("mPayInfoItabBAcc", "teststring");
    actual.put("mPayInfoNetBAcc", "teststring");
    actual.put("mPayInfoPostalCode", "teststring");
    actual.put("mPayInfoName", "teststring");
    actual.put("mPayInfoContactName", "teststring");
    actual.put("mPayInfoAddress2", "teststring");
    actual.put("mPayInfoAddress1", "teststring");
    actual.put("mOIStartDate", "teststring");
    actual.put("mOIMemo", "teststring");
    actual.put("mOIGuaranteeEndDate", "teststring");
    actual.put("mOIContractEndDate", "teststring");
    actual.put("mOIAcceptanceDate", "teststring");
    actual.put("mIWarningEmail", "teststring");
    actual.put("mIServiceID", "teststring");
    actual.put("mIRemarks", "teststring");
    actual.put("mIradeName", "teststring");
    actual.put("mIPrefectureCode", "teststring");
    actual.put("mIPostalCode", "teststring");
    actual.put("mIPhoneNumberHyphen", "teststring");
    actual.put("mIPaymentContactName", "teststring");
    actual.put("mIMeterList", "teststring");
    actual.put("mIIsRepresentative", "teststring");
    actual.put("mIInquiryPhone", "teststring");
    actual.put("mIInquiryName", "teststring");
    actual.put("mIInquiryEmail", "teststring");
    actual.put("mIErrorInquiryPhone", "teststring");
    actual.put("mIErrorInquiryName", "teststring");
    actual.put("mIErrorInquiryEmail", "teststring");
    actual.put("mIErrorAccountType", "teststring");
    actual.put("mIErrorAccountNumber", "teststring");
    actual.put("mIErrorAccountName", "teststring");
    actual.put("mIErrorAccountKana", "teststring");
    actual.put("mIErrorAccountBranch", "teststring");
    actual.put("mIErrorAccountBank", "teststring");
    actual.put("mIContactName", "teststring");
    actual.put("mIContactEmail", "teststring");
    actual.put("mIAddressKana", "teststring");
    actual.put("mIAddress", "teststring");
    actual.put("mDIPrefectureCode", "teststring");
    actual.put("mDIPostalCode", "teststring");
    actual.put("mDIPhone", "teststring");
    actual.put("mDIName", "teststring");
    actual.put("mDIMemo", "teststring");
    actual.put("mDIGakkenCustomerNumber", "teststring");
    actual.put("mDIDate", "teststring");
    actual.put("mDIContactName", "teststring");
    actual.put("mDIAddress", "teststring");
    actual.put("mCITabletModel", "teststring");
    actual.put("mCILicenseNumber", "teststring");
    actual.put("mCILicenseName", "teststring");
    actual.put("mCILicenseAcquisitionDate", "teststring");
    actual.put("mCIIsRegistry", "teststring");
    actual.put("mCIIsFacetofaceSettlement", "teststring");
    actual.put("mCIApplicationDate", "teststring");
    actual.put("mCIApplicationCars", "teststring");
    actual.put("keiri_no", "1234");
    actual.put("iris_id", "teststring");
    actual.put("invalidReason", "teststring");
    actual.put("invalidDetailReason", "teststring");
    actual.put("invalidDate", "teststring");
    actual.put("corporateNumber", "teststring");
    actual.put("appCompany", "teststring");
    actual.put("aSNameKanji", "teststring");
    actual.put("aSNameKana", "teststring");
    actual.put("aSNameAlphabetic", "teststring");
    actual.put("lastMDate", "teststring");
    actual.put("appCompanyID", "12345");
    actual.put("jppOrgID", "teststring");

    Assert.assertEquals(expected.getAffiliationstorenamealphabetic(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getAffiliationstorenamealphabetic());
    Assert.assertEquals(expected.getAffiliationstorenamekana(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getAffiliationstorenamekana());
    Assert.assertEquals(expected.getAffiliationstorenamekanji(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getAffiliationstorenamekanji());
    Assert.assertEquals(expected.getAppcompany(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getAppcompany());
    Assert.assertEquals(expected.getCorporatenumber(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getCorporatenumber());
    Assert.assertEquals(expected.getInvaliddate(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getInvaliddate());
    Assert.assertEquals(expected.getInvaliddetailreason(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getInvaliddetailreason());
    Assert.assertEquals(expected.getInvalidreason(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getInvalidreason());

    Assert.assertEquals(expected.getIrisid(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getIrisid());
    Assert.assertEquals(expected.getKeirino(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getKeirino());
    Assert.assertEquals(expected.getMerchantcontrinforapplicationcars(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantcontrinforapplicationcars());
    Assert.assertEquals(expected.getMerchantcontrinforapplicationdate(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantcontrinforapplicationdate());
    Assert.assertEquals(expected.getMerchantcontrinforisfacetofacesettlement(), ConvertDataUtil
        .convertJsonObjectToAccount(actual).getMerchantcontrinforisfacetofacesettlement());
    Assert.assertEquals(expected.getMerchantcontrinforisregistry(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantcontrinforisregistry());
    Assert.assertEquals(expected.getMerchantcontrinforlicenseacquisitiondate(), ConvertDataUtil
        .convertJsonObjectToAccount(actual).getMerchantcontrinforlicenseacquisitiondate());
    Assert.assertEquals(expected.getMerchantcontrinforlicensename(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantcontrinforlicensename());
    Assert.assertEquals(expected.getMerchantcontrinforlicensenumber(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantcontrinforlicensenumber());
    Assert.assertEquals(expected.getMerchantcontrinfortabletmodel(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantcontrinfortabletmodel());
    Assert.assertEquals(expected.getMerchantdeliveryinforaddress(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantdeliveryinforaddress());
    Assert.assertEquals(expected.getMerchantdeliveryinforcontactname(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantdeliveryinforcontactname());
    Assert.assertEquals(expected.getMerchantdeliveryinfordate(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantdeliveryinfordate());
    Assert.assertEquals(expected.getMerchantdeliveryinforgakkencustomernumber(), ConvertDataUtil
        .convertJsonObjectToAccount(actual).getMerchantdeliveryinforgakkencustomernumber());
    Assert.assertEquals(expected.getMerchantdeliveryinformemo(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantdeliveryinformemo());
    Assert.assertEquals(expected.getMerchantdeliveryinforname(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantdeliveryinforname());
    Assert.assertEquals(expected.getMerchantdeliveryinforphone(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantdeliveryinforphone());
    Assert.assertEquals(expected.getMerchantdeliveryinforpostalcode(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantdeliveryinforpostalcode());
    Assert.assertEquals(expected.getMerchantdeliveryinforprefecturecode(), ConvertDataUtil
        .convertJsonObjectToAccount(actual).getMerchantdeliveryinforprefecturecode());
    Assert.assertEquals(expected.getMerchantinfoaddress(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoaddress());
    Assert.assertEquals(expected.getMerchantinfoaddresskana(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoaddresskana());
    Assert.assertEquals(expected.getMerchantinfocontactemail(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfocontactemail());
    Assert.assertEquals(expected.getMerchantinfocontactname(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfocontactname());
    Assert.assertEquals(expected.getMerchantinfoerroraccountbank(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoerroraccountbank());
    Assert.assertEquals(expected.getMerchantinfoerroraccountbranch(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoerroraccountbranch());
    Assert.assertEquals(expected.getMerchantinfoerroraccountkana(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoerroraccountkana());
    Assert.assertEquals(expected.getMerchantinfoerroraccountname(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoerroraccountname());
    Assert.assertEquals(expected.getMerchantinfoerroraccountnumber(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoerroraccountnumber());
    Assert.assertEquals(expected.getMerchantinfoerroraccounttype(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoerroraccounttype());
    Assert.assertEquals(expected.getMerchantinfoerrorinquiryemail(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoerrorinquiryemail());
    Assert.assertEquals(expected.getMerchantinfoerrorinquiryname(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoerrorinquiryname());
    Assert.assertEquals(expected.getMerchantinfoerrorinquiryphone(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoerrorinquiryphone());
    Assert.assertEquals(expected.getMerchantinfoinquiryemail(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoinquiryemail());
    Assert.assertEquals(expected.getMerchantinfoinquiryname(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoinquiryname());
    Assert.assertEquals(expected.getMerchantinfoinquiryphone(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoinquiryphone());
    Assert.assertEquals(expected.getMerchantinfoisrepresentative(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoisrepresentative());
    Assert.assertEquals(expected.getMerchantinfometerlist(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfometerlist());
    Assert.assertEquals(expected.getMerchantinfopaymentcontactname(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfopaymentcontactname());
    Assert.assertEquals(expected.getMerchantinfophonenumberhyphen(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfophonenumberhyphen());
    Assert.assertEquals(expected.getMerchantinfopostalcode(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfopostalcode());
    Assert.assertEquals(expected.getMerchantinfoprefecturecode(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoprefecturecode());
    Assert.assertEquals(expected.getMerchantinforadename(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinforadename());
    Assert.assertEquals(expected.getMerchantinforemarks(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinforemarks());
    Assert.assertEquals(expected.getMerchantinfoserviceid(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfoserviceid());
    Assert.assertEquals(expected.getMerchantinfowarningemail(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantinfowarningemail());
    Assert.assertEquals(expected.getMerchantoperinforacceptancedate(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantoperinforacceptancedate());
    Assert.assertEquals(expected.getMerchantoperinforcontractenddate(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantoperinforcontractenddate());
    Assert.assertEquals(expected.getMerchantoperinforguaranteeenddate(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantoperinforguaranteeenddate());
    Assert.assertEquals(expected.getMerchantoperinformemo(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantoperinformemo());
    Assert.assertEquals(expected.getMerchantoperinforstartdate(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantoperinforstartdate());
    Assert.assertEquals(expected.getMerchantpayinfoaddress1(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantpayinfoaddress1());
    Assert.assertEquals(expected.getMerchantpayinfoaddress2(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantpayinfoaddress2());
    Assert.assertEquals(expected.getMerchantpayinfocontactname(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantpayinfocontactname());
    Assert.assertEquals(expected.getMerchantpayinfoname(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantpayinfoname());
    Assert.assertEquals(expected.getMerchantpayinfopostalcode(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantpayinfopostalcode());
    Assert.assertEquals(expected.getMerchantreprinforaddress(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantreprinforaddress());
    Assert.assertEquals(expected.getMerchantreprinforbirthday(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantreprinforbirthday());
    Assert.assertEquals(expected.getMerchantreprinforfullnamekana(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantreprinforfullnamekana());
    Assert.assertEquals(expected.getMerchantreprinforfullnamekanji(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantreprinforfullnamekanji());
    Assert.assertEquals(expected.getMerchantreprinformemo(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantreprinformemo());
    Assert.assertEquals(expected.getMerchantpayinfoitabbankaccount(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantpayinfoitabbankaccount());
    Assert.assertEquals(expected.getMerchantpayinfonetbankaccount(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantpayinfonetbankaccount());
    Assert.assertEquals(expected.getMerchantreprinforphone(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantreprinforphone());
    Assert.assertEquals(expected.getMerchantreprinforpostalcode(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantreprinforpostalcode());
    Assert.assertEquals(expected.getMerchantreprinforprefecturecode(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantreprinforprefecturecode());
    Assert.assertEquals(expected.getMerchantreprinforsex(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantreprinforsex());
    Assert.assertEquals(expected.getMerchantstoreinforaddress(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantstoreinforaddress());
    Assert.assertEquals(expected.getMerchantstoreinforaddresskana(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantstoreinforaddresskana());
    Assert.assertEquals(expected.getMerchantstoreinformemo(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantstoreinformemo());
    Assert.assertEquals(expected.getMerchantstoreinforphone(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantstoreinforphone());
    Assert.assertEquals(expected.getMerchantstoreinforpostalcode(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantstoreinforpostalcode());
    Assert.assertEquals(expected.getMerchantstoreinforprefecturecode(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantstoreinforprefecturecode());
    Assert.assertEquals(expected.getMerchantstoreinforstorenamealphabet(), ConvertDataUtil
        .convertJsonObjectToAccount(actual).getMerchantstoreinforstorenamealphabet());
    Assert.assertEquals(expected.getMerchantstoreinforstorenamehalfwidthkana(), ConvertDataUtil
        .convertJsonObjectToAccount(actual).getMerchantstoreinforstorenamehalfwidthkana());
    Assert.assertEquals(expected.getMerchantstoreinforstorenamekanji(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMerchantstoreinforstorenamekanji());
    Assert.assertEquals(expected.getMertermno(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getMertermno());
    Assert.assertEquals(expected.getSfid(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getSfid());
    Assert.assertEquals(expected.getWebsite(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getWebsite());
    Assert.assertEquals(expected.getAppcompanyid(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getAppcompanyid());
    Assert.assertEquals(expected.getJpporganizationid(),
        ConvertDataUtil.convertJsonObjectToAccount(actual).getJpporganizationid());
  }

  @Test
  public void test_convertAccount2Sync() {
    Account expected = new Account();
    expected.setSyncedtime(
        Timestamp.valueOf(LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
    Account actual = new Account();

    Assert.assertNotEquals(expected, ConvertDataUtil.convertAccount2Sync(actual, true));
  }

  @Test
  public void test_convertJsonObjectToAppcompany() {
    AppCompany expected = new AppCompany();
    expected.setAcceptmajor2(Utility.parseFloat("teststring"));
    expected.setAcceptminor2(Utility.parseFloat("teststring"));
    expected.setAccount("teststring");
    expected.setJtxcompanyid(Utility.parseInt("teststring"));
    expected.setAppdisplayname("teststring");
    expected.setBookingfeedescription("teststring");
    expected.setBankaccount("teststring");
    expected.setBankaccount2("teststring");
    expected.setCarimagecolor1("teststring");
    expected.setCarimagecolor2("teststring");
    expected.setCarimagecolor3("teststring");
    expected.setCarimagecolor4("teststring");
    expected.setCarimagecolor5("teststring");
    expected.setCarimagecolor6("teststring");
    expected.setCarimagecolor7("teststring");
    expected.setCarimagecolor8("teststring");
    expected.setCarimagename1("teststring");
    expected.setCarimagename2("teststring");
    expected.setCarimagename3("teststring");
    expected.setCarimagename4("teststring");
    expected.setCarimagename5("teststring");
    expected.setCarimagename6("teststring");
    expected.setCarimagename7("teststring");
    expected.setCarimagename8("teststring");
    expected.setCars(Utility.parseInt("teststring"));
    expected.setClosingtimefrom(
        DateTimeUtil.getTimestampFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setClosingtimeto(
        DateTimeUtil.getTimestampFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setCommunicationurl("teststring");
    expected.setCompanylogoimage("teststring");
    expected.setCompanynameen("teststring");
    expected.setDispatchtype("teststring");
    expected.setDisplaysalescity("teststring");
    expected.setDisplaysalescityen("teststring");
    expected.setEmail("teststring");
    expected.setExtendedfieldmap("teststring");
    expected.setFaretable("teststring");
    expected.setGloballat("teststring");
    expected.setGloballng("teststring");
    expected.setGroupcompanies(Utility.parseInt("teststring"));
    expected
        .setInvaliddate(DateTimeUtil.getDateFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setInvaliddetailreason("teststring");
    expected.setInvalidreason("teststring");
    expected.setIsenableclosing(Utility.parseBoolean("teststring"));
    expected.setIsexternalcancel(Utility.parseBoolean("teststring"));
    expected.setIsradionumberedit(Utility.parseBoolean("teststring"));
    expected.setLogoimage("teststring");
    expected.setMinutestowaitforpickup("teststring");
    expected.setName("teststring");
    expected.setNetpaytypes("teststring");
    expected.setOrdercancelurl("teststring");
    expected.setOrderurl("teststring");
    expected.setPartnerapiversion(Utility.parseInt("teststring"));
    expected.setPaymentmethod("teststring");
    expected.setPaymentmethoden("teststring");
    expected.setPhonenumber("teststring");
    expected.setPhonenumbercancel("teststring");
    expected.setPickupfeedescription("teststring");
    expected.setProfile("teststring");
    expected.setRatingaverage(Utility.parseFloat("teststring"));
    expected.setReplywatingminute(Utility.parseInt("teststring"));
    expected.setSalescity("teststring");
    expected.setArea("teststring");
    expected.setAreacode("teststring");
    expected.setSalesprefecture("teststring");
    expected.setSalesprefectureen("teststring");
    expected.setShowbookingcancel(Utility.parseBoolean("teststring"));
    expected.setShowdest(Utility.parseBoolean("teststring"));
    expected.setShowemptycar(Utility.parseBoolean("teststring"));
    expected.setShowimmediatecancel(Utility.parseBoolean("teststring"));
    expected.setShowminutestowaitforpickup(Utility.parseBoolean("teststring"));
    expected.setShowmoving(Utility.parseBoolean("teststring"));
    expected.setShowmustarrivaltime(Utility.parseBoolean("teststring"));
    expected.setShowvisit("teststring");
    expected.setStatus("teststring");
    expected.setSfid("a04N000000KgpbjIAB");
    expected.setWaitforpickupdefault(Utility.parseInt("teststring"));
    expected.setWarningemail("teststring");
    expected.setDrivercontract("teststring");
    expected.setImplementationcosts(Utility.parseInt("teststring"));
    expected.setMonthlyminimumcars(Utility.parseInt("teststring"));
    expected.setMonthlyminimumcharge(Utility.parseInt("teststring"));
    expected.setPayablecommission(Utility.parseInt("teststring"));
    expected.setRemarks("teststring");
    expected.setRequestcompanyidrelation(Utility.parseInt("teststring"));
    expected.setRequeststartdate(DateTimeUtil.getDateFromString("teststring", DateTimeUtil.DD_FM));
    expected.setWww("teststring");
    expected.setDestinationaddress("teststring");
    expected.setDestinationcompanyname("teststring");
    expected.setDestinationcontactname("teststring");
    expected.setDestinationpostalcode("teststring");
    expected.setDestinationprefecture("teststring");
    JSONObject actual = new JSONObject();
    actual.put("warningEmail", "teststring");
    actual.put("waitForPickUpDefault", "teststring");
    actual.put("status", "teststring");
    actual.put("showVisit", "teststring");
    actual.put("showMustArrivalTime", "teststring");
    actual.put("showMoving", "teststring");
    actual.put("showMinutesToWaitForPickUp", "teststring");
    actual.put("showImmediateCancel", "teststring");
    actual.put("showEmptyCar", "teststring");
    actual.put("showDest", "teststring");
    actual.put("showBookingCancel", "teststring");
    actual.put("areaId", "teststring");
    actual.put("areaCode", "teststring");
    actual.put("salesPrefecture", "teststring");
    actual.put("salesPrefeEN", "teststring");
    actual.put("salesCity", "teststring");
    actual.put("replyWaitingMinute", "teststring");
    actual.put("ratingAverage", "teststring");
    actual.put("profile", "teststring");
    actual.put("pickupFeeDescription", "teststring");
    actual.put("phoneNumberCancel", "teststring");
    actual.put("phoneNumber", "teststring");
    actual.put("paymentMethod", "teststring");
    actual.put("payMethodEN", "teststring");
    actual.put("partnerApiVersion", "teststring");
    actual.put("orderUrl", "teststring");
    actual.put("orderCancelUrl", "teststring");
    actual.put("netPayTypes", "teststring");
    actual.put("name", "teststring");
    actual.put("minutesToWaitForPickUp", "teststring");
    actual.put("logoImage", "teststring");
    actual.put("isRadioNumberEdit", "teststring");
    actual.put("isExternalCancel", "teststring");
    actual.put("isEnableClosing", "teststring");
    actual.put("invalidReason", "teststring");
    actual.put("invalidDetailReason", "teststring");
    actual.put("invalidDate", "teststring");
    actual.put("id", "a04N000000KgpbjIAB");
    actual.put("groupCompanies", "teststring");
    actual.put("globalLng", "teststring");
    actual.put("globalLat", "teststring");
    actual.put("fareTable", "teststring");
    actual.put("extendedFieldMap", "teststring");
    actual.put("email", "teststring");
    actual.put("displaySalesCityEN", "teststring");
    actual.put("displaySalesCity", "teststring");
    actual.put("dispatchType", "teststring");
    actual.put("companyNameEN", "teststring");
    actual.put("companyLogoImage", "teststring");
    actual.put("communicationUrl", "teststring");
    actual.put("closingTimeTo", "teststring");
    actual.put("closingTimeFrom", "teststring");
    actual.put("cars", "teststring");
    actual.put("carIColor1", "teststring");
    actual.put("carIColor2", "teststring");
    actual.put("carIColor3", "teststring");
    actual.put("carIColor4", "teststring");
    actual.put("carIColor5", "teststring");
    actual.put("carIColor6", "teststring");
    actual.put("carIColor7", "teststring");
    actual.put("carIColor8", "teststring");
    actual.put("carIName1", "teststring");
    actual.put("carIName2", "teststring");
    actual.put("carIName3", "teststring");
    actual.put("carIName4", "teststring");
    actual.put("carIName5", "teststring");
    actual.put("carIName6", "teststring");
    actual.put("carIName7", "teststring");
    actual.put("carIName8", "teststring");
    actual.put("bookingFeeDescription", "teststring");
    actual.put("appDisplayName", "teststring");
    actual.put("jtxCompanyID", "teststring");
    actual.put("account", "teststring");
    actual.put("acceptMinor2", "teststring");
    actual.put("acceptMajor2", "teststring");
    actual.put("driverContract", "teststring");
    actual.put("implementationCosts", "teststring");
    actual.put("monthlyMinimumCars", "teststring");
    actual.put("monthlyMinimumCharge", "teststring");
    actual.put("payableCommission", "teststring");
    actual.put("remarks", "teststring");
    actual.put("requestCompanyIDRelation", "teststring");
    actual.put("requestStartDate", "teststring");
    actual.put("bankAccount", "teststring");
    actual.put("bankAccount2", "teststring");
    actual.put("www", "teststring");
    actual.put("desAddr", "teststring");
    actual.put("desCompName", "teststring");
    actual.put("desContName", "teststring");
    actual.put("desPostCode", "teststring");
    actual.put("desPrefec", "teststring");
    actual.put("lastMDate", "teststring");

    Assert.assertNotEquals(expected, ConvertDataUtil.convertJsonObjectToAppCompany(actual));
    Assert.assertEquals(expected.getAcceptmajor2(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getAcceptmajor2());
    Assert.assertEquals(expected.getAcceptminor2(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getAcceptminor2());
    Assert.assertEquals(expected.getAccount(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getAccount());
    Assert.assertEquals(expected.getJtxcompanyid(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getJtxcompanyid());
    Assert.assertEquals(expected.getAppdisplayname(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getAppdisplayname());
    Assert.assertEquals(expected.getBookingfeedescription(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getBookingfeedescription());
    Assert.assertEquals(expected.getCarimagecolor1(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagecolor1());
    Assert.assertEquals(expected.getCarimagecolor2(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagecolor2());
    Assert.assertEquals(expected.getCarimagecolor3(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagecolor3());
    Assert.assertEquals(expected.getCarimagecolor4(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagecolor4());
    Assert.assertEquals(expected.getCarimagecolor5(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagecolor5());
    Assert.assertEquals(expected.getCarimagecolor6(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagecolor6());
    Assert.assertEquals(expected.getCarimagecolor7(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagecolor7());
    Assert.assertEquals(expected.getCarimagecolor8(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagecolor8());
    Assert.assertEquals(expected.getCarimagename1(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagename1());
    Assert.assertEquals(expected.getCarimagename2(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagename2());
    Assert.assertEquals(expected.getCarimagename3(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagename3());
    Assert.assertEquals(expected.getCarimagename4(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagename4());
    Assert.assertEquals(expected.getCarimagename5(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagename5());
    Assert.assertEquals(expected.getCarimagename6(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagename6());
    Assert.assertEquals(expected.getCarimagename7(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagename7());
    Assert.assertEquals(expected.getCarimagename8(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCarimagename8());
    Assert.assertEquals(expected.getCars(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCars());
    Assert.assertEquals(expected.getClosingtimefrom(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getClosingtimefrom());
    Assert.assertEquals(expected.getClosingtimeto(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getClosingtimeto());
    Assert.assertEquals(expected.getCommunicationurl(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCommunicationurl());
    Assert.assertEquals(expected.getCompanylogoimage(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCompanylogoimage());
    Assert.assertEquals(expected.getCompanynameen(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getCompanynameen());
    Assert.assertEquals(expected.getDispatchtype(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getDispatchtype());
    Assert.assertEquals(expected.getDisplaysalescity(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getDisplaysalescity());
    Assert.assertEquals(expected.getDisplaysalescityen(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getDisplaysalescityen());
    Assert.assertEquals(expected.getEmail(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getEmail());
    Assert.assertEquals(expected.getExtendedfieldmap(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getExtendedfieldmap());
    Assert.assertEquals(expected.getFaretable(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getFaretable());
    Assert.assertEquals(expected.getGloballat(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getGloballat());
    Assert.assertEquals(expected.getGloballng(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getGloballng());
    Assert.assertEquals(expected.getGroupcompanies(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getGroupcompanies());
    Assert.assertEquals(expected.getInvaliddate(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getInvaliddate());
    Assert.assertEquals(expected.getInvaliddetailreason(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getInvaliddetailreason());
    Assert.assertEquals(expected.getInvalidreason(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getInvalidreason());
    Assert.assertEquals(expected.getIsenableclosing(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getIsenableclosing());
    Assert.assertEquals(expected.getIsexternalcancel(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getIsexternalcancel());
    Assert.assertEquals(expected.getIsradionumberedit(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getIsradionumberedit());
    Assert.assertEquals(expected.getLogoimage(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getLogoimage());
    Assert.assertEquals(expected.getMinutestowaitforpickup(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getMinutestowaitforpickup());
    Assert.assertEquals(expected.getName(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getName());
    Assert.assertEquals(expected.getNetpaytypes(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getNetpaytypes());
    Assert.assertEquals(expected.getOrdercancelurl(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getOrdercancelurl());
    Assert.assertEquals(expected.getOrderurl(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getOrderurl());
    Assert.assertEquals(expected.getPartnerapiversion(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getPartnerapiversion());
    Assert.assertEquals(expected.getPaymentmethod(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getPaymentmethod());
    Assert.assertEquals(expected.getPaymentmethoden(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getPaymentmethoden());
    Assert.assertEquals(expected.getPhonenumber(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getPhonenumber());
    Assert.assertEquals(expected.getPhonenumbercancel(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getPhonenumbercancel());
    Assert.assertEquals(expected.getPickupfeedescription(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getPickupfeedescription());
    Assert.assertEquals(expected.getProfile(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getProfile());
    Assert.assertEquals(expected.getRatingaverage(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getRatingaverage());
    Assert.assertEquals(expected.getReplywatingminute(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getReplywatingminute());
    Assert.assertEquals(expected.getSalescity(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getSalescity());
    Assert.assertEquals(expected.getArea(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getArea());
    Assert.assertEquals(expected.getAreacode(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getAreacode());
    Assert.assertEquals(expected.getSalesprefecture(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getSalesprefecture());
    Assert.assertEquals(expected.getSalesprefectureen(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getSalesprefectureen());
    Assert.assertEquals(expected.getShowbookingcancel(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getShowbookingcancel());
    Assert.assertEquals(expected.getShowdest(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getShowdest());
    Assert.assertEquals(expected.getShowemptycar(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getShowemptycar());
    Assert.assertEquals(expected.getShowimmediatecancel(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getShowimmediatecancel());
    Assert.assertEquals(expected.getShowminutestowaitforpickup(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getShowminutestowaitforpickup());
    Assert.assertEquals(expected.getShowmoving(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getShowmoving());
    Assert.assertEquals(expected.getShowmustarrivaltime(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getShowmustarrivaltime());
    Assert.assertEquals(expected.getShowvisit(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getShowvisit());
    Assert.assertEquals(expected.getStatus(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getStatus());
    Assert.assertEquals(expected.getSfid(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getSfid());
    Assert.assertEquals(expected.getWaitforpickupdefault(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getWaitforpickupdefault());
    Assert.assertEquals(expected.getWarningemail(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getWarningemail());
    Assert.assertEquals(expected.getDrivercontract(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getDrivercontract());
    Assert.assertEquals(expected.getImplementationcosts(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getImplementationcosts());
    Assert.assertEquals(expected.getMonthlyminimumcars(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getMonthlyminimumcars());
    Assert.assertEquals(expected.getMonthlyminimumcharge(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getMonthlyminimumcharge());
    Assert.assertEquals(expected.getPayablecommission(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getPayablecommission());
    Assert.assertEquals(expected.getRemarks(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getRemarks());
    Assert.assertEquals(expected.getRequestcompanyidrelation(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getRequestcompanyidrelation());
    Assert.assertEquals(expected.getBankaccount(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getBankaccount());
    Assert.assertEquals(expected.getBankaccount2(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getBankaccount2());
    Assert.assertEquals(expected.getWww(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getWww());
    Assert.assertEquals(expected.getDestinationaddress(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getDestinationaddress());
    Assert.assertEquals(expected.getDestinationcompanyname(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getDestinationcompanyname());
    Assert.assertEquals(expected.getDestinationcontactname(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getDestinationcontactname());
    Assert.assertEquals(expected.getDestinationpostalcode(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getDestinationpostalcode());
    Assert.assertEquals(expected.getDestinationprefecture(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getDestinationprefecture());
    Assert.assertEquals(expected.getRequeststartdate(),
        ConvertDataUtil.convertJsonObjectToAppCompany(actual).getRequeststartdate());
  }

  @Test
  public void test_convertAppCompany2Sync() throws IOException {
    AppCompany expected = new AppCompany();
    expected.setAcceptmajor2(Utility.parseFloat("teststring"));
    expected.setAcceptminor2(Utility.parseFloat("teststring"));
    expected.setAccount("teststring");
    expected.setJtxcompanyid(Utility.parseInt("teststring"));
    expected.setAppdisplayname("teststring");
    expected.setBookingfeedescription("teststring");
    expected.setCarimagecolor1("teststring");
    expected.setCarimagecolor2("teststring");
    expected.setCarimagecolor3("teststring");
    expected.setCarimagecolor4("teststring");
    expected.setCarimagecolor5("teststring");
    expected.setCarimagecolor6("teststring");
    expected.setCarimagecolor7("teststring");
    expected.setCarimagecolor8("teststring");
    expected.setCarimagename1("teststring");
    expected.setCarimagename2("teststring");
    expected.setCarimagename3("teststring");
    expected.setCarimagename4("teststring");
    expected.setCarimagename5("teststring");
    expected.setCarimagename6("teststring");
    expected.setCarimagename7("teststring");
    expected.setCarimagename8("teststring");
    expected.setCars(Utility.parseInt("teststring"));
    expected.setClosingtimefrom(
        DateTimeUtil.getTimestampFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setClosingtimeto(
        DateTimeUtil.getTimestampFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setCommunicationurl("teststring");
    expected.setCompanylogoimage("teststring");
    expected.setCompanynameen("teststring");
    expected.setDispatchtype("teststring1");
    expected.setDisplaysalescity("teststring");
    expected.setDisplaysalescityen("teststring");
    expected.setEmail("teststring");
    expected.setExtendedfieldmap("teststring");
    expected.setFaretable("teststring");
    expected.setGloballat("teststring");
    expected.setGloballng("teststring");
    expected.setGroupcompanies(Utility.parseInt("teststring"));
    expected
        .setInvaliddate(DateTimeUtil.getDateFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setInvaliddetailreason("teststring");
    expected.setInvalidreason("teststring");
    expected.setIsenableclosing(Utility.parseBoolean("teststring"));
    expected.setIsexternalcancel(Utility.parseBoolean("teststring"));
    expected.setIsradionumberedit(Utility.parseBoolean("teststring"));
    expected.setLogoimage("teststring");
    expected.setMinutestowaitforpickup("teststring");
    expected.setName("teststring");
    expected.setNetpaytypes("teststring");
    expected.setOrdercancelurl("teststring");
    expected.setOrderurl("teststring");
    expected.setPartnerapiversion(Utility.parseInt("teststring"));
    expected.setPaymentmethod("teststring");
    expected.setPhonenumber("teststring");
    expected.setPhonenumbercancel("teststring");
    expected.setPickupfeedescription("teststring");
    expected.setProfile("teststring");
    expected.setRatingaverage(Utility.parseFloat("teststring"));
    expected.setReplywatingminute(Utility.parseInt("teststring"));
    expected.setSalescity("teststring");
    expected.setArea("teststring");
    expected.setShowbookingcancel(Utility.parseBoolean("teststring"));
    expected.setShowdest(Utility.parseBoolean("teststring"));
    expected.setShowemptycar(Utility.parseBoolean("teststring"));
    expected.setShowimmediatecancel(Utility.parseBoolean("teststring"));
    expected.setShowminutestowaitforpickup(Utility.parseBoolean("teststring"));
    expected.setShowmoving(Utility.parseBoolean("teststring"));
    expected.setShowmustarrivaltime(Utility.parseBoolean("teststring"));
    expected.setShowvisit("teststring");
    expected.setStatus("teststring");
    expected.setSfid("a04N000000KgpbjIAB");
    expected.setWaitforpickupdefault(Utility.parseInt("teststring"));
    expected.setWarningemail("teststring");
    expected.setSyncedtime(
        Timestamp.valueOf(LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
    AppCompany actual = new AppCompany();

    JsonMapper.readDataSync(Constant.APPCOMPANY);
    Assert.assertNotEquals(actual, ConvertDataUtil.convertAppCompany2Sync(expected, true));
  }

  @Test
  public void test_convertJsonObjectToBankAccountInformation() {
    BankAccountInformation expected = new BankAccountInformation();
    expected.setAccount("teststring");
    expected.setAppcompany("teststring");
    expected.setAccountbank("teststring");
    expected.setAccountcategory("teststring");
    expected.setAccountname("teststring");
    expected.setAccountnamekana("teststring");
    expected.setAccountnumber("teststring");
    expected.setAccounttype("teststring");
    expected.setInvaliddate(DateTimeUtil.getDateFromString("", DateTimeUtil.DATE_TIME_FM));
    expected.setInvalidreason("teststring");
    expected.setSfid("a04N000000KgpbjIAB");
    expected.setAppcompanyid("teststring");
    JSONObject actual = new JSONObject();
    actual.put("account", "teststring");
    actual.put("appcompany", "teststring");
    actual.put("accountBank", "teststring");
    actual.put("accountCategory", "teststring");
    actual.put("accountName", "teststring");
    actual.put("accountNameKana", "teststring");
    actual.put("accountNumber", "teststring");
    actual.put("accountType", "teststring");
    actual.put("invalidDate", "teststring");
    actual.put("invalidReason", "teststring");
    actual.put("id", "a04N000000KgpbjIAB");
    actual.put("compnayID", "teststring");
    actual.put("lastMDate", "teststring");

    Assert.assertNotEquals(expected,
        ConvertDataUtil.convertJsonObjectToBankAccountInformation(actual));
    Assert.assertEquals(expected.getAccount(),
        ConvertDataUtil.convertJsonObjectToBankAccountInformation(actual).getAccount());
    Assert.assertEquals(expected.getAppcompany(),
        ConvertDataUtil.convertJsonObjectToBankAccountInformation(actual).getAppcompany());
    Assert.assertEquals(expected.getAccountbank(),
        ConvertDataUtil.convertJsonObjectToBankAccountInformation(actual).getAccountbank());
    Assert.assertEquals(expected.getAccountcategory(),
        ConvertDataUtil.convertJsonObjectToBankAccountInformation(actual).getAccountcategory());
    Assert.assertEquals(expected.getAccountname(),
        ConvertDataUtil.convertJsonObjectToBankAccountInformation(actual).getAccountname());
    Assert.assertEquals(expected.getAccountnamekana(),
        ConvertDataUtil.convertJsonObjectToBankAccountInformation(actual).getAccountnamekana());
    Assert.assertEquals(expected.getAccountnumber(),
        ConvertDataUtil.convertJsonObjectToBankAccountInformation(actual).getAccountnumber());
    Assert.assertEquals(expected.getAccounttype(),
        ConvertDataUtil.convertJsonObjectToBankAccountInformation(actual).getAccounttype());
  }

  @Test
  public void test_convertBankAccountInformation2Sync() throws IOException {
    BankAccountInformation expected = new BankAccountInformation();
    expected.setAccount("teststring");
    expected.setAccountbank("teststring");
    expected.setAccountcategory("teststring");
    expected.setAccountname("teststring");
    expected.setAccountnamekana("teststring");
    expected.setAccountnumber("teststring");
    expected.setAccounttype("teststring");
    expected.setInvaliddate(DateTimeUtil.getDateFromString("", DateTimeUtil.DATE_TIME_FM));
    expected.setInvalidreason("teststring");
    expected.setSfid("a04N000000KgpbjIAB");
    expected.setSyncedtime(
        Timestamp.valueOf(LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
    BankAccountInformation actual = new BankAccountInformation();
    JsonMapper.readDataSync(Constant.BANKACCOUNTINFORMATION);
    Assert.assertNotEquals(actual,
        ConvertDataUtil.convertBankAccountInformation2Sync(expected, true));
  }

  @Test
  public void test_convertJsonObjectToPaymentSystemLinkInfor() {
    PaymentSystemLinkInfor expected = new PaymentSystemLinkInfor();
    expected.setServiceid("teststring");
    expected.setAppcompany("teststring");
    expected.setTofacemerchantid("teststring");
    expected.setNontofacemerchantid("teststring");
    expected.setLinepaychannelsecret("teststring");
    expected.setLinepaychannelid("teststring");
    expected.setLastmodifieddate(
        DateTimeUtil.getTimestampFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setInternetpaymerstore("teststring");
    expected.setTofacepaymentrouteid(Utility.parseInt("1234"));
    expected.setNontofacepaymentrouteid(Utility.parseInt("1234"));
    expected.setTofacehashkey("teststring");
    expected.setNontofacehashkey("teststring");
    expected.setTofaceencryptkey("teststring");
    expected.setNontofaceencryptkey("teststring");
    expected.setTofaceencryptiv("teststring");
    expected.setNontofaceencryptiv("teststring");
    expected.setTofacecardtype("teststring");
    expected.setNontofacecardtype("teststring");
    expected.setTofacebasicauthpassword("teststring");
    expected.setNontofacebasicauthpassword("teststring");
    expected.setAupaystoreid("teststring");
    expected.setPaymentmerchantid(123);
    expected.setMerchantbasicinfoname("teststring");
    expected.setQrgatewaybranchcode("teststring");
    expected.setOnepaymerchantcode("teststring");
    JSONObject actual = new JSONObject();
    actual.put("serviceId", "teststring");
    actual.put("appCompany", "teststring");
    actual.put("linePayChannelId", "teststring");
    actual.put("linePayChannelSecret", "teststring");
    actual.put("lastMDate", "teststring");
    actual.put("internetPayMerStore", "teststring");
    actual.put("id", "teststring");
    actual.put("auPayStoreId", "teststring");
    actual.put("toFacePaymentRouteId", "1234");
    actual.put("toFaceMerchantId", "teststring");
    actual.put("toFaceHashKey", "teststring");
    actual.put("toFaceEncryptKey", "teststring");
    actual.put("toFaceEncryptIV", "teststring");
    actual.put("toFaceCardType", "teststring");
    actual.put("toFaceBasicAuthPassword", "teststring");
    actual.put("toFaceBasicAuthId", "teststring");
    actual.put("nonToFaceHashKey", "teststring");
    actual.put("nonToFacePaymentRouteId", "1234");
    actual.put("nonToFaceMerchantId", "teststring");
    actual.put("nonToFaceBasicAuthPassword", "teststring");
    actual.put("nonToFaceBasicAuthId", "teststring");
    actual.put("nonToFaceEncryptKey", "teststring");
    actual.put("nonToFaceEncryptIV", "teststring");
    actual.put("nonToFaceCardType", "teststring");
    actual.put("paymentMerchantId", 123);
    actual.put("merchantBasicInfoName", "teststring");
    actual.put("qrGatewayBranchCode", "teststring");
    actual.put("onePayMerchantCode", "teststring");
    Assert.assertNotEquals(expected,
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual));
    Assert.assertEquals(expected.getServiceid(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getServiceid());
    Assert.assertEquals(expected.getTofacemerchantid(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getTofacemerchantid());
    Assert.assertEquals(expected.getNontofacemerchantid(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getNontofacemerchantid());
    Assert.assertEquals(expected.getLinepaychannelsecret(), ConvertDataUtil
        .convertJsonObjectToPaymentSystemLinkInfor(actual).getLinepaychannelsecret());
    Assert.assertEquals(expected.getLinepaychannelid(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getLinepaychannelid());
    Assert.assertEquals(expected.getLastmodifieddate(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getLastmodifieddate());
    Assert.assertEquals(expected.getInternetpaymerstore(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getInternetpaymerstore());
    Assert.assertEquals(expected.getTofacepaymentrouteid(), ConvertDataUtil
        .convertJsonObjectToPaymentSystemLinkInfor(actual).getTofacepaymentrouteid());
    Assert.assertEquals(expected.getNontofacepaymentrouteid(), ConvertDataUtil
        .convertJsonObjectToPaymentSystemLinkInfor(actual).getNontofacepaymentrouteid());
    Assert.assertEquals(expected.getTofacehashkey(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getTofacehashkey());
    Assert.assertEquals(expected.getNontofacehashkey(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getNontofacehashkey());
    Assert.assertEquals(expected.getTofaceencryptkey(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getTofaceencryptkey());
    Assert.assertEquals(expected.getNontofaceencryptkey(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getNontofaceencryptkey());
    Assert.assertEquals(expected.getTofaceencryptiv(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getTofaceencryptiv());
    Assert.assertEquals(expected.getNontofaceencryptiv(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getNontofaceencryptiv());
    Assert.assertEquals(expected.getTofacecardtype(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getTofacecardtype());
    Assert.assertEquals(expected.getNontofacecardtype(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getNontofacecardtype());
    Assert.assertEquals(expected.getTofacebasicauthpassword(), ConvertDataUtil
        .convertJsonObjectToPaymentSystemLinkInfor(actual).getTofacebasicauthpassword());
    Assert.assertEquals(expected.getNontofacebasicauthpassword(), ConvertDataUtil
        .convertJsonObjectToPaymentSystemLinkInfor(actual).getNontofacebasicauthpassword());
    Assert.assertEquals(expected.getAupaystoreid(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getAupaystoreid());
    Assert.assertEquals(expected.getPaymentmerchantid(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getPaymentmerchantid());
    Assert.assertEquals(expected.getMerchantbasicinfoname(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getMerchantbasicinfoname());
    Assert.assertEquals(expected.getQrgatewaybranchcode(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getQrgatewaybranchcode());
    Assert.assertEquals(expected.getOnepaymerchantcode(),
        ConvertDataUtil.convertJsonObjectToPaymentSystemLinkInfor(actual).getOnepaymerchantcode());
  }

  @Test
  public void test_convertBankMaster2Sync() {
    BankMaster expected = new BankMaster();
    expected.setSyncedtime(
        Timestamp.valueOf(LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
    BankMaster actual = new BankMaster();
    Assert.assertNotEquals(actual, ConvertDataUtil.convertBankMaster2Sync(expected, true));
  }

  @Test
  public void test_convertFareTable2Sync() {
    FareTable expected = new FareTable();
    expected.setSyncedtime(
        Timestamp.valueOf(LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
    FareTable actual = new FareTable();
    Assert.assertNotEquals(actual, ConvertDataUtil.convertFareTable2Sync(expected, true));
  }

  @Test
  public void test_convertPaymentSystemLinkInfor2Sync() throws IOException {
    PaymentSystemLinkInfor expected = new PaymentSystemLinkInfor();
    expected.setServiceid("teststring");
    expected.setLinepaychannelsecret("teststring");
    expected.setLinepaychannelid("teststring");
    expected.setLastmodifieddate(
        DateTimeUtil.getTimestampFromString("teststring", DateTimeUtil.DATE_TIME_FM));
    expected.setInternetpaymerstore("teststring");
    expected.setAupaystoreid("teststring");
    expected.setSyncedtime(
        Timestamp.valueOf(LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
    PaymentSystemLinkInfor actual = new PaymentSystemLinkInfor();
    JsonMapper.readDataSync(Constant.PAYMENTSYSTEMLINKINFOR);
    Assert.assertNotEquals(actual,
        ConvertDataUtil.convertPaymentSystemLinkInfor2Sync(expected, true));
  }

  @Test
  public void test_convertJsonObjectToBankMaster() {
    BankMaster expected = new BankMaster();
    expected.setSfid("a04N000000KgpbjIAB");
    expected.setBankcode("teststring");
    expected.setName("teststring");
    expected.setBankname("teststring");
    expected.setBanknamekana("teststring");
    expected.setBranchcode("teststring");
    expected.setBranchname("teststring");
    expected.setBranchnamekana("teststring");
    expected
        .setLastmodifieddate(DateTimeUtil.getTimestampFromString("01:01:01", DateTimeUtil.DD_FM));

    JSONObject actual = new JSONObject();
    actual.put("id", "a04N000000KgpbjIAB");
    actual.put("name", "teststring");
    actual.put("bankName", "teststring");
    actual.put("bankCode", "teststring");
    actual.put("bankNameKana", "teststring");
    actual.put("branchCode", "teststring");
    actual.put("branchName", "teststring");
    actual.put("branchNameKana", "teststring");
    actual.put("syncedtime", "01:01:01");
    actual.put("lastMDate", "teststring");

    Assert.assertNotEquals(expected, ConvertDataUtil.convertJsonObjectToBankMaster(actual));
    Assert.assertEquals(expected.getBankcode(),
        ConvertDataUtil.convertJsonObjectToBankMaster(actual).getBankcode());
    Assert.assertEquals(expected.getName(),
        ConvertDataUtil.convertJsonObjectToBankMaster(actual).getName());
    Assert.assertEquals(expected.getBanknamekana(),
        ConvertDataUtil.convertJsonObjectToBankMaster(actual).getBanknamekana());
    Assert.assertEquals(expected.getBranchcode(),
        ConvertDataUtil.convertJsonObjectToBankMaster(actual).getBranchcode());
    Assert.assertEquals(expected.getBranchname(),
        ConvertDataUtil.convertJsonObjectToBankMaster(actual).getBranchname());
    Assert.assertEquals(expected.getBranchnamekana(),
        ConvertDataUtil.convertJsonObjectToBankMaster(actual).getBranchnamekana());
    Assert.assertEquals(expected.getSyncedtime(),
        ConvertDataUtil.convertJsonObjectToBankMaster(actual).getSyncedtime());
  }

  @Test
  public void test_convertJsonObjectToFareTable() {
    FareTable expected = new FareTable();
    expected.setAppcompany("teststring");
    expected.setAdditionaldistance(Utility.parseInt("teststring"));
    expected.setAdditionalfare(Utility.parseInt("teststring"));
    expected.setBookingfeedescription("teststring");
    expected.setBookingfeeexistence(Utility.parseBoolean("true"));
    expected.setFirstsquaredistance(Utility.parseInt("teststring"));
    expected.setFirstsquarefare(Utility.parseInt("teststring"));
    expected.setLatenightearlymorningextra(Utility.parseInt("teststring"));
    expected.setPickupfare(Utility.parseInt("teststring"));
    expected.setPickupfeedescription("teststring");
    expected.setPickupfeeexistence(Utility.parseBoolean("false"));
    expected.setReservationfee(Utility.parseInt("teststring"));
    expected.setSfid("a04N000000KgpbjIAB");

    JSONObject actual = new JSONObject();
    actual.put("additionalFare", "teststring");
    actual.put("additionalDistance", "teststring");
    actual.put("appCompany", "teststring");
    actual.put("bookingFeeExistence", "teststring");
    actual.put("bookingFeeDesc", "teststring");
    actual.put("reservationFee", "teststring");
    actual.put("pickupFare", "teststring");
    actual.put("pickupFeeDesc", "teststring");
    actual.put("pickupFeeExistence", "teststring");
    actual.put("lateNightEarlyMorningExtra", "teststring");
    actual.put("firstSquareFare", "teststring");
    actual.put("firstSquareDistance", "teststring");
    actual.put("id", "a04N000000KgpbjIAB");
    actual.put("lastMDate", "teststring");

    Assert.assertNotEquals(expected, ConvertDataUtil.convertJsonObjectToFareTable(actual));
    Assert.assertEquals(expected.getSfid(),
        ConvertDataUtil.convertJsonObjectToFareTable(actual).getSfid());
    Assert.assertEquals(expected.getAdditionaldistance(),
        ConvertDataUtil.convertJsonObjectToFareTable(actual).getAdditionaldistance());
    Assert.assertEquals(expected.getAdditionalfare(),
        ConvertDataUtil.convertJsonObjectToFareTable(actual).getAdditionalfare());
    Assert.assertEquals(expected.getFirstsquaredistance(),
        ConvertDataUtil.convertJsonObjectToFareTable(actual).getFirstsquaredistance());
    Assert.assertEquals(expected.getFirstsquarefare(),
        ConvertDataUtil.convertJsonObjectToFareTable(actual).getFirstsquarefare());
    Assert.assertEquals(expected.getLatenightearlymorningextra(),
        ConvertDataUtil.convertJsonObjectToFareTable(actual).getLatenightearlymorningextra());
    Assert.assertEquals(expected.getPickupfare(),
        ConvertDataUtil.convertJsonObjectToFareTable(actual).getPickupfare());
    Assert.assertEquals(expected.getReservationfee(),
        ConvertDataUtil.convertJsonObjectToFareTable(actual).getReservationfee());
  }
}
