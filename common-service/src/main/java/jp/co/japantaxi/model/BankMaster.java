package jp.co.japantaxi.model;

import java.sql.Timestamp;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BankMaster extends ObjectSync {
  /**
   * 6 fields + sfid
   */
  private String name;
  private String banknamekana;
  private String bankcode;
  private String bankname;
  private String branchname;
  private String branchcode;
  private String branchnamekana;
  private Timestamp lastmodifieddate;
}
