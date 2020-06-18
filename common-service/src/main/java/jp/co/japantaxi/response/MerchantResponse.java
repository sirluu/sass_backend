package jp.co.japantaxi.response;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchantResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter
	@Setter
	public static class MerchantObjectList implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 246112083084645508L;
		private String sfid;
		private Integer appCompanyId;
		private String merTermNo;
		private String affiliationStoreNameKanji;
		private String affiliationStoreNameKana;
		private String merchantInfoRadeName;
		private String merchantInfoContactName;
	}
	
	@Getter
	@Setter
	public static class MerchantDetail implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 246112083084645508L;
		
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String sfid;
		private String affiliationStoreNameAlphabetic;
		private String affiliationStoreNameKana;
		private String affiliationStoreNameKanji;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private String website;
		private String keiriNo;
		private String corporateNumber;
		private String merTermNo;
		private String irisId;
		private Integer merchantInfoIsRepresentative;
		private String merchantInfoInquiryName;
		private String merchantInfoInquiryPhone;
		private String merchantInfoWarningEmail;
		private String merchantInfoInquiryEmail;
		private String merchantInfoErrorInquiryName;
		private String merchantInfoErrorInquiryPhone;
		private String merchantInfoErrorInquiryEmail;
		private String merchantInfoErrorAccountBank;
		private String merchantInfoErrorAccountBranch;
		private String merchantInfoErrorAccountType;
		private String merchantInfoErrorAccountNumber;
		private String merchantInfoErrorAccountName;
		private String merchantInfoErrorAccountKana;
		private String merchantInfoServiceId;
		private String merchantInfoPaymentContactName;
		private String merchantInfoRemarks;
		private String merchantInforadeName;
		private String merchantInfoPostalCode;
		private String merchantInfoPrefectureCode;
		private String merchantInfoAddress;
		private String merchantInfoAddressKana;
		private String merchantInfoPhoneNumberHyphen;
		private String merchantInfoContactName;
		private String merchantInfoContactEmail;
		private String merchantInfoMeterList;
		private Integer merchantContrInforApplicationCars;
		private String merchantContrInforTabletModel;
		private String merchantContrInforApplicationDate;
		private Boolean merchantContrInforIsFacetofaceSettlement;
		private Boolean merchantContrInforIsRegistry;
		private String merchantContrInforLicenseName;
		private String merchantContrInforLicenseNumber;
		private String merchantContrInforLicenseAcquisitionDate;
		private String merchantReprInforFullNameKanji;
		private String merchantReprInforFullNameKana;
		private String merchantReprInforPostalCode;
		private String merchantReprInforPrefectureCode;
		private String merchantReprInforAddress;
		private String merchantReprInforPhone;
		private String merchantReprInforBirthday;
		private String merchantReprInforSex;
		private String merchantReprInforMemo;
		private String merchantStoreInforStoreNameKanji;
		private String merchantStoreInforStoreNameHalfwidthKana;
		private String merchantStoreInforStoreNameAlphabet;
		private String merchantStoreInforPostalCode;
		private String merchantStoreInforPrefectureCode;
		private String merchantStoreInforAddress;
		private String merchantStoreInforAddressKana;
		private String merchantStoreInforPhone;
		private String merchantStoreInforMemo;
		private String merchantDeliveryInforDate;
		private String merchantDeliveryInforGakkenCustomerNumber;
		private String merchantDeliveryInforName;
		private String merchantDeliveryInforPostalCode;
		private String merchantDeliveryInforPrefectureCode;
		private String merchantDeliveryInforAddress;
		private String merchantDeliveryInforContactName;
		private String merchantDeliveryInforPhone;
		private String merchantDeliveryInforMemo;
		private String merchantPayInfoName;
		private String merchantPayInfoPostalCode;
		private String merchantPayInfoAddress1;
		private String merchantPayInfoAddress2;
		private String merchantPayInfoContactName;
		private String merchantOperInforStartDate;
		private String merchantOperInforAcceptanceDate;
		private String merchantOperInforGuaranteeEndDate;
		private String merchantOperInforContractEndDate;
		private String merchantOperInforMemo;
		public int appCompanyId;
		public String jppOrganizationId;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private Map<String, Object> netBankAccount;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private Map<String, Object> itabBankAccount;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private PaymentSystemLinkInforDetail paymentSystemLinkInfor;
	}
	
	@Getter
	@Setter
	public static class PaymentSystemLinkInforDetail {
		private String linePayChannelId;
		private String linePayChannelSecret;
		private String auPayStoreId;
		private String serviceId;
		private String toFaceMerchantId;
		private String nonToFaceMerchantId;
		private String toFaceHashKey;
		private String nonToFaceHashKey;
		private String toFaceEncryptKey;
		private String nonToFaceEncryptKey;
		private String toFaceEncryptIv;
		private String nonToFaceEncryptIv;
		private String toFaceBasicAuthId;
		private String nonToFaceBasicAuthId;
		private String toFaceBasicAuthPassword;
		private String nonToFaceBasicAuthPassword;
		private String toFaceCardType;
		private String nonToFaceCardType;
		private Integer toFacePaymentRouteId;
		private Integer nonToFacePaymentRouteId;
		private String sfid;
	}
	@Getter
	@Setter
	public static class MerchantCoopApproval{
		
		private String affiliationStoreNameAlphabetic;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private Integer appcompanyid; // field map data BK
		private String appCompany;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private Integer jtxcompanyid;
		private String affiliationStoreNameKana;
		private String netpayMerchantsAffiliationStoreNameKanji;
		private String applicationFormAffiliationStoreNameKanji;
		private String companiesAffiliationStoreNameKanji;
		private String keiriNo;
		private String corporateNumber;
		private String netpayMerchantsMerTermNo;
		private String applicationFormMerTermNo;
		private String irisId;
		private Integer merchantInfoIsRepresentative;
		private String merchantInfoInquiryName;
		private String merchantInfoInquiryPhone;
		private String merchantInfoWarningEmail;
		private String merchantInfoInquiryEmail;
		private String merchantInfoErrorInquiryName;
		private String merchantInfoErrorInquiryPhone;
		private String merchantInfoErrorInquiryEmail;
		private String merchantInfoErrorAccountBank;
		private String merchantInfoErrorAccountBranch;
		private String merchantInfoErrorAccountType;
		private String merchantInfoErrorAccountNumber;
		private String merchantInfoErrorAccountName;
		private String merchantInfoErrorAccountKana;
		private String merchantInfoServiceId;
		private String merchantInfoPaymentContactName;
		private String merchantInfoRemarks;
		private String merchantInforadeName;
		private String merchantInfoPostalCode;
		private String merchantInfoPrefectureCode;
		private String merchantInfoAddress;
		private String merchantInfoAddressKana;
		private String applicationFormMerchantInfoPhoneNumberHyphen;
		private String companiesMerchantInfoPhoneNumberHyphen;
		private String merchantInfoContactName;
		private String applicationFormMerchantInfoContactEmail;
		private String companiesMerchantInfoContactEmail;
		private String merchantInfoMeterList;
		private Integer merchantContrInforApplicationCars;
		private String merchantContrInforTabletModel;
		private String merchantContrInforApplicationDate = "";
		private Integer merchantContrInforIsFacetofaceSettlement;
		private Integer merchantContrInforIsRegistry;
		private String merchantContrInforLicenseName;
		private String merchantContrInforLicenseNumber;
		private String merchantContrInforLicenseAcquisitionDate = "";
		private String merchantReprInforFullNameKanji;
		private String merchantReprInforFullNameKana;
		private String merchantReprInforPostalCode;
		private String merchantReprInforPrefectureCode;
		private String merchantReprInforAddress;
		private String merchantReprInforPhone;
		private String merchantReprInforBirthday;
		private String merchantReprInforSex;
		private String merchantReprInforMemo;
		private String merchantStoreInforStoreNameKanji;
		private String merchantStoreInforStoreNameHalfwidthKana;
		private String merchantStoreInforStoreNameAlphabet;
		private String merchantStoreInforPostalCode;
		private String merchantStoreInforPrefectureCode;
		private String merchantStoreInforAddress;
		private String merchantStoreInforAddressKana;
		private String merchantStoreInforPhone;
		private String merchantStoreInforMemo;
		private String merchantDeliveryInforDate; // string ‎yyyy/MM/dd hh:mm:ss
		private String merchantDeliveryInforGakkenCustomerNumber;
		private String merchantDeliveryInforName;
		private String merchantDeliveryInforPostalCode;
		private String merchantDeliveryInforPrefectureCode;
		private String merchantDeliveryInforAddress;
		private String merchantDeliveryInforContactName;
		private String merchantDeliveryInforPhone;
		private String merchantDeliveryInforMemo;
		private String netpayMerchantsMerchantPayInfoName;
		private String applicationFormMerchantPayInfoName;
		private String companiesMerchantPayInfoName;
		private String netpayMerchantsMerchantPayInfoPostalCode;
		private String applicationFormMerchantPayInfoPostalCode;
		private String companiesMerchantPayInfoPostalCode;
		private String netpayMerchantsMerchantPayInfoAddress1;
		private String companiesMerchantPayInfoAddress1;
		private String netpayMerchantsMerchantPayInfoAddress2;
		private String applicationFormMerchantPayInfoAddress2;
		private String merchantPayInfoContactName;
		private String merchantOperInforStartDate; // string ‎yyyy/MM/dd
		private String merchantOperInforAcceptanceDate; // string ‎yyyy/MM/dd
		private String merchantOperInforGuaranteeEndDate; // string ‎yyyy/MM/dd
		private String merchantOperInforContractEndDate; // string ‎yyyy/MM/dd
		private String merchantOperInforMemo;
		private String sfid;

		private NetBankAccount netBankAccount;
		private ItabBankAccount itabBankAccount;
		private PaymentSystemLinkInforSync paymentSystemLinkInfor;
	}
	
	@Getter
	@Setter
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class ItabBankAccount {
		public String applicationFormPaymentBankNameKana;
		public String companiesBankNameKana;
		public String applicationFormPaymentBankCode;
		public String companiesBankCode;
		public String applicationFormPaymentBranchCode;
		public String companiesBranchCode;
		public String applicationFormPaymentBranchNameKana;
		public String companiesBranchNameKana;
		public String applicationFormPaymentAccountNumber;
		public String companiesAccountNumber;
		public String applicationFormPaymentAccountNameKana;
		public String companiesAccountNameKana;
		public String applicationFormPaymentAccountType;
		public String companiesAccountType;
	}
	
	
	@Getter
	@Setter
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class NetBankAccount {
		public String netpayMerchantsBankNameKana;
		public String netpayMerchantsBankCode;
		public String netpayMerchantsBranchCode;
		public String netpayMerchantsBranchNameKana;
		public String netpayMerchantsAccountNumber;
		public String netpayMerchantsAccountNameKana;
		public String netpayMerchantsAccountType;
	}
	
	@Getter
	@Setter
	public static class BankInFo
	{
		public Integer companyid;
		public String state;
		public String address;
		public String postalcode;
		public String contactname;
	}
	
	@Getter
	@Setter
	public static class BankAccountFielsBKMapper
	{
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
	public static class MerchantCoopApprovalInfoListResponse {
		private Object paymentSystemLinkInfor;
		private Object merchant;
		
		private Object netBankAccount;
		private Object itabBankAccount;
	}
	
	@Getter
	@Setter
	public static class PaymentSystemLinkInforSync {
		private String linePayChannelId;
		private String linePayChannelSecret;
		private String auPayStoreId;
		private String serviceId;
		private String toFaceMerchantId;
		private String nonToFaceMerchantId;
		private String toFaceHashKey;
		private String nonToFaceHashKey;
		private String toFaceEncryptKey;
		private String nonToFaceEncryptKey;
		private String toFaceEncryptIv;
		private String nonToFaceEncryptIv;
		private String toFaceBasicAuthId;
		private String nonToFaceBasicAuthId;
		private String toFaceBasicAuthPassword;
		private String nonToFaceBasicAuthPassword;
		private String toFaceCardType;
		private String nonToFaceCardType;
		private Integer toFacePaymentRouteId;
		private Integer nonToFacePaymentRouteId;
	}
}
