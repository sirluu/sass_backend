package jp.co.japantaxi.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Iterator;
import org.json.JSONObject;
import jp.co.japantaxi.model.Account;
import jp.co.japantaxi.model.AppCompany;
import jp.co.japantaxi.model.BankAccountInformation;
import jp.co.japantaxi.model.BankMaster;
import jp.co.japantaxi.model.FareTable;
import jp.co.japantaxi.model.PaymentSystemLinkInfor;

public class ConvertDataUtil {

  public static Account convertJsonObjectToAccount(JSONObject jsonObject) {
    Account account = new Account();
    account.setAffiliationstorenamealphabetic(
        jsonObject.get("aSNameAlphabetic").toString().replace("{}", "").trim());
    account.setAffiliationstorenamekana(jsonObject.get("aSNameKana").toString().replace("{}", "").trim());
    account
        .setAffiliationstorenamekanji(jsonObject.get("aSNameKanji").toString().replace("{}", "").trim());
    account.setAppcompany(jsonObject.get("appCompany").toString().replace("{}", "").trim());
    account.setCorporatenumber(jsonObject.get("corporateNumber").toString().replace("{}", "").trim());
    account.setInvaliddate(
        DateTimeUtil.getDateFromString(jsonObject.get("invalidDate").toString().replace("{}", "").trim(),
            DateTimeUtil.DD_FM, DateTimeUtil.TIMEZONE_UTC));
    account
        .setInvaliddetailreason(jsonObject.get("invalidDetailReason").toString().replace("{}", "").trim());
    account.setInvalidreason(jsonObject.get("invalidReason").toString().replace("{}", "").trim());
    account.setIrisid(jsonObject.get("iris_id").toString().replace("{}", "").trim());
    account.setKeirino(Utility.parseInt(jsonObject.get("keiri_no").toString().replace("{}", "").trim()));
    account.setMerchantcontrinforapplicationcars(
        Utility.parseInt(jsonObject.get("mCIApplicationCars").toString().replace("{}", "").trim()));
    account.setMerchantcontrinforapplicationdate(DateTimeUtil.getTimestampFromString(
        jsonObject.get("mCIApplicationDate").toString().replace("{}", "").trim(), DateTimeUtil.DATE_TIME_FM));
    account.setMerchantcontrinforisfacetofacesettlement(Utility
        .parseBoolean(jsonObject.get("mCIIsFacetofaceSettlement").toString().replace("{}", "").trim()));
    account.setMerchantcontrinforisregistry(
        Utility.parseBoolean(jsonObject.get("mCIIsRegistry").toString().replace("{}", "").trim()));
    account.setMerchantcontrinforlicenseacquisitiondate(DateTimeUtil.getDateFromString(
        jsonObject.get("mCILicenseAcquisitionDate").toString().replace("{}", "").trim(),
        DateTimeUtil.DD_FM, DateTimeUtil.TIMEZONE_UTC));
    account.setMerchantcontrinforlicensename(
        jsonObject.get("mCILicenseName").toString().replace("{}", "").trim());
    account.setMerchantcontrinforlicensenumber(
        jsonObject.get("mCILicenseNumber").toString().replace("{}", "").trim());
    account.setMerchantcontrinfortabletmodel(
        jsonObject.get("mCITabletModel").toString().replace("{}", "").trim());
    account
        .setMerchantdeliveryinforaddress(jsonObject.get("mDIAddress").toString().replace("{}", "").trim());
    account.setMerchantdeliveryinforcontactname(
        jsonObject.get("mDIContactName").toString().replace("{}", "").trim());
	account.setMerchantdeliveryinfordate(DateTimeUtil.getTimestampFromString(
			jsonObject.get("mDIDate").toString().replace("{}", "").trim(), DateTimeUtil.DATE_TIME_FM));
    account.setMerchantdeliveryinforgakkencustomernumber(
        jsonObject.get("mDIGakkenCustomerNumber").toString().replace("{}", "").trim());
    account.setMerchantdeliveryinformemo(jsonObject.get("mDIMemo").toString().replace("{}", "").trim());
    account.setMerchantdeliveryinforname(jsonObject.get("mDIName").toString().replace("{}", "").trim());
    account.setMerchantdeliveryinforphone(jsonObject.get("mDIPhone").toString().replace("{}", "").trim());
    account.setMerchantdeliveryinforpostalcode(
        jsonObject.get("mDIPostalCode").toString().replace("{}", "").trim());
    account.setMerchantdeliveryinforprefecturecode(
        jsonObject.get("mDIPrefectureCode").toString().replace("{}", "").trim());
    account.setMerchantinfoaddress(jsonObject.get("mIAddress").toString().replace("{}", "").trim());
    account
        .setMerchantinfoaddresskana(jsonObject.get("mIAddressKana").toString().replace("{}", "").trim());
    account
        .setMerchantinfocontactemail(jsonObject.get("mIContactEmail").toString().replace("{}", "").trim());
    account
        .setMerchantinfocontactname(jsonObject.get("mIContactName").toString().replace("{}", "").trim());
    account.setMerchantinfoerroraccountbank(
        jsonObject.get("mIErrorAccountBank").toString().replace("{}", "").trim());
    account.setMerchantinfoerroraccountbranch(
        jsonObject.get("mIErrorAccountBranch").toString().replace("{}", "").trim());
    account.setMerchantinfoerroraccountkana(
        jsonObject.get("mIErrorAccountKana").toString().replace("{}", "").trim());
    account.setMerchantinfoerroraccountname(
        jsonObject.get("mIErrorAccountName").toString().replace("{}", "").trim());
    account.setMerchantinfoerroraccountnumber(
        jsonObject.get("mIErrorAccountNumber").toString().replace("{}", "").trim());
    account.setMerchantinfoerroraccounttype(
        jsonObject.get("mIErrorAccountType").toString().replace("{}", "").trim());
    account.setMerchantinfoerrorinquiryemail(
        jsonObject.get("mIErrorInquiryEmail").toString().replace("{}", "").trim());
    account.setMerchantinfoerrorinquiryname(
        jsonObject.get("mIErrorInquiryName").toString().replace("{}", "").trim());
    account.setMerchantinfoerrorinquiryphone(
        jsonObject.get("mIErrorInquiryPhone").toString().replace("{}", "").trim());
    account
        .setMerchantinfoinquiryemail(jsonObject.get("mIInquiryEmail").toString().replace("{}", "").trim());
    account
        .setMerchantinfoinquiryname(jsonObject.get("mIInquiryName").toString().replace("{}", "").trim());
    account
        .setMerchantinfoinquiryphone(jsonObject.get("mIInquiryPhone").toString().replace("{}", "").trim());
    account.setMerchantinfoisrepresentative(
        Utility.parseBoolean(jsonObject.get("mIIsRepresentative").toString().replace("{}", "").trim()));
    account.setMerchantinfometerlist(jsonObject.get("mIMeterList").toString().replace("{}", "").trim());
    account.setMerchantinfopaymentcontactname(
        jsonObject.get("mIPaymentContactName").toString().replace("{}", "").trim());
    account.setMerchantinfophonenumberhyphen(
        jsonObject.get("mIPhoneNumberHyphen").toString().replace("{}", "").trim());
    account.setMerchantinfopostalcode(jsonObject.get("mIPostalCode").toString().replace("{}", "").trim());
    account.setMerchantinfoprefecturecode(
        jsonObject.get("mIPrefectureCode").toString().replace("{}", "").trim());
    account.setMerchantinforadename(jsonObject.get("mIradeName").toString().replace("{}", "").trim());
    account.setMerchantinforemarks(jsonObject.get("mIRemarks").toString().replace("{}", "").trim());
    account.setMerchantinfoserviceid(jsonObject.get("mIServiceID").toString().replace("{}", "").trim());
    account
        .setMerchantinfowarningemail(jsonObject.get("mIWarningEmail").toString().replace("{}", "").trim());
    account.setMerchantoperinforacceptancedate(DateTimeUtil.getDateFromString(
        jsonObject.get("mOIAcceptanceDate").toString().replace("{}", "").trim(), DateTimeUtil.DD_FM,
        DateTimeUtil.TIMEZONE_UTC));
    account.setMerchantoperinforcontractenddate(DateTimeUtil.getDateFromString(
        jsonObject.get("mOIContractEndDate").toString().replace("{}", "").trim(), DateTimeUtil.DD_FM,
        DateTimeUtil.TIMEZONE_UTC));
    account.setMerchantoperinforguaranteeenddate(DateTimeUtil.getDateFromString(
        jsonObject.get("mOIGuaranteeEndDate").toString().replace("{}", "").trim(), DateTimeUtil.DD_FM,
        DateTimeUtil.TIMEZONE_UTC));
    account.setMerchantoperinformemo(jsonObject.get("mOIMemo").toString().replace("{}", "").trim());
    account.setMerchantoperinforstartdate(
        DateTimeUtil.getDateFromString(jsonObject.get("mOIStartDate").toString().replace("{}", "").trim(),
            DateTimeUtil.DD_FM, DateTimeUtil.TIMEZONE_UTC));
    account.setMerchantpayinfoaddress1(
        jsonObject.get("mPayInfoAddress1").toString().replace("{}", "").trim());
    account.setMerchantpayinfoaddress2(
        jsonObject.get("mPayInfoAddress2").toString().replace("{}", "").trim());
    account.setMerchantpayinfocontactname(
        jsonObject.get("mPayInfoContactName").toString().replace("{}", "").trim());
    account.setMerchantpayinfoitabbankaccount(jsonObject.get("mPayInfoItabBAcc").toString().replace("{}", "").trim());
    account.setMerchantpayinfonetbankaccount(jsonObject.get("mPayInfoNetBAcc").toString().replace("{}", "").trim());
    account.setMerchantpayinfoname(jsonObject.get("mPayInfoName").toString().replace("{}", "").trim());
    account.setMerchantpayinfopostalcode(
        jsonObject.get("mPayInfoPostalCode").toString().replace("{}", "").trim());
    account.setMerchantreprinforaddress(jsonObject.get("mRIAddress").toString().replace("{}", "").trim());
    account.setMerchantreprinforbirthday(
        DateTimeUtil.getDateFromString(jsonObject.get("mRIBirthday").toString().replace("{}", "").trim(),
            DateTimeUtil.DD_FM, DateTimeUtil.TIMEZONE_UTC));
    account.setMerchantreprinforfullnamekana(
        jsonObject.get("mRIFullNameKana").toString().replace("{}", "").trim());
    account.setMerchantreprinforfullnamekanji(
        jsonObject.get("mRIFullNameKanji").toString().replace("{}", "").trim());
    account.setMerchantreprinformemo(jsonObject.get("mRIMemo").toString().replace("{}", "").trim());
    account.setMerchantreprinforphone(jsonObject.get("mRIPhone").toString().replace("{}", "").trim());
    account.setMerchantreprinforpostalcode(
        jsonObject.get("mRIPostalCode").toString().replace("{}", "").trim());
    account.setMerchantreprinforprefecturecode(
        jsonObject.get("mRIPrefectureCode").toString().replace("{}", "").trim());
    account.setMerchantreprinforsex(jsonObject.get("mRISex").toString().replace("{}", "").trim());
    account.setMerchantstoreinforaddress(jsonObject.get("mSIAddress").toString().replace("{}", "").trim());
    account.setMerchantstoreinforaddresskana(
        jsonObject.get("mSIAddressKana").toString().replace("{}", "").trim());
    account.setMerchantstoreinformemo(jsonObject.get("mSIMemo").toString().replace("{}", "").trim());
    account.setMerchantstoreinforphone(jsonObject.get("mSIPhone").toString().replace("{}", "").trim());
    account.setMerchantstoreinforpostalcode(
        jsonObject.get("mSIPostalCode").toString().replace("{}", "").trim());
    account.setMerchantstoreinforprefecturecode(
        jsonObject.get("mSIPrefectureCode").toString().replace("{}", "").trim());
    account.setMerchantstoreinforstorenamealphabet(
        jsonObject.get("mSIStoreNameAlphabet").toString().replace("{}", "").trim());
    account.setMerchantstoreinforstorenamehalfwidthkana(
        jsonObject.get("mSIStoreNameHalfwidthKana").toString().replace("{}", "").trim());
    account.setMerchantstoreinforstorenamekanji(
        jsonObject.get("mSIStoreNameKanji").toString().replace("{}", "").trim());
    account.setMertermno(jsonObject.get("merTermNo").toString().replace("{}", "").trim());
    account.setLastmodifieddate(DateTimeUtil.getTimestampFromString(
        jsonObject.get("lastMDate").toString().replace("{}", "").trim(), DateTimeUtil.DATE_TIME_FM));
    account.setSfid(jsonObject.get("sfid").toString().replace("{}", "").trim());
    
    account.setWebsite(jsonObject.get("website").toString().replace("{}", "").trim());
    account.setAppcompanyid(Utility.parseInt(jsonObject.get("appCompanyID").toString().replace("{}", "").trim()));
    account.setJpporganizationid(jsonObject.get("jppOrgID").toString().replace("{}", "").trim());
    return account;
  }

