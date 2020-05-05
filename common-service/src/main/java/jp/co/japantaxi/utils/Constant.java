package jp.co.japantaxi.utils;

public class Constant {

	public static final String ACCOUNT = "Account";
	public static final String ACCOUNTSYNC = "AccountSync";
	
	public static final String APPCOMPANY = "AppCompany";
	public static final String APPCOMPANYSYNC = "AppCompanySync";
	
	public static final String BANKMASTER = "BankMaster";
	public static final String BANKMASTERSYNC = "BankMasterSync";
	
	public static final String BANKACCOUNTINFORMATION = "BankAccountInformation";
	public static final String BANKACCOUNTINFORMATIONSYNC = "BankAccountInformationSync";
	
	public static final String FARETABLE = "FareTable";
	public static final String FARETABLESYNC = "FareTableSync";
	
	public static final String PAYMENTSYSTEMLINKINFOR = "PaymentSystemLinkInfor";
	public static final String PAYMENTSYSTEMLINKINFORSYNC = "PaymentSystemLinkInforSync";
	
	public static final String FROM_DATETIME = "fromdatetime";
	public static final String TO_DATETIME = "todatetime";
	
	public static final String START_MODE = "startmode";
	
	public static final String TIME_INIT = "1900-01-01 00:00:00";
	// Error message
	public static final String API_GET = "API-GET error request API: ";
	public static final String SF_REG = "SF-REG error sync at table: ";
	public static final String BACK_PRO = "BACK_PRO error convert at table: ";
	public static final String BACK_REG = "BACK_REG error sync at table: ";
	
	public static final int LIMIT = 1000;
	
	public enum PROCESSMODE {
		GETSF("GETSF"), COREDATECREAT("COREDATECREAT"), CLEAR("CLEAR"), NULL("");
	
		public String value;
	
		PROCESSMODE(String value) {
			this.value = value;
		}
	}
	
	public static PROCESSMODE checkProcessMode(String value) {
		for (PROCESSMODE processMode : PROCESSMODE.values()) {
			if (processMode.value.equalsIgnoreCase(value.toUpperCase())) {
				return processMode;
			}
		}
		return null;
	}
	
	public enum STARTMODE {
		OFF("OFF"), CRON("CRON"), MANUAL("MANUAL");
	
		public String value;
	
		STARTMODE(String value) {
			this.value = value;
		}
	}
	
	public static STARTMODE checkStartMode(String value) {
		for (STARTMODE startMode : STARTMODE.values()) {
			if (startMode.value.equalsIgnoreCase(value.toUpperCase())) {
				return startMode;
			}
		}
		return null;
	}
	
	public enum NORMALCODE {
		N00("N00"), N01("N01"), N02("N02"), E00("E00"), E01("E01"), E02("E02"), E03("E03"), E04("E04"), E05("E05");
	
		public String value;
	
		NORMALCODE(String value) {
			this.value = value;
		}
	}
	
	public enum ERRORCODE {
		BAD_REQUEST(400), UNAUTHORIZED(401), NOT_FOUND(404), UNSUPPORTED_MEDIA_TYPE(415), INTERNAL_SERVER_ERROR(500);
	
		public Integer value;
	
		ERRORCODE(Integer value) {
			this.value = value;
		}
	}
	
	public static ERRORCODE checkError(Integer code) {
		for (ERRORCODE error : ERRORCODE.values()) {
			if (error.value.equals(code)) {
				return error;
			}
		}
		return null;
	}
}
