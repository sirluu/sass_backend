package jp.co.japantaxi.response;

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
	private boolean isEnableClosing;
	private String displaySalesCity;
	private boolean showDest;
	private boolean showMoving;
	private boolean showMustArrivalTime;
	private String showVisit;
	private boolean showBookingCancel;
	private String extendedFieldMap;
	private String closingTimeFrom;
	private String closingTimeTo;
	private String globalLat;
	private String globalLng;
	private String companyNameEn;
	private String displaySalesCityEn;
	private String pickupFeeDescriptionEn;
	private String bookingFeeDescriptionEn;
//	private Integer partnerApiVersion;
	private boolean showEmptyCar;
	private Float ratingAverage;
	private Integer waitForPickUpDefault;
	private boolean showMinutesToWaitForPickUp;
	private boolean isRadioNumberEdit;
	private boolean isExternalCancel;
	private boolean showImmediateCancel;
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
	private Map<?, ?> faretables;
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
	private Map<String, Object> bankAccount;
	private Map<String, Object> bankAccount2;
	private Integer isExternalDispatchDispatchType;
	private Integer partnerApiVersionDispatchType;
	private Integer dispatchMethodIdDriverContract;
	private Integer isPreviewEnabledDriverContract;
	private Integer isEnabledDriverContract;
	private Integer useAsDefaultDriverContract;
	private Integer onlyUseWhenNecessaryDriverContract;
	
	@Getter
	@Setter
	public static class AppCompanyCoopApproval extends AppCompanyResponse {
		// field update  dbo.requestCompanies
		private boolean dispatchtypeisexternaldispatch;
		private int dispatchtypepartnerapiversion;
		private Map<String, Object> requestcompanies;
		
		// companySpecificDispatchMethods
		private int drivercontractdispatchmethodid;
		private int drivercontractisenabled;
		private int drivercontractispreviewenabled;
		private int drivercontractuseasdefault;
		private int drivercontractonlyusewhennecessary;
	}
}