  public static Account convertAccount2Sync(Account account, boolean sync) {
    if (sync) {
      account.setSyncedtime(Timestamp.valueOf(LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
    }
    return account;
  }

  public static AppCompany convertJsonObjectToAppCompany(JSONObject jsonObject) {
    AppCompany appCompany = new AppCompany();
    appCompany.setAcceptmajor2(
        Utility.parseFloat(jsonObject.get("acceptMajor2").toString().replace("{}", "").trim()));
    appCompany.setAcceptminor2(
        Utility.parseFloat(jsonObject.get("acceptMinor2").toString().replace("{}", "").trim()));
    appCompany.setAccount(jsonObject.get("account").toString().replace("{}", "").trim());
    appCompany.setJtxcompanyid(Utility.parseInt(jsonObject.get("jtxCompanyID").toString().replace("{}", "").trim()));
    appCompany.setAppdisplayname(jsonObject.get("appDisplayName").toString().replace("{}", "").trim());

    appCompany.setBookingfeedescription(
        jsonObject.get("bookingFeeDescription").toString().replace("{}", "").trim());
    appCompany.setBankaccount(jsonObject.get("bankAccount").toString().replace("{}", "").trim());
    appCompany.setBankaccount2(jsonObject.get("bankAccount2").toString().replace("{}", "").trim());
    appCompany.setCarimagecolor1(jsonObject.get("carIColor1").toString().replace("{}", "").trim());
    appCompany.setCarimagecolor2(jsonObject.get("carIColor2").toString().replace("{}", "").trim());
    appCompany.setCarimagecolor3(jsonObject.get("carIColor3").toString().replace("{}", "").trim());
    appCompany.setCarimagecolor4(jsonObject.get("carIColor4").toString().replace("{}", "").trim());
    appCompany.setCarimagecolor5(jsonObject.get("carIColor5").toString().replace("{}", "").trim());
    appCompany.setCarimagecolor6(jsonObject.get("carIColor6").toString().replace("{}", "").trim());
    appCompany.setCarimagecolor7(jsonObject.get("carIColor7").toString().replace("{}", "").trim());
    appCompany.setCarimagecolor8(jsonObject.get("carIColor8").toString().replace("{}", "").trim());
    appCompany.setCarimagename1(jsonObject.get("carIName1").toString().replace("{}", "").trim());
    appCompany.setCarimagename2(jsonObject.get("carIName2").toString().replace("{}", "").trim());
    appCompany.setCarimagename3(jsonObject.get("carIName3").toString().replace("{}", "").trim());
    appCompany.setCarimagename4(jsonObject.get("carIName4").toString().replace("{}", "").trim());
    appCompany.setCarimagename5(jsonObject.get("carIName5").toString().replace("{}", "").trim());
    appCompany.setCarimagename6(jsonObject.get("carIName6").toString().replace("{}", "").trim());
    appCompany.setCarimagename7(jsonObject.get("carIName7").toString().replace("{}", "").trim());
    appCompany.setCarimagename8(jsonObject.get("carIName8").toString().replace("{}", "").trim());
    appCompany.setCars(Utility.parseInt(jsonObject.get("cars").toString().replace("{}", "").trim()));
    appCompany.setClosingtimeto(DateTimeUtil.getTimestampFromString(
        jsonObject.get("closingTimeTo").toString().replace("{}", "").trim(), DateTimeUtil.TIME_FM_S));
    appCompany.setClosingtimefrom(DateTimeUtil.getTimestampFromString(
        jsonObject.get("closingTimeFrom").toString().replace("{}", "").trim(), DateTimeUtil.TIME_FM_S));
    appCompany.setCommunicationurl(jsonObject.get("communicationUrl").toString().replace("{}", "").trim());
    appCompany.setCompanylogoimage(jsonObject.get("companyLogoImage").toString().replace("{}", "").trim());
    appCompany.setCompanynameen(jsonObject.get("companyNameEN").toString().replace("{}", "").trim());

    appCompany.setDestinationaddress(jsonObject.get("desAddr").toString().replace("{}", "").trim());
    appCompany.setDestinationcompanyname(jsonObject.get("desCompName").toString().replace("{}", "").trim());
    appCompany.setDestinationcontactname(jsonObject.get("desContName").toString().replace("{}", "").trim());
    appCompany.setDestinationpostalcode(jsonObject.get("desPostCode").toString().replace("{}", "").trim());
    appCompany.setDestinationprefecture(jsonObject.get("desPrefec").toString().replace("{}", "").trim());
    appCompany.setDispatchtype(jsonObject.get("dispatchType").toString().replace("{}", "").trim());
    appCompany.setDisplaysalescity(jsonObject.get("displaySalesCity").toString().replace("{}", "").trim());
    appCompany.setDisplaysalescityen(jsonObject.get("displaySalesCityEN").toString().replace("{}", "").trim());
    appCompany.setEmail(jsonObject.get("email").toString().replace("{}", "").trim());
    appCompany.setExtendedfieldmap(jsonObject.get("extendedFieldMap").toString().replace("{}", "").trim());
    appCompany.setFaretable(jsonObject.get("fareTable").toString().replace("{}", "").trim());
    appCompany.setGloballat(jsonObject.get("globalLat").toString().replace("{}", "").trim());
    appCompany.setGloballng(jsonObject.get("globalLng").toString().replace("{}", "").trim());
    appCompany.setGroupcompanies(Utility.parseInt(jsonObject.get("groupCompanies").toString().replace("{}", "").trim()));
    appCompany.setInvaliddate(DateTimeUtil.getDateFromString(jsonObject.get("invalidDate").toString().replace("{}", "").trim(),
        DateTimeUtil.DD_FM, DateTimeUtil.TIMEZONE_UTC));
    appCompany.setInvaliddetailreason(jsonObject.get("invalidDetailReason").toString().replace("{}", "").trim());
    appCompany.setInvalidreason(jsonObject.get("invalidReason").toString().replace("{}", "").trim());
    appCompany.setIsenableclosing(
        Utility.parseBoolean(jsonObject.get("isEnableClosing").toString().replace("{}", "").trim()));
    appCompany.setIsexternalcancel(
        Utility.parseBoolean(jsonObject.get("isExternalCancel").toString().replace("{}", "").trim()));
    appCompany.setIsradionumberedit(
        Utility.parseBoolean(jsonObject.get("isRadioNumberEdit").toString().replace("{}", "").trim()));
    appCompany.setLogoimage(jsonObject.get("logoImage").toString().replace("{}", "").trim());
    appCompany.setMinutestowaitforpickup(
        jsonObject.get("minutesToWaitForPickUp").toString().replace("{}", "").trim());
    appCompany.setName(jsonObject.get("name").toString().replace("{}", "").trim());
    appCompany.setNetpaytypes(jsonObject.get("netPayTypes").toString().replace("{}", "").trim());
    appCompany.setOrdercancelurl(jsonObject.get("orderCancelUrl").toString().replace("{}", "").trim());
    appCompany.setOrderurl(jsonObject.get("orderUrl").toString().replace("{}", "").trim());
    appCompany.setPartnerapiversion(Utility.parseInt(jsonObject.get("partnerApiVersion").toString().replace("{}", "").trim()));
    appCompany.setPaymentmethod(jsonObject.get("paymentMethod").toString().replace("{}", "").trim());
    appCompany.setPaymentmethoden(jsonObject.get("payMethodEN").toString().replace("{}", "").trim());
    appCompany.setPhonenumber(jsonObject.get("phoneNumber").toString().replace("{}", "").trim());
    appCompany.setPhonenumbercancel(jsonObject.get("phoneNumberCancel").toString().replace("{}", "").trim());
    appCompany.setPickupfeedescription(
        jsonObject.get("pickupFeeDescription").toString().replace("{}", "").trim());
    appCompany.setProfile(jsonObject.get("profile").toString().replace("{}", "").trim());
    appCompany.setLastmodifieddate(DateTimeUtil.getTimestampFromString(
        jsonObject.get("lastMDate").toString().replace("{}", "").trim(), DateTimeUtil.DATE_TIME_FM));
    appCompany.setRatingaverage(
        Utility.parseFloat(jsonObject.get("ratingAverage").toString().replace("{}", "").trim()));
    appCompany.setReplywatingminute(
        Utility.parseInt(jsonObject.get("replyWatingMinute").toString().replace("{}", "").trim()));
    appCompany.setSalescity(jsonObject.get("salesCity").toString().replace("{}", "").trim());
    appCompany.setArea(jsonObject.get("areaId").toString().replace("{}", "").trim());
    appCompany.setAreacode(jsonObject.get("areaCode").toString().replace("{}", "").trim());
    appCompany.setSalesprefecture(jsonObject.get("salesPrefecture").toString().replace("{}", "").trim());
    appCompany.setSalesprefectureen(jsonObject.get("salesPrefeEN").toString().replace("{}", "").trim());
    appCompany.setShowbookingcancel(Utility.parseBoolean(jsonObject.get("showBookingCancel").toString().replace("{}", "").trim()));
    appCompany.setShowdest(Utility.parseBoolean(jsonObject.get("showDest").toString().replace("{}", "").trim()));
    appCompany.setShowemptycar(
        Utility.parseBoolean(jsonObject.get("showEmptyCar").toString().replace("{}", "").trim()));
    appCompany.setShowimmediatecancel(
        Utility.parseBoolean(jsonObject.get("showImmediateCancel").toString().replace("{}", "").trim()));
    appCompany.setShowminutestowaitforpickup(Utility
        .parseBoolean(jsonObject.get("showMinutesToWaitForPickUp").toString().replace("{}", "").trim()));
    appCompany.setShowmoving(
        Utility.parseBoolean(jsonObject.get("showMoving").toString().replace("{}", "").trim()));
    appCompany.setShowmustarrivaltime(
        Utility.parseBoolean(jsonObject.get("showMustArrivalTime").toString().replace("{}", "").trim()));
    appCompany.setShowvisit(jsonObject.get("showVisit").toString().replace("{}", "").trim());
    appCompany.setStatus(jsonObject.get("status").toString().replace("{}", "").trim());
    appCompany.setSfid(jsonObject.get("id").toString().replace("{}", "").trim());
    appCompany.setWww(jsonObject.get("www").toString().replace("{}", "").trim());
    appCompany.setWaitforpickupdefault(Utility.parseInt(jsonObject.get("waitForPickUpDefault").toString().replace("{}", "").trim()));
    appCompany.setWarningemail(jsonObject.get("warningEmail").toString().replace("{}", "").trim());
    appCompany.setDrivercontract(jsonObject.get("driverContract").toString().replace("{}", "").trim());
	appCompany.setImplementationcosts(Utility.parseInt(jsonObject.get("implementationCosts").toString().replace("{}", "").trim()));
	appCompany.setMonthlyminimumcars(Utility.parseInt(jsonObject.get("monthlyMinimumCars").toString().replace("{}", "").trim()));
	appCompany.setMonthlyminimumcharge(Utility.parseInt(jsonObject.get("monthlyMinimumCharge").toString().replace("{}", "").trim()));
	appCompany.setPayablecommission(Utility.parseInt(jsonObject.get("payableCommission").toString().replace("{}", "").trim()));
	appCompany.setRemarks(jsonObject.get("remarks").toString().replace("{}", "").trim());
	appCompany.setRequestcompanyidrelation(Utility.parseInt(jsonObject.get("requestCompanyIDRelation").toString().replace("{}", "").trim()));
	appCompany.setRequeststartdate(DateTimeUtil.getDateFromString(jsonObject.get("requestStartDate").toString().replace("{}", "").trim(), 
	    DateTimeUtil.DD_FM, DateTimeUtil.TIMEZONE_UTC));
    return appCompany;
  }

  public static AppCompany convertAppCompany2Sync(AppCompany appCompany, boolean sync) {
    if (sync) {
      appCompany
      .setSyncedtime(Timestamp.valueOf(LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
    }

    String key = null;
    JSONObject dispatch = JsonMapper.object.getJSONObject("dispatch");
    if (!dispatch.isEmpty()) {
      Iterator<?> keysItr = dispatch.keySet().iterator();
      while (keysItr.hasNext()) {
        key = (String) keysItr.next();
        if (appCompany.getDispatchtype().contains(key)) {
          if (dispatch.getString(key).equalsIgnoreCase("manual")) {
            appCompany.setDispatchtypeisexternaldispatch(false);
            appCompany.setDispatchtypepartnerapiversion(null);
          } else {
            appCompany.setDispatchtypeisexternaldispatch(true);
            appCompany.setDispatchtypepartnerapiversion(3);
          }
        }
      }
    }
	appCompany.setPhonenumber(appCompany.getPhonenumber().replace("-", "").replace("_", ""));
	appCompany.setPhonenumbercancel(appCompany.getPhonenumbercancel().replace("-", "").replace("_", ""));
    appCompany.setCompanylogoimage(appCompany.getCompanylogoimage().substring(appCompany.getCompanylogoimage().lastIndexOf('/') + 1));
    
    appCompany.setDrivercontractdispatchmethodid(1);
    appCompany.setDrivercontractisenabled(false);
    appCompany.setDrivercontractispreviewenabled(false);
    appCompany.setDrivercontractonlyusewhennecessary(false);
    appCompany.setDrivercontractuseasdefault(false);
    return appCompany;
  }

  public static BankAccountInformation convertJsonObjectToBankAccountInformation(
      JSONObject jsonObject) {
    BankAccountInformation bankAccountInformation = new BankAccountInformation();
    bankAccountInformation.setAccount(jsonObject.get("account").toString().replace("{}", "").trim());
    bankAccountInformation.setAppcompany(jsonObject.get("appcompany").toString().replace("{}", "").trim());
    bankAccountInformation
        .setAccountbank(jsonObject.get("accountBank").toString().replace("{}", "").trim());
    bankAccountInformation
        .setAccountcategory(jsonObject.get("accountCategory").toString().replace("{}", "").trim());
    bankAccountInformation
        .setAccountname(jsonObject.get("accountName").toString().replace("{}", "").trim());
    bankAccountInformation
        .setAccountnamekana(jsonObject.get("accountNameKana").toString().replace("{}", "").trim());
    bankAccountInformation
        .setAccountnumber(jsonObject.get("accountNumber").toString().replace("{}", "").trim());
    bankAccountInformation
        .setAccounttype(jsonObject.get("accountType").toString().replace("{}", "").trim());
    bankAccountInformation.setInvaliddate(
        DateTimeUtil.getDateFromString(jsonObject.get("invalidDate").toString().replace("{}", "").trim(),
            DateTimeUtil.DD_FM_S, DateTimeUtil.TIMEZONE_UTC));
    bankAccountInformation
        .setInvalidreason(jsonObject.get("invalidReason").toString().replace("{}", "").trim());
    bankAccountInformation.setLastmodifieddate(DateTimeUtil.getTimestampFromString(
        jsonObject.get("lastMDate").toString().replace("{}", "").trim(), DateTimeUtil.DATE_TIME_FM));
    bankAccountInformation.setSfid(jsonObject.get("id").toString().replace("{}", "").trim());
    return bankAccountInformation;
  }

  public static BankAccountInformation convertBankAccountInformation2Sync(
      BankAccountInformation bankAccountInformation, boolean sync) {
    
    if (sync) {
      bankAccountInformation
      .setSyncedtime(Timestamp.valueOf(LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
    }

    String key = null;
    JSONObject accounttype = JsonMapper.object.getJSONObject("accounttype");
    if (!accounttype.isEmpty()) {
      Iterator<?> keysItr = accounttype.keySet().iterator();
      while (keysItr.hasNext()) {
        key = (String) keysItr.next();
        if (bankAccountInformation.getAccounttype().contains(key)) {
        	bankAccountInformation.setAccounttype(accounttype.getString(key));
        }
      }
    }
    
    return bankAccountInformation;
  }

  public static BankMaster convertJsonObjectToBankMaster(JSONObject jsonObject) {
    BankMaster bankMaster = new BankMaster();
    bankMaster.setName(jsonObject.get("name").toString().replace("{}", "").trim());
    bankMaster.setBankname(jsonObject.get("bankName").toString().replace("{}", "").trim());
    bankMaster.setBankcode(jsonObject.get("bankCode").toString().replace("{}", "").trim());
    bankMaster.setBanknamekana(jsonObject.get("bankNameKana").toString().replace("{}", "").trim());
    bankMaster.setBranchcode(jsonObject.get("branchCode").toString().replace("{}", "").trim());
    bankMaster.setBranchname(jsonObject.get("branchName").toString().replace("{}", "").trim());
    bankMaster.setBranchnamekana(jsonObject.get("branchNameKana").toString().replace("{}", "").trim());
    bankMaster.setLastmodifieddate(DateTimeUtil.getTimestampFromString(
        jsonObject.get("lastMDate").toString().replace("{}", "").trim(), DateTimeUtil.DATE_TIME_FM));
    bankMaster.setSfid(jsonObject.get("id").toString().replace("{}", "").trim());
    return bankMaster;
  }

  public static BankMaster convertBankMaster2Sync(BankMaster bankMaster, boolean sync) {
    if (sync) {
      bankMaster
      .setSyncedtime(Timestamp.valueOf(LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
    }
    return bankMaster;
  }

  public static FareTable convertJsonObjectToFareTable(JSONObject jsonObject) {
    FareTable fareTable = new FareTable();
    fareTable.setAdditionaldistance(
        Utility.parseInt(jsonObject.get("additionalDistance").toString().replace("{}", "").trim()));
    fareTable.setAdditionalfare(
        Utility.parseInt(jsonObject.get("additionalFare").toString().replace("{}", "").trim()));
    fareTable.setAppcompany(jsonObject.get("appCompany").toString().replace("{}", "").trim());
    fareTable
        .setBookingfeedescription(jsonObject.get("bookingFeeDesc").toString().replace("{}", "").trim());
    fareTable.setBookingfeeexistence(
        Utility.parseBoolean(jsonObject.get("bookingFeeExistence").toString().replace("{}", "").trim()));
    fareTable.setFirstsquaredistance(
        Utility.parseInt(jsonObject.get("firstSquareDistance").toString().replace("{}", "").trim()));
    fareTable.setFirstsquarefare(
        Utility.parseInt(jsonObject.get("firstSquareFare").toString().replace("{}", "").trim()));
    fareTable.setLastmodifieddate(DateTimeUtil.getTimestampFromString(
        jsonObject.get("lastMDate").toString().replace("{}", "").trim(), DateTimeUtil.DATE_TIME_FM));
    fareTable.setLatenightearlymorningextra(Utility
        .parseInt(jsonObject.get("lateNightEarlyMorningExtra").toString().replace("{}", "").trim()));
    fareTable
        .setPickupfare(Utility.parseInt(jsonObject.get("pickupFare").toString().replace("{}", "").trim()));
    fareTable.setPickupfeedescription(jsonObject.get("pickupFeeDesc").toString().replace("{}", "").trim());
    fareTable.setPickupfeeexistence(
        Utility.parseBoolean(jsonObject.get("pickupFeeExistence").toString().replace("{}", "").trim()));
    fareTable.setReservationfee(
        Utility.parseInt(jsonObject.get("reservationFee").toString().replace("{}", "").trim()));
    fareTable.setSfid(jsonObject.get("id").toString().replace("{}", "").trim());
    return fareTable;
  }

  public static FareTable convertFareTable2Sync(FareTable fareTable, boolean sync) {
    if (sync) {
      fareTable.setSyncedtime(Timestamp.valueOf(LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
    }
    return fareTable;
  }

  public static PaymentSystemLinkInfor convertJsonObjectToPaymentSystemLinkInfor(
      JSONObject jsonObject) {
    PaymentSystemLinkInfor linkInforc = new PaymentSystemLinkInfor();
    linkInforc.setAupaystoreid(jsonObject.get("auPayStoreId").toString().replace("{}", "").trim());
    linkInforc.setTofacebasicauthid(jsonObject.get("toFaceBasicAuthId").toString().replace("{}", "").trim());
    linkInforc.setNontofacebasicauthid(jsonObject.get("nonToFaceBasicAuthId").toString().replace("{}", "").trim());

    linkInforc.setTofacebasicauthpassword(jsonObject.get("toFaceBasicAuthPassword").toString().replace("{}", "").trim());
    linkInforc.setNontofacebasicauthpassword(jsonObject.get("nonToFaceBasicAuthPassword").toString().replace("{}", "").trim());

    linkInforc.setTofacecardtype(jsonObject.get("toFaceCardType").toString().replace("{}", "").trim());
    linkInforc.setNontofacecardtype(jsonObject.get("nonToFaceCardType").toString().replace("{}", "").trim());

    linkInforc.setTofaceencryptiv(jsonObject.get("toFaceEncryptIV").toString().replace("{}", "").trim());
    linkInforc.setNontofaceencryptiv(jsonObject.get("nonToFaceEncryptIV").toString().replace("{}", "").trim());

    linkInforc.setTofaceencryptkey(jsonObject.get("toFaceEncryptKey").toString().replace("{}", "").trim());
    linkInforc.setNontofaceencryptkey(jsonObject.get("nonToFaceEncryptKey").toString().replace("{}", "").trim());

    linkInforc.setTofacehashkey(jsonObject.get("toFaceHashKey").toString().replace("{}", "").trim());
    linkInforc.setNontofacehashkey(jsonObject.get("nonToFaceHashKey").toString().replace("{}", "").trim());

    linkInforc.setInternetpaymerstore(jsonObject.get("internetPayMerStore").toString().replace("{}", "").trim());
    linkInforc.setLastmodifieddate(DateTimeUtil.getTimestampFromString(jsonObject.get("lastMDate").toString().replace("{}", "").trim(), DateTimeUtil.DATE_TIME_FM));
    linkInforc.setLinepaychannelsecret(jsonObject.get("linePayChannelSecret").toString().replace("{}", "").trim());
    linkInforc.setLinepaychannelid(jsonObject.get("linePayChannelId").toString().replace("{}", "").trim());
 
    linkInforc.setTofacemerchantid(jsonObject.get("toFaceMerchantId").toString().replace("{}", "").trim());
    linkInforc.setNontofacemerchantid(jsonObject.get("nonToFaceMerchantId").toString().replace("{}", "").trim());

    linkInforc.setTofacepaymentrouteid(Utility.parseInt(jsonObject.get("toFacePaymentRouteId").toString().replace("{}", "").trim()));
    linkInforc.setNontofacepaymentrouteid(Utility.parseInt(jsonObject.get("nonToFacePaymentRouteId").toString().replace("{}", "").trim()));

    linkInforc.setServiceid(jsonObject.get("serviceId").toString().replace("{}", "").trim());
    linkInforc.setSfid(jsonObject.get("id").toString().replace("{}", "").trim());
    linkInforc.setAppcompany(jsonObject.get("appCompany").toString().replace("{}", "").trim());

    return linkInforc;
  }
  
  public static PaymentSystemLinkInfor convertPaymentSystemLinkInfor2Sync(
      PaymentSystemLinkInfor linkInfor, boolean sync) {
    if (sync) {
      linkInfor.setSyncedtime(Timestamp.valueOf(LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
    }
    return linkInfor;
  }

}
