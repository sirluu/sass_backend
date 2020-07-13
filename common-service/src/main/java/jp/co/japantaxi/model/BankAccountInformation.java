package jp.co.japantaxi.model;

import java.sql.Timestamp;
import java.util.Date;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BankAccountInformation extends ObjectSync {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  /**
   * 9 fields + sfid + account
   */
  private String appcompany;
  private String appcompanyid;
  private String accountcategory;
  private String accountbank;
  private String accountnumber;
  private String accountname;
  private String accountnamekana;
  private String accounttype;
  private Date invaliddate;
  private String invalidreason;
  private Timestamp lastmodifieddate;
}
