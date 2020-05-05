package jp.co.japantaxi.model;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatchStatus {
	/**
	 * 8 fields
	 */
	private String batchcode;
	private String batchname;
	private String startupmode;
	private Timestamp processstartdate;
	private Timestamp processenddate;
	private String status;
	private String statusinfo;
	private Integer sfapicallcount;
	private Timestamp fromdatetime;
}
