package jp.co.japantaxi.mapper.stockholm;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import jp.co.japantaxi.model.Account;
import jp.co.japantaxi.model.AccountSync;
import jp.co.japantaxi.model.ParameterRequest;
import jp.co.japantaxi.response.MerchantResponse;
import jp.co.japantaxi.response.MerchantResponse.MerchantDetail;

public interface AccountMapper {

	Account getAccountById(@Param("sfid") String sfid);

	List<String> getListAccountIdFromStockholm(ParameterRequest ids);

    List<String> getListAccountIds(ParameterRequest request);
  
    List<Account> getListAccount(ParameterRequest request);
  
    List<Account> getListAccountSync(ParameterRequest request);
  
    Integer countAccount(ParameterRequest request);

	void insertAccount(Account account);

	void updateAccount(Account account);

	void insertAccountSync(Account account);

	void updateAccountSync(Account account);

	void truncateAccount();

	// Singapore
	List<AccountSync> getAccountByAppCompany(@Param("appCompany") String appCompany);

	MerchantDetail getMerchantInfoBySFID(@Param("sfid") String sfid);

	List<MerchantResponse.MerchantObjectList> getMerchantInfoList(@Param("appCompanyId") String appCompanyId);
	
	@Update("UPDATE accountsync SET mertermno=#{appCompanyId}, keiri_no=#{appCompanyId} WHERE sfid=#{sfid}")
	void testUpdateAppCompanyId(@Param("appCompanyId") Integer appCompanyId, @Param("sfid") String sfid);
	// End Singapore
}