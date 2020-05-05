package jp.co.japantaxi.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AppCompany extends ObjectSync {
  /**
   * 56 fields + sfid + account + syncedtime
   */
  private String name;
  private String faretable;
  private Integer jtxcompanyid;
  private String appdisplayname;
  private String status;
  private Float acceptmajor2;
  private Float acceptminor2;
  private String dispatchtype;
  //sync table
  private Boolean dispatchtypeisexternaldispatch;
  private Integer dispatchtypepartnerapiversion;
  //sync table
  private String phonenumber;
  private String phonenumbercancel;
  private String email;
  private String warningemail;
  private String companylogoimage;
  private String area;
  private String areacode;
  private String salesprefecture;
  private String salesprefectureen;
  private String salescity;
  private String profile;
  private Integer cars;
  private Boolean isenableclosing;
  private String displaysalescity;
  private Boolean showdest;
  private Boolean showmoving;
  private Boolean showmustarrivaltime;
  private String showvisit;
  private Boolean showbookingcancel;
  private String extendedfieldmap;
  private Timestamp closingtimefrom;
  private Timestamp closingtimeto;
  private String globallat;
  private String globallng;
  private String companynameen;
  private String displaysalescityen;
  private String pickupfeedescription;
  private String bookingfeedescription;
  private Integer partnerapiversion;
  private Boolean showemptycar;
  private Float ratingaverage;
  private Integer waitforpickupdefault;
  private Boolean showminutestowaitforpickup;
  private Boolean isradionumberedit;
  private Boolean isexternalcancel;
  private Boolean showimmediatecancel;
  private String netpaytypes;
  private String paymentmethod;
  private String paymentmethoden;
  private String minutestowaitforpickup;
  private String carimagecolor;
  private String carimagename;
  private String logoimage;
  private Integer replywatingminute;
  private Integer groupcompanies;
  private String communicationurl;
  private String orderurl;
  private String ordercancelurl;
  private Date invaliddate;
  private String invalidreason;
  private String invaliddetailreason;
  //sync table
  private Integer drivercontractdispatchmethodid;
  private Boolean drivercontractisenabled;
  private Boolean drivercontractispreviewenabled;
  private Boolean drivercontractonlyusewhennecessary;
  private Boolean drivercontractuseasdefault;
  //sync table
  private String drivercontract;
  private Integer implementationcosts;
  private Integer monthlyminimumcars;
  private Integer monthlyminimumcharge;
  private String remarks;
  private Integer payablecommission;
  private Date requeststartdate;
  private Integer requestcompanyidrelation;
  private String bankaccount;
  private String bankaccount2;
  private String www;
  private String destinationprefecture;
  private String destinationaddress;
  private String destinationpostalcode;
  private String destinationcontactname;
  private String destinationcompanyname;
  private Timestamp lastmodifieddate;
  // Map select multi table.
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Map<?, ?> faretables;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<Object> accounts;
}
