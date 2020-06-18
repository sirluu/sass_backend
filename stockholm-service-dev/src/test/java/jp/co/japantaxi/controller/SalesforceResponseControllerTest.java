package jp.co.japantaxi.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import jp.co.japantaxi.config.CacheManagerConfig;
import jp.co.japantaxi.model.BatchStatus;
import jp.co.japantaxi.model.ParameterRequest;
import jp.co.japantaxi.utils.Constant;

@RunWith(SpringRunner.class)
@WebMvcTest(value = SalesforceResponseControllerTest.class)
public class SalesforceResponseControllerTest {

	@MockBean
	private BatchController batchController;

	@Autowired
	public SalesforceResponseController salesforceResponseController;

	@Autowired
	public AccountController accountController;

	@Autowired
	public AppCompanyController appCompanyController;

	@Autowired
	public BankMasterController bankMasterController;

	@Autowired
	public PaymentController paymentController;

	@Autowired
	private CacheManagerConfig cacheManagerConfig;

	public void test_callsalesforce() {
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		cacheManagerConfig.setToken(null);
		cacheManagerConfig.setFromDateTime("2020-01-01 01:01:01");
		cacheManagerConfig.setToDateTime("2020-04-04 01:01:01");
		cacheManagerConfig.setNextPageToken(null);
		salesforceResponseController.callSalesforce("context", batchStatus);
	}
	
	@Test
	public void test_callsalesforce_401() {
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		cacheManagerConfig.setToken("test");
		cacheManagerConfig.setFromDateTime("2020-01-01 01:01:01");
		cacheManagerConfig.setToDateTime("2020-04-04 01:01:01");
		cacheManagerConfig.setNextPageToken("test");
		salesforceResponseController.callSalesforce("context", batchStatus);
	}
	
	@Test
	public void test_getlistobjectfromsalesforce() {
		test_callsalesforce();
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		Mockito.when(batchController.getBatchStatus()).thenReturn(batchStatus);
		salesforceResponseController.getListObjectFromSalesforce("context", batchStatus);
	}

	@Test
	public void test_getlistobjectfromsalesforce_4xx() {
		test_callsalesforce();
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		Mockito.when(batchController.getBatchStatus()).thenReturn(batchStatus);
		salesforceResponseController.getListObjectFromSalesforce("context", batchStatus);
	}
	
	@Test
	public void test_getlistaccountfromsalesforce() {
		test_callsalesforce();
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		Mockito.when(batchController.getBatchStatus()).thenReturn(batchStatus);
		salesforceResponseController.getListAccountFromSalesforce(batchStatus);
		ParameterRequest parameterRequest = new ParameterRequest();
		accountController.getSFAccount(parameterRequest, batchStatus);
	}

	@Test
	public void test_getlistfaretablefromsalesforce() {
		test_callsalesforce();
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		Mockito.when(batchController.getBatchStatus()).thenReturn(batchStatus);
		salesforceResponseController.getListFareTableFromSalesforce(batchStatus);
		ParameterRequest parameterRequest = new ParameterRequest();
		accountController.getSFFareTable(parameterRequest, batchStatus);
	}

	@Test
	public void test_getlistappcompanyfromsalesforce() {
		test_callsalesforce();
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		Mockito.when(batchController.getBatchStatus()).thenReturn(batchStatus);
		salesforceResponseController.getListAppCompanyFromSalesforce(batchStatus);
		ParameterRequest parameterRequest = new ParameterRequest();
		appCompanyController.getSFAppCompany(parameterRequest, batchStatus);
	}
	
	@Test
	public void test_getlistbankmasterfromsalesforce() {
		test_callsalesforce();
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		Mockito.when(batchController.getBatchStatus()).thenReturn(batchStatus);
		salesforceResponseController.getListBankMasterFromSalesforce(batchStatus);
		ParameterRequest parameterRequest = new ParameterRequest();
		bankMasterController.getSFBankMaster(parameterRequest, batchStatus);
	}

	@Test
	public void test_getlistbankaccountinformationfromsalesforce() {
		test_callsalesforce();
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		Mockito.when(batchController.getBatchStatus()).thenReturn(batchStatus);
		salesforceResponseController.getListBankAccountInformationFromSalesforce(batchStatus);
		ParameterRequest parameterRequest = new ParameterRequest();
		bankMasterController.getSFBankAccountInformation(parameterRequest, batchStatus);
	}
	
	@Test
	public void test_getlistpaymentsystemlinkinforfromsalesforce() {
		test_callsalesforce();
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		Mockito.when(batchController.getBatchStatus()).thenReturn(batchStatus);
		salesforceResponseController.getListPaymentSystemLinkInforFromSalesforce(batchStatus);
		ParameterRequest parameterRequest = new ParameterRequest();
		paymentController.getSFPaymentSystemLinkInfor(parameterRequest, batchStatus);
	}

}
