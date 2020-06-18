package jp.co.japantaxi.controller;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import jp.co.japantaxi.config.CacheManagerConfig;
import jp.co.japantaxi.mapper.stockholm.BankAccountInformationMapper;
import jp.co.japantaxi.mapper.stockholm.BankMasterMapper;
import jp.co.japantaxi.model.BankAccountInformation;
import jp.co.japantaxi.model.BankMaster;
import jp.co.japantaxi.model.BatchStatus;
import jp.co.japantaxi.model.ParameterRequest;
import jp.co.japantaxi.utils.DateTimeUtil;

@RunWith(SpringRunner.class)
@WebMvcTest(value = BankMasterController.class)
public class BankMasterControllerTest {

	@Autowired
	private BankMasterController masterController;

	@Autowired
	public SalesforceResponseController salesforceResponseController;

	@MockBean
	private BankMasterMapper bankMasterMapper;

	@MockBean
	private BankAccountInformationMapper bankAccountInformationMapper;

	@MockBean
	private CacheManagerConfig cacheManager;

	@Mock
	RestTemplate restTemplate;

	@Test
	public void test_insertBankMaster() {
		BankMaster expected = new BankMaster();
		expected.setSyncedtime(DateTimeUtil.getTimestampFromString("01:01:01", DateTimeUtil.DD_FM));
		expected.setAccount("teststring");
		expected.setSfid("a0000000000KgpbjIAB");
		List<BankMaster> list = new ArrayList<BankMaster>();
		list.add(expected);
		bankMasterMapper.insertBankMaster(expected);
		masterController.insertBankMaster(list);
	}

	@Test
	public void test_updateBankMaster() {
		BankMaster expected = new BankMaster();
		expected.setSyncedtime(DateTimeUtil.getTimestampFromString("01:01:01", DateTimeUtil.DD_FM));
		expected.setAccount("teststring");
		expected.setSfid("a04N000000KgpbjIAB");
		List<BankMaster> list = new ArrayList<BankMaster>();
		list.add(expected);
		bankMasterMapper.updateBankMaster(expected);
		masterController.updateBankMaster(list);
	}

	@Test
	public void test_insertBankMasterSync() {
		BankMaster expected = new BankMaster();
		expected.setSyncedtime(DateTimeUtil.getTimestampFromString("01:01:01", DateTimeUtil.DD_FM));
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		List<BankMaster> list = new ArrayList<BankMaster>();
		list.add(expected);
		bankMasterMapper.insertBankMasterSync(expected);
		masterController.insertBankMasterSync(list);
	}

	@Test
	public void test_updateBankMasterSync() {
		BankMaster expected = new BankMaster();
		expected.setSyncedtime(DateTimeUtil.getTimestampFromString("01:01:01", DateTimeUtil.DD_FM));
		expected.setAccount("teststring");
		expected.setSfid("a04N000000KgpbjIAB");
		List<BankMaster> list = new ArrayList<BankMaster>();
		list.add(expected);
		bankMasterMapper.updateBankMasterSync(expected);
		masterController.updateBankMasterSync(list);
	}

	@Test
	public void test_getListBankMasterIdFromStockholm() {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
		masterController.getListBankMasterIdFromStockholm(list);
	}

	@Test
	public void test_getListBankMasterToInsert() {
		List<BankMaster> list = new ArrayList<>();

		List<String> salesForceIds = new ArrayList<>();
		salesForceIds.add("1");
		salesForceIds.add("2");
		salesForceIds.add("3");

		List<String> stockholmIds = new ArrayList<>();
		stockholmIds.add("2");
		stockholmIds.add("3");

		List<BankMaster> sfBankMasterList = new ArrayList<>();
		BankMaster bankMaster = new BankMaster();
		bankMaster.setAccount("teststring");
		bankMaster.setSfid("1");
		sfBankMasterList.add(bankMaster);
		list.add(bankMaster);
		bankMaster = new BankMaster();
		bankMaster.setAccount("teststring");
		bankMaster.setSfid("3");
		sfBankMasterList.add(bankMaster);

//		Assert.assertEquals(list,
//				masterController.getListBankMasterToInsert(salesForceIds, stockholmIds, sfBankMasterList));
//		stockholmIds = new ArrayList<>();
//		Assert.assertEquals(2,
//				masterController.getListBankMasterToInsert(salesForceIds, stockholmIds, sfBankMasterList).size());
	}

	@Test
	public void test_getListBankMasterToUpdate() {
		List<BankMaster> list = new ArrayList<>();

		List<String> salesForceIds = new ArrayList<>();
		salesForceIds.add("1");
		salesForceIds.add("2");
		salesForceIds.add("3");

		List<String> stockholmIds = new ArrayList<>();
		stockholmIds.add("2");
		stockholmIds.add("3");

		List<BankMaster> sfBankMasterList = new ArrayList<>();
		BankMaster bankMaster = new BankMaster();
		bankMaster.setAccount("teststring");
		bankMaster.setSfid("1");
		sfBankMasterList.add(bankMaster);
		bankMaster.setAccount("teststring");
		bankMaster.setSfid("2");
		list.add(bankMaster);
		bankMaster = new BankMaster();
		bankMaster.setAccount("teststring");
		bankMaster.setSfid("3");
		sfBankMasterList.add(bankMaster);
		list.add(bankMaster);

//		Assert.assertEquals(list,
//				masterController.getListBankMasterToUpdate(salesForceIds, stockholmIds, sfBankMasterList));
	}

	@Test
	public void test_truncateBankMaster() {
		bankMasterMapper.truncateBankMaster();
	}

