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
public class PaymentSystemLinkInfor extends ObjectSync {
  /**
   * 14 fields + sfid
   */
  private String internetpaymerstore;
  private String linepaychannelid;
  private String linepaychannelsecret;
  private String aupaystoreid;
  private String serviceid;

  private String nontofacemerchantid;
  private String tofacemerchantid;
  private String nontofacehashkey;
  private String tofacehashkey;
  private String nontofaceencryptkey;
  private String tofaceencryptkey;
  private String nontofaceencryptiv;
  private String tofaceencryptiv;
  private String nontofacebasicauthid;
  private String tofacebasicauthid;
  private String nontofacebasicauthpassword;
  private String tofacebasicauthpassword;
  private String nontofacecardtype;
  private String tofacecardtype;
  private Integer nontofacepaymentrouteid;
  private Integer tofacepaymentrouteid;

  private Timestamp lastmodifieddate;
}
