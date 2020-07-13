package jp.co.japantaxi.model;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
@Setter
public class ObjectSync implements Serializable {
  /**
   * some fields usually in common case
   */
  private String sfid;
  private String account;
  private Timestamp syncedtime;
}
