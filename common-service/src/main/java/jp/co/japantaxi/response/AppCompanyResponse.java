package jp.co.japantaxi.response;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppCompanyResponse {
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String sfid;
	private Integer jtxCompanyId;
	private String appDisplayName;
	private Integer acceptMajor2;
	private Integer acceptMinor2;
	private String dispatchType;
	private String phoneNumber;
	private String phoneNumberCancel;
	private String email;
	private String warningEmail;
	private String companyLogoImage;
	private String areaCode;
	private String salesPrefecture;
	private String salesCity;
	private String profile;
	private Integer cars;
	private Boolean isEnableClosing;
	private String displaySalesCity;
	private Boolean showDest;
	private Boolean showMoving;
	private Boolean showMustArrivalTime;
	private String showVisit;
	private Boolean showBookingCancel;
	private String extendedFieldMap;
	private String closingTimeFrom;
	private String closingTimeTo;
	private Double globalLat;
	private Double globalLng;
	private String companyNameEn;
	private String displaySalesCityEn;
	private String pickupFeeDescriptionEn;
	private String bookingFeeDescriptionEn;
	private Boolean showEmptyCar;
	private Float ratingAverage;
	private Integer waitForPickUpDefault;
	private Boolean showMinutesToWaitForPickUp;
	private Boolean isRadioNumberEdit;
	private Boolean isExternalCancel;
	private Boolean showImmediateCancel;
	private String netPayTypes;
	private String paymentMethod;
	private String minutesToWaitForPickUp;
	private String carImageColor;
	private String carImageName;
//	private String logoImage;
	private Integer replyWatingMinute;
	private Integer groupCompanies;
	private String communicationUrl;
	private String orderUrl;
	private String orderCancelUrl;
	private Integer payableCommission;
	// Map select multi table.
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, Object> faretables;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, Object> account;
	
	// update 2020/04/15
	private String driverContract;
	private Integer implementationCosts;
	private Integer monthlyMinimumCharge;
	private Integer monthlyMinimumCars;
	private String remarks;
	private String requestStartDate;
	private Integer requestCompanyIDRelation;
	private String website;
	private HashMap<String, Object> bankAccount;
	private HashMap<String, Object> bankAccount2;
	private String paymentMethodEN;
	private String salesPrefectureEN;
	private String destinationPrefecture;
	private String destinationAddress;
	private String destinationPostalCode;
	private String destinationContactName;
	private String destinationCompanyName;
	
	@Getter
	@Setter
	public static class AppCompanyCoopApproval extends AppCompanyResponse {
		
		private String dispatchType;
		// field update  dbo.requestCompanies
		private boolean dispatchtypeisexternaldispatch;
		private Integer dispatchtypepartnerapiversion;
		private String paymentMethodEN;
		private String salesPrefectureEN;
		private int drivercontractdispatchmethodid;
		private int drivercontractisenabled;
		private int drivercontractispreviewenabled;
		private int drivercontractuseasdefault;
		private int drivercontractonlyusewhennecessary;
		private Integer pickupFee;
		private Integer uponEntryMeter;
		private Integer firstsquarefare;
		private Integer uponEntryFare;
		private Integer UnitMeter;
		private Integer unitFare;
		private Integer bookingFee;
		private Integer pickupFeeExistence;
		private Integer BookingFeeExistence;
		private String PickupFeeDescription;
		private String bookingFeeDescription;
		private BankAccountFiels bankAccountbk;
		private BankAccountFiels bankAccountbk2;
		private String accountMerTermNo;
	}
	
	@Getter
	@Setter
	public static class BankAccountFiels
	{
		private String sfid;
		private String merTermNo;
		private String bankNameKana;
		private String bankCode;
		private String branchName;
		private String branchCode;
		private String branchNameKana;
		private String accountCategory;
		private String accountNumber;
		private String accountName;
		private String accountNameKana;
		private String accountType;
		private String invalidDate;
		private String invalidReason;
	}
	@Getter
	@Setter
	public static class AppCompanyDetailResponse {
		private Integer statusCode;
		private String message;
		private ResultDetail result;
	}
	
	@Getter
	@Setter
	public static class ResultDetail {
		private HashMap<String, Object> appCompany;
		private Map<String, Object> fareTable;
		private HashMap<String, Object> bankAccount;
		private HashMap<String, Object> bankAccount2;
	}
	
	@Getter
	@Setter
	public static class AppCompanyList {
		private Boolean isNeedApproval;
		private Integer jtxCompanyId;
		private String dispatchType;
		private String phoneNumber;
		private String salesPrefecture;
		private String appDisplayName;
		private String sfid;
	}
}
