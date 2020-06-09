package jp.co.japantaxi.response;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppCompanyCoopApprovalBrooklyn {

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
	private Integer isEnableClosing;
	private String displaySalesCity;
	private Integer showDest;
	private Integer showMoving;
	private Integer showMustArrivalTime;
	private String showVisit;
	private Integer showBookingCancel;
	private String extendedFieldMap;
	private String closingTimeFrom;
	private String closingTimeTo;
	private String globalLat;
	private String globalLng;
	private String companyNameEn;
	private String displaySalesCityEn;
	private String pickupFeeDescriptionEn;
	private String bookingFeeDescriptionEn;
	private Integer showEmptyCar;
	private Float ratingAverage;
	private Integer waitForPickUpDefault;
	private Integer showMinutesToWaitForPickUp;
	private Integer isRadioNumberEdit;
	private Integer isExternalCancel;
	private Integer showImmediateCancel;
	private String netPayTypes;
	private String paymentMethod;
	private String minutesToWaitForPickUp;

//	private String carImageColor;
//	private String carImageName;

	private String carImage;
	
	private Integer replyWatingMinute;
	private Integer groupCompanies;
	private String communicationUrl;
	private String orderUrl;
	private String orderCancelUrl;
	private Integer payableCommission;
	// Map select multi table.
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private FareTableCoopApproval faretables;
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
	// update 05/04/2020
	private String paymentMethodEN;
	private String salesPrefectureEN;
	private String destinationAddress;
	private String destinationPostalCode;
	private String destinationContactName;
	private String destinationCompanyName;
	
	@Getter
	@Setter
	public static class FareTableCoopApproval {
		private Integer pickupFare;
		private Integer firstSquareDistance;
		private Integer firstSquareFare;
		private Integer additionalDistance;
		private Integer additionalFare;
		private Integer reservationFee;
		private Integer pickupFeeExistence;
		private String pickupFeeDescription;
		private Integer bookingFeeExistence;
		private String bookingFeeDescription;
	}
}
