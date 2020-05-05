package jp.co.japantaxi.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParameterRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4530664545495329192L;
	private String ids;
	private String startTime;
	private String startMode;
	private String processMode;
	private String id;
	private Integer jtxcompanyid;
	private String mertermno;
}
