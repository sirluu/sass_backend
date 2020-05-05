package jp.co.japantaxi.model;

import java.sql.Timestamp;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Account extends ObjectSync{
  /**
   * 88 fields + sfid
   */
  private String appcompany;
  private String affiliationstorenamealphabetic;
  private String affiliationstorenamekana;
  private String affiliationstorenamekanji;
  private String website;
  private Integer keirino;
  private String corporatenumber;
  private String mertermno;
  private String irisid;
  private Boolean merchantinfoisrepresentative;
  private String merchantinfoinquiryname;
  private String merchantinfoinquiryphone;
  private String merchantinfowarningemail;
  private String merchantinfoinquiryemail;
  private String merchantinfoerrorinquiryname;
  private String merchantinfoerrorinquiryphone;
  private String merchantinfoerrorinquiryemail;
  private String merchantinfoerroraccountbank;
  private String merchantinfoerroraccountbranch;
  private String merchantinfoerroraccounttype;
  private String merchantinfoerroraccountnumber;
  private String merchantinfoerroraccountname;
  private String merchantinfoerroraccountkana;
  private String merchantinfoserviceid;
  private String merchantinfopaymentcontactname;
  private String merchantinforemarks;
  private String merchantinforadename;
  private String merchantinfopostalcode;
  private String merchantinfoprefecturecode;
  private String merchantinfoaddress;
  private String merchantinfoaddresskana;
  private String merchantinfophonenumberhyphen;
  private String merchantinfocontactname;
  private String merchantinfocontactemail;
  private String merchantinfometerlist;
  private Integer merchantcontrinforapplicationcars;
  private String merchantcontrinfortabletmodel;
  private Date merchantcontrinforapplicationdate;
  private Boolean merchantcontrinforisfacetofacesettlement;
  private Boolean merchantcontrinforisregistry;
  private String merchantcontrinforlicensename;
  private String merchantcontrinforlicensenumber;
  private Date merchantcontrinforlicenseacquisitiondate;
  private String merchantreprinforfullnamekanji;
  private String merchantreprinforfullnamekana;
  private String merchantreprinforpostalcode;
  private String merchantreprinforprefecturecode;
  private String merchantreprinforaddress;
  private String merchantreprinforphone;
  private Date merchantreprinforbirthday;
  private String merchantreprinforsex;
  private String merchantreprinformemo;
  private String merchantstoreinforstorenamekanji;
  private String merchantstoreinforstorenamehalfwidthkana;
  private String merchantstoreinforstorenamealphabet;
  private String merchantstoreinforpostalcode;
  private String merchantstoreinforprefecturecode;
  private String merchantstoreinforaddress;
  private String merchantstoreinforaddresskana;
  private String merchantstoreinforphone;
  private String merchantstoreinformemo;
  private Date merchantdeliveryinfordate;
  private String merchantdeliveryinforgakkencustomernumber;
  private String merchantdeliveryinforname;
  private String merchantdeliveryinforpostalcode;
  private String merchantdeliveryinforprefecturecode;
  private String merchantdeliveryinforaddress;
  private String merchantdeliveryinforcontactname;
  private String merchantdeliveryinforphone;
  private String merchantdeliveryinformemo;
  private String merchantpayinfonetbankaccount;
  private String merchantpayinfoitabbankaccount;
  private String merchantpayinfoname;
  private String merchantpayinfopostalcode;
  private String merchantpayinfoaddress1;
  private String merchantpayinfoaddress2;
  private String merchantpayinfocontactname;
  private Date merchantoperinforstartdate;
  private Date merchantoperinforacceptancedate;
  private Date merchantoperinforguaranteeenddate;
  private Date merchantoperinforcontractenddate;
  private String merchantoperinformemo;
  private String thincacloudmertermno;
  private String linkmertermno;
  private Date invaliddate;
  private String invalidreason;
  private String invaliddetailreason;
  private Timestamp lastmodifieddate;
  private Integer appcompanyid;
  private String jpporganizationid;
}
