package jp.co.japantaxi.model;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class BankMaster extends ObjectSync {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
