package jp.co.japantaxi.controller;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import jp.co.japantaxi.config.CacheManagerConfig;
import jp.co.japantaxi.mapper.stockholm.AppCompanyMapper;
import jp.co.japantaxi.model.AppCompany;
import jp.co.japantaxi.model.BatchStatus;
import jp.co.japantaxi.model.ParameterRequest;

@RunWith(SpringRunner.class)
@WebMvcTest(value = AppCompanyController.class)
public class AppCompanyControllerTest {

	@Autowired
	private AppCompanyController companyController;

	@Autowired
	public SalesforceResponseController salesforceResponseController;

	@MockBean
	public SalesforceResponseControllerTest salesforceResponseControllerTest;

	@MockBean
	private AppCompanyMapper appCompanyMapper;

	@MockBean
	private CacheManagerConfig cacheManagerConfig;

	@Mock
	RestTemplate restTemplate;

	@Test
	public void test_insertAppCompany() {
		AppCompany expected = new AppCompany();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		List<AppCompany> list = new ArrayList<AppCompany>();
		list.add(expected);
		appCompanyMapper.insertAppCompany(expected);
		companyController.insertAppCompany(list);
	}

	@Test
	public void test_updateAppCompany() {
		AppCompany expected = new AppCompany();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		List<AppCompany> list = new ArrayList<AppCompany>();
		list.add(expected);
		appCompanyMapper.updateAppCompany(expected);
		companyController.updateAppCompany(list);
	}

	@Test
	public void test_insertAppCompanySync() {
		AppCompany expected = new AppCompany();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		expected.setDispatchtype("teststring");
		expected.setPhonenumber("teststring");
		expected.setPhonenumbercancel("teststring");
		expected.setCompanylogoimage("teststring");
		List<AppCompany> list = new ArrayList<AppCompany>();
		list.add(expected);
		appCompanyMapper.insertAppCompanySync(expected);
		companyController.insertAppCompanySync(list);
	}

	@Test
	public void test_updateAppCompanySync() throws Exception {
		AppCompany expected = new AppCompany();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		expected.setDispatchtype("teststring");
		expected.setPhonenumber("teststring");
		expected.setPhonenumbercancel("teststring");
		expected.setCompanylogoimage("teststring");
		List<AppCompany> list = new ArrayList<AppCompany>();
		list.add(expected);
		companyController.updateAppCompanySync(list);
		list.remove(expected);
		companyController.updateAppCompanySync(list);
	}

	@Test
	public void test_getListAppCompanyFromStockholm() {
		List<String> list = new ArrayList<>();
		Mockito.when(cacheManagerConfig.getListObjectId("AppCompany")).thenReturn(list);
		Assert.assertEquals(companyController.getListAppCompanyFromStockholm(), list);
	}

	@Test
	public void test_getListAppCompanyIdFromStockholm() {
		List<AppCompany> list = new ArrayList<>();
		AppCompany add = new AppCompany();
		add.setSfid("1");
		list.add(add);
		companyController.getListAppCompanyIdFromStockholm();
		companyController.getListAppCompanyIdFromStockholm(list);
	}
	
	@Test
	public void test_truncateAppCompany() {
		appCompanyMapper.truncateAppCompany();
	}

	@Test
	public void test_getListAppCompanyToInsert() {
		List<AppCompany> list = new ArrayList<>();

		List<String> salesForceIds = new ArrayList<>();
		salesForceIds.add("1");
		salesForceIds.add("2");
		salesForceIds.add("3");

		List<String> stockholmIds = new ArrayList<>();
		stockholmIds.add("2");
		stockholmIds.add("3");

		List<AppCompany> sfAppCompanyList = new ArrayList<>();
		AppCompany appCompany = new AppCompany();
		appCompany.setAccount("teststring");
		appCompany.setSfid("1");
		sfAppCompanyList.add(appCompany);
		list.add(appCompany);
		appCompany = new AppCompany();
		appCompany.setAccount("teststring");
		appCompany.setSfid("3");
		sfAppCompanyList.add(appCompany);

		Assert.assertEquals(list,
				companyController.getListAppCompanyToInsert(salesForceIds, stockholmIds, sfAppCompanyList));
		stockholmIds = new ArrayList<>();
		Assert.assertEquals(2,
				companyController.getListAppCompanyToInsert(salesForceIds, stockholmIds, sfAppCompanyList).size());
	}

	@Test
	public void test_getListAppCompanyToUpdate() {
		List<AppCompany> list = new ArrayList<>();

		List<String> salesForceIds = new ArrayList<>();
		salesForceIds.add("1");
		salesForceIds.add("2");
		salesForceIds.add("3");

		List<String> stockholmIds = new ArrayList<>();
		stockholmIds.add("2");
		stockholmIds.add("3");

		List<AppCompany> sfAppCompanyList = new ArrayList<>();
		AppCompany taxiComptactAddress = new AppCompany();
		taxiComptactAddress.setAccount("teststring");
		taxiComptactAddress.setSfid("1");
		sfAppCompanyList.add(taxiComptactAddress);
		list.add(taxiComptactAddress);
		taxiComptactAddress = new AppCompany();
		taxiComptactAddress.setAccount("teststring");
		taxiComptactAddress.setSfid("3");
		sfAppCompanyList.add(taxiComptactAddress);

		Assert.assertEquals(list.size(),
				companyController.getListAppCompanyToUpdate(salesForceIds, stockholmIds, sfAppCompanyList).size());
		stockholmIds = new ArrayList<>();
		Assert.assertEquals(0,
				companyController.getListAppCompanyToUpdate(salesForceIds, stockholmIds, sfAppCompanyList).size());
	}

	@Test
	public void test_coreDateCreatAppCompany() {
		ParameterRequest parameterRequest = new ParameterRequest();
		parameterRequest.setStartMode("test");
		parameterRequest.setStartTime("2020-01-0101:01:01");
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		companyController.coreDateCreatAppCompany(parameterRequest, batchStatus);
	}
}
