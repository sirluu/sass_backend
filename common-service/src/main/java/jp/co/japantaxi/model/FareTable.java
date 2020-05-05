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
public class FareTable extends ObjectSync {
  /**
   * 13 fields + sfid
   */
  private String appcompany;
  private Integer pickupfare;
  private Integer firstsquaredistance;
  private Integer firstsquarefare;
  private Integer additionaldistance;
  private Integer additionalfare;
  private Integer latenightearlymorningextra;
  private Integer reservationfee;
  private Boolean pickupfeeexistence; // 0,1
  private String pickupfeedescription;
  private Boolean bookingfeeexistence; // 0,1
  private String bookingfeedescription;
  private Timestamp lastmodifieddate;
}