	@Test
	public void test_insertBankAccountInformation() {
		BankAccountInformation expected = new BankAccountInformation();
		expected.setAccount("teststring");
		expected.setSfid("a04N000000KgpbjIAB");
		List<BankAccountInformation> list = new ArrayList<BankAccountInformation>();
		list.add(expected);
		bankAccountInformationMapper.insertBankAccountInformation(expected);
		masterController.insertBankAccountInformation(list);
	}

	@Test
	public void test_updateBankAccountInformation() {
		BankAccountInformation expected = new BankAccountInformation();
		expected.setAccount("teststring");
		expected.setSfid("a04N000000KgpbjIAB");
		List<BankAccountInformation> list = new ArrayList<BankAccountInformation>();
		list.add(expected);
		bankAccountInformationMapper.updateBankAccountInformation(expected);
		masterController.updateBankAccountInformation(list);
	}

	@Test
	public void test_insertBankAccountInformationSync() {
		BankAccountInformation expected = new BankAccountInformation();
		expected.setAccount("teststring");
		expected.setSfid("a05N000000KgpbjIAB");
		List<BankAccountInformation> list = new ArrayList<BankAccountInformation>();
		list.add(expected);
		bankAccountInformationMapper.insertBankAccountInformationSync(expected);
		masterController.insertBankAccountInformationSync(list);
	}

	@Test
	public void test_updateBankAccountInformationSync() {
		BankAccountInformation expected = new BankAccountInformation();
		expected.setAccount("teststring");
		expected.setSfid("a04N000000KgpbjIAB");
		List<BankAccountInformation> list = new ArrayList<BankAccountInformation>();
		list.add(expected);
		bankAccountInformationMapper.updateBankAccountInformationSync(expected);
		masterController.updateBankAccountInformationSync(list);
	}

	@Test
	public void test_getListBankAccountInformationIdFromStockholm() {
      List<String> list = new ArrayList<>();
      list.add("1");
      list.add("2");
      masterController.getListBankAccountInformationIdFromStockholm(list);
    }

	@Test
	public void test_getListBankAccountInformationToInsert() {
		List<BankAccountInformation> list = new ArrayList<>();

		List<String> salesForceIds = new ArrayList<>();
		salesForceIds.add("1");
		salesForceIds.add("2");
		salesForceIds.add("3");

		List<String> stockholmIds = new ArrayList<>();
		stockholmIds.add("2");
		stockholmIds.add("3");

		List<BankAccountInformation> sfBankAccountInformationList = new ArrayList<>();
		BankAccountInformation accountInformation = new BankAccountInformation();
		accountInformation.setAccountbank("teststring");
		accountInformation.setAccount("teststring");
		accountInformation.setSfid("1");
		sfBankAccountInformationList.add(accountInformation);
		list.add(accountInformation);
		accountInformation = new BankAccountInformation();
		accountInformation.setAccountbank("teststring");
		accountInformation.setAccount("teststring");
		accountInformation.setSfid("3");
		sfBankAccountInformationList.add(accountInformation);

//		Assert.assertEquals(list, masterController.getListBankAccountInformationToInsert(salesForceIds, stockholmIds,
//				sfBankAccountInformationList));
//		stockholmIds = new ArrayList<>();
//		Assert.assertEquals(2, masterController
//				.getListBankAccountInformationToInsert(salesForceIds, stockholmIds, sfBankAccountInformationList)
//				.size());
	}

	@Test
	public void test_getListBankAccountInformationToUpdate() {
		List<BankAccountInformation> list = new ArrayList<>();

		List<String> salesForceIds = new ArrayList<>();
		salesForceIds.add("1");
		salesForceIds.add("2");
		salesForceIds.add("3");

		List<String> stockholmIds = new ArrayList<>();
		stockholmIds.add("2");
		stockholmIds.add("3");

		List<BankAccountInformation> sfBankAccountInformationList = new ArrayList<>();
		BankAccountInformation accountInformation = new BankAccountInformation();
		accountInformation.setAccountbank("teststring");
		accountInformation.setAccount("teststring");
		accountInformation.setSfid("1");
		sfBankAccountInformationList.add(accountInformation);
		accountInformation.setAccountbank("teststring");
		accountInformation.setAccount("teststring");
		accountInformation.setSfid("2");
		list.add(accountInformation);
		accountInformation = new BankAccountInformation();
		accountInformation.setAccountbank("teststring");
		accountInformation.setAccount("teststring");
		accountInformation.setSfid("3");
		sfBankAccountInformationList.add(accountInformation);
		list.add(accountInformation);

//		Assert.assertEquals(list, masterController.getListBankAccountInformationToUpdate(salesForceIds, stockholmIds, sfBankAccountInformationList));
	}

	@Test
	public void test_truncateBankAccountInformation() {
		bankAccountInformationMapper.truncateBankAccountInformation();
	}

	@Test
	public void test_coreDateCreatBankAccountInformation() {
		ParameterRequest parameterRequest = new ParameterRequest();
		parameterRequest.setStartMode("test");
		parameterRequest.setStartTime("2020-01-0101:01:01");
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		masterController.coreDateCreatBankAccountInformation(parameterRequest, batchStatus);
	}
	
	@Test
	public void test_coreDateCreatBankMaster() {
		ParameterRequest parameterRequest = new ParameterRequest();
		parameterRequest.setStartMode("test");
		parameterRequest.setStartTime("2020-01-0101:01:01");
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		masterController.coreDateCreatBankMaster(parameterRequest, batchStatus);
	}
}
