package jp.co.japantaxi.controller;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import jp.co.japantaxi.config.CacheManagerConfig;
import jp.co.japantaxi.mapper.stockholm.AccountMapper;
import jp.co.japantaxi.mapper.stockholm.AppCompanyMapper;
import jp.co.japantaxi.mapper.stockholm.FareTableMapper;
import jp.co.japantaxi.model.Account;
import jp.co.japantaxi.model.BatchStatus;
import jp.co.japantaxi.model.FareTable;
import jp.co.japantaxi.model.ParameterRequest;

@RunWith(SpringRunner.class)
@WebMvcTest(value = AccountController.class)
public class AccountControllerTest {

	@Autowired
	private AccountController accountController;

	@Autowired
	public SalesforceResponseController salesforceResponseController;

	@MockBean
	private AccountMapper accountMapper;

	@MockBean
	private AppCompanyMapper appCompanyMapper;

	@MockBean
	private FareTableMapper fareTableMapper;

	@MockBean
	private CacheManagerConfig cacheManagerConfig;

	@Test
	public void test_insertAccount() {
		Account expected = new Account();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		List<Account> list = new ArrayList<Account>();
		list.add(expected);
		accountMapper.insertAccount(expected);
		accountController.insertAccount(list);
	}

	@Test
	public void test_updateAccount() {
		Account expected = new Account();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		List<Account> list = new ArrayList<Account>();
		list.add(expected);
		accountMapper.updateAccount(expected);
		accountController.updateAccount(list);
	}

	@Test
	public void test_insertAccountSync() {
		Account expected = new Account();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		List<Account> list = new ArrayList<Account>();
		list.add(expected);
		accountMapper.insertAccountSync(expected);
		accountController.insertAccountSync(list);
	}

	@Test
	public void test_updateAccountSync() {
		Account expected = new Account();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		List<Account> list = new ArrayList<Account>();
		list.add(expected);
		accountMapper.updateAccountSync(expected);
		accountController.updateAccountSync(list);
	}

	@Test
	public void test_getListAccountIdFromStockholm() {
		List<String> list = new ArrayList<>();
		list.add("1");
		list.add("2");
		accountController.getListAccountIdFromStockholm(list);
	}
	
	@Test
	public void test_truncateAccount() {
		accountMapper.truncateAccount();
	}

	@Test
	public void test_getListAccountToInsert() {
		List<Account> list = new ArrayList<>();

		List<String> salesForceIds = new ArrayList<>();
		salesForceIds.add("1");
		salesForceIds.add("2");
		salesForceIds.add("3");

		List<String> stockholmIds = new ArrayList<>();
		stockholmIds.add("2");
		stockholmIds.add("3");

		List<Account> sfAccountList = new ArrayList<>();
		Account account = new Account();
		account.setAccount("teststring");
		account.setSfid("1");
		sfAccountList.add(account);
		list.add(account);
		account = new Account();
		account.setAccount("teststring");
		account.setSfid("3");
		sfAccountList.add(account);

		Assert.assertEquals(list, accountController.getListAccountToInsert(salesForceIds, stockholmIds, sfAccountList));
		stockholmIds = new ArrayList<>();
		Assert.assertEquals(2,
				accountController.getListAccountToInsert(salesForceIds, stockholmIds, sfAccountList).size());
	}

	@Test
	public void test_getListAccountToUpdate() {
		List<Account> list = new ArrayList<>();

		List<String> salesForceIds = new ArrayList<>();
		salesForceIds.add("1");
		salesForceIds.add("2");
		salesForceIds.add("3");

		List<String> stockholmIds = new ArrayList<>();
		stockholmIds.add("2");
		stockholmIds.add("3");

		List<Account> sfAccountList = new ArrayList<>();
		Account account = new Account();
		account.setAccount("teststring");
		account.setSfid("1");
		sfAccountList.add(account);
		account.setAccount("teststring");
		account.setSfid("2");
		list.add(account);
		account = new Account();
		account.setAccount("teststring");
		account.setSfid("3");
		sfAccountList.add(account);
		list.add(account);

		Assert.assertEquals(list, accountController.getListAccountToUpdate(salesForceIds, stockholmIds, sfAccountList));
	}

