package jp.co.japantaxi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Worker extends ObjectSync {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  /**
   * 4 fields + sfid + syncedtime
   */
  private String tablename;
  private Boolean sycapproveflg;
  private String syncstatus;
  private Boolean deleteflg;
}
