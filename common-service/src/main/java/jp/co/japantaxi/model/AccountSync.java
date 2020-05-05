package jp.co.japantaxi.model;

import java.util.List;

import jp.co.japantaxi.response.MerchantResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountSync extends Account{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<MerchantResponse.BankAccountFielsBKMapper> bankaccountinfo;
}