	@Test
	public void test_insertFareTable() {
		FareTable expected = new FareTable();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		List<FareTable> list = new ArrayList<FareTable>();
		list.add(expected);
		fareTableMapper.insertFareTable(expected);
		accountController.insertFareTable(list);
	}

	@Test
	public void test_updateFareTable() {
		FareTable expected = new FareTable();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		List<FareTable> list = new ArrayList<FareTable>();
		list.add(expected);
		fareTableMapper.updateFareTable(expected);
		accountController.updateFareTable(list);
	}

	@Test
	public void test_insertFareTableSync() {
		FareTable expected = new FareTable();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		List<FareTable> list = new ArrayList<FareTable>();
		list.add(expected);
		fareTableMapper.insertFareTableSync(expected);
		accountController.insertFareTableSync(list);
	}

	@Test
	public void test_updateFareTableSync() {
		FareTable expected = new FareTable();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		List<FareTable> list = new ArrayList<FareTable>();
		list.add(expected);
		fareTableMapper.updateFareTableSync(expected);
		accountController.updateFareTableSync(list);
	}

	@Test
	public void test_getListFareTableIdFromStockholm() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
		accountController.getListFareTableIdFromStockholm(list);
	}

	@Test
	public void test_truncateFareTable() {
		fareTableMapper.truncateFareTable();
	}

	@Test
	public void test_getListFareTableToInsert() {
		List<FareTable> list = new ArrayList<>();

		List<String> salesForceIds = new ArrayList<>();
		salesForceIds.add("1");
		salesForceIds.add("2");
		salesForceIds.add("3");

		List<String> stockholmIds = new ArrayList<>();
		stockholmIds.add("2");
		stockholmIds.add("3");

		List<FareTable> sfFareTableList = new ArrayList<>();
		FareTable fareTable = new FareTable();
		fareTable.setAccount("teststring");
		fareTable.setSfid("1");
		sfFareTableList.add(fareTable);
		list.add(fareTable);
		fareTable = new FareTable();
		fareTable.setAccount("teststring");
		fareTable.setSfid("3");
		sfFareTableList.add(fareTable);

		Assert.assertEquals(list,
				accountController.getListFareTableToInsert(salesForceIds, stockholmIds, sfFareTableList));
		stockholmIds = new ArrayList<>();
		Assert.assertEquals(2,
				accountController.getListFareTableToInsert(salesForceIds, stockholmIds, sfFareTableList).size());
	}

	@Test
	public void test_getListFareTableToUpdate() {
		List<FareTable> list = new ArrayList<>();

		List<String> salesForceIds = new ArrayList<>();
		salesForceIds.add("1");
		salesForceIds.add("2");
		salesForceIds.add("3");

		List<String> stockholmIds = new ArrayList<>();
		stockholmIds.add("2");
		stockholmIds.add("3");

		List<FareTable> sfFareTableList = new ArrayList<>();
		FareTable fareTable = new FareTable();
		fareTable.setAccount("teststring");
		fareTable.setSfid("1");
		sfFareTableList.add(fareTable);
		fareTable.setAccount("teststring");
		fareTable.setSfid("2");
		list.add(fareTable);
		fareTable = new FareTable();
		fareTable.setAccount("teststring");
		fareTable.setSfid("3");
		sfFareTableList.add(fareTable);
		list.add(fareTable);

		Assert.assertEquals(list,
				accountController.getListFareTableToUpdate(salesForceIds, stockholmIds, sfFareTableList));
	}

	@Test
	public void test_coreDateCreatAccount() {
		ParameterRequest parameterRequest = new ParameterRequest();
		parameterRequest.setStartMode("test");
		parameterRequest.setStartTime("2020-01-0101:01:01");
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		accountController.coreDateCreatAccount(parameterRequest, batchStatus);
	}

	@Test
	public void test_coreDateCreatFareTable() {
		ParameterRequest parameterRequest = new ParameterRequest();
		parameterRequest.setStartMode("test");
		parameterRequest.setStartTime("2020-01-0101:01:01");
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		accountController.coreDateCreatFareTable(parameterRequest, batchStatus);
	}
}
