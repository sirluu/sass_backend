package jp.co.japantaxi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountSync extends Account{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String channelId;
	private String channelSecret;
	private String auPayStoreId;
	private String serviceId;
	private String toFaceMerchantId;
	private String nonToFaceMerchantId;
	private String toFaceHashKey;
	private String nonToFaceHashKey;
	private String toFaceEncryptKey;
	private String nonToFaceEncryptKey;
	private String toFaceEncryptIV;
	private String nonToFaceEncryptIV;
	private String toFaceBasicAuthId;
	private String nonToFaceBasicAuthId;
	private String toFaceBasicAuthPassword;
	private String nonToFaceBasicAuthPassword;
	private String toFaceCardType;
	private String nonToFaceCardType;
	private Integer toFacePaymentRouteId;
	private Integer nonToFacePaymentRouteId;
	private String companyPaymentaddress;

	/* tsp.application_form_payment */
	private String paymentBankCode;
	private String paymentBankNameKana;
	private String paymentBranchCode;
	private String paymentBankName;
	private String paymentBranchName;
	private String paymentDepositType;
	private String paymentAccountNo;
	private String paymentDepositor;
	private String paymentBranchNameKana;
	private String paymentAccountNameKana;
	private String paymentAccountType;
	
	private String netpayAccountNameKana;
	private String netpayAccountType;
	private String netpayBankNameKana;
	private String netpayBankCode;
	private String netpayBranchCode;
	private String netpayBranchNameKana;
	private String netpayPaymentAccountNo;
}
