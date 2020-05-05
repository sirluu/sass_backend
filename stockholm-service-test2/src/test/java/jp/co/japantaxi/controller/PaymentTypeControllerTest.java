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
import jp.co.japantaxi.mapper.stockholm.PaymentSystemLinkInforMapper;
import jp.co.japantaxi.model.BatchStatus;
import jp.co.japantaxi.model.ParameterRequest;
import jp.co.japantaxi.model.PaymentSystemLinkInfor;
import jp.co.japantaxi.utils.DateTimeUtil;
import jp.co.japantaxi.utils.Utility;

@RunWith(SpringRunner.class)
@WebMvcTest(value = PaymentController.class)
public class PaymentTypeControllerTest {

	@Autowired
	private PaymentController paymentController;

	@Autowired
	public SalesforceResponseController salesforceResponseController;

	@MockBean
	private PaymentSystemLinkInforMapper linkInforMapper;

	@MockBean
	private CacheManagerConfig cacheManager;

	@Mock
	RestTemplate restTemplate;

	@Test
	public void test_insertPaymentSystemLinkInfor() {
		PaymentSystemLinkInfor expected = new PaymentSystemLinkInfor();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		List<PaymentSystemLinkInfor> list = new ArrayList<PaymentSystemLinkInfor>();
		list.add(expected);
		linkInforMapper.insertPaymentSystemLinkInfor(expected);
		paymentController.insertPaymentSystemLinkInfor(list);
	}

	@Test
	public void test_updatePaymentSystemLinkInfor() {
		PaymentSystemLinkInfor expected = new PaymentSystemLinkInfor();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		List<PaymentSystemLinkInfor> list = new ArrayList<PaymentSystemLinkInfor>();
		list.add(expected);
		linkInforMapper.updatePaymentSystemLinkInfor(expected);
		paymentController.updatePaymentSystemLinkInfor(list);
	}

	@Test
	public void test_insertPaymentSystemLinkInforSync() {
		PaymentSystemLinkInfor expected = new PaymentSystemLinkInfor();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		expected.setServiceid("teststring");
		expected.setTofacemerchantid("teststring");
		expected.setNontofacemerchantid("teststring");
		expected.setLinepaychannelsecret("teststring");
		expected.setLinepaychannelid("teststring");
		expected.setLastmodifieddate(DateTimeUtil.getTimestampFromString("teststring", DateTimeUtil.DATE_TIME_FM));
		expected.setInternetpaymerstore("teststring");
		expected.setTofacepaymentrouteid(Utility.parseInt("1234"));
		expected.setNontofacepaymentrouteid(Utility.parseInt("1234"));
		expected.setTofacehashkey("teststring");
		expected.setNontofacehashkey("teststring");
		expected.setTofaceencryptkey("teststring");
		expected.setNontofaceencryptkey("teststring");
		expected.setTofaceencryptiv("teststring");
		expected.setNontofaceencryptiv("teststring");
		expected.setTofacecardtype("teststring");
		expected.setNontofacecardtype("teststring");
		expected.setTofacebasicauthpassword("teststring");
		expected.setNontofacebasicauthpassword("teststring");
		expected.setAupaystoreid("teststring");
		List<PaymentSystemLinkInfor> list = new ArrayList<PaymentSystemLinkInfor>();
		list.add(expected);
		linkInforMapper.insertPaymentSystemLinkInforSync(expected);
		paymentController.insertPaymentSystemLinkInforSync(list);
	}

	@Test
	public void test_updatePaymentSystemLinkInforSync() {
		PaymentSystemLinkInfor expected = new PaymentSystemLinkInfor();
		expected.setAccount("teststring");
		expected.setSfid("000000000000000000");
		expected.setServiceid("teststring");
		expected.setTofacemerchantid("teststring");
		expected.setNontofacemerchantid("teststring");
		expected.setLinepaychannelsecret("teststring");
		expected.setLinepaychannelid("teststring");
		expected.setLastmodifieddate(DateTimeUtil.getTimestampFromString("teststring", DateTimeUtil.DATE_TIME_FM));
		expected.setInternetpaymerstore("teststring");
		expected.setTofacepaymentrouteid(Utility.parseInt("1234"));
		expected.setNontofacepaymentrouteid(Utility.parseInt("1234"));
		expected.setTofacehashkey("teststring");
		expected.setNontofacehashkey("teststring");
		expected.setTofaceencryptkey("teststring");
		expected.setNontofaceencryptkey("teststring");
		expected.setTofaceencryptiv("teststring");
		expected.setNontofaceencryptiv("teststring");
		expected.setTofacecardtype("teststring");
		expected.setNontofacecardtype("teststring");
		expected.setTofacebasicauthpassword("teststring");
		expected.setNontofacebasicauthpassword("teststring");
		expected.setAupaystoreid("teststring");
		List<PaymentSystemLinkInfor> list = new ArrayList<PaymentSystemLinkInfor>();
		list.add(expected);
		linkInforMapper.updatePaymentSystemLinkInforSync(expected);
		paymentController.updatePaymentSystemLinkInforSync(list);
	}

	@Test
	public void test_getListPaymentSystemLinkInforFromStockholm() {
		List<String> list = new ArrayList<>();
		Mockito.when(cacheManager.getListObjectId("PaymentSystemLinkInfor")).thenReturn(list);
		Assert.assertEquals(paymentController.getListPaymentSystemLinkInforFromStockholm(), list);
	}

	@Test
	public void test_getListPaymentSystemLinkInforIdFromStockholm() {
		List<PaymentSystemLinkInfor> list = new ArrayList<>();
		PaymentSystemLinkInfor add = new PaymentSystemLinkInfor();
		add.setSfid("1");
		list.add(add);
		paymentController.getListPaymentSystemLinkInforIdFromStockholm();
		paymentController.getListPaymentSystemLinkInforIdFromStockholm(list);
	}

	@Test
	public void test_getListPaymentSystemLinkInforToInsert() {
		List<PaymentSystemLinkInfor> list = new ArrayList<>();

		List<String> salesForceIds = new ArrayList<>();
		salesForceIds.add("1");
		salesForceIds.add("2");
		salesForceIds.add("3");

		List<String> stockholmIds = new ArrayList<>();
		stockholmIds.add("2");
		stockholmIds.add("3");

		List<PaymentSystemLinkInfor> sfPaymentSystemLinkInforList = new ArrayList<>();
		PaymentSystemLinkInfor systemLinkInfor = new PaymentSystemLinkInfor();
		systemLinkInfor.setAccount("teststring");
		systemLinkInfor.setSfid("1");
		sfPaymentSystemLinkInforList.add(systemLinkInfor);
		list.add(systemLinkInfor);
		systemLinkInfor = new PaymentSystemLinkInfor();
		systemLinkInfor.setAccount("teststring");
		systemLinkInfor.setSfid("3");
		sfPaymentSystemLinkInforList.add(systemLinkInfor);

		Assert.assertEquals(list, paymentController.getListPaymentSystemLinkInforToInsert(salesForceIds, stockholmIds,
				sfPaymentSystemLinkInforList));
		stockholmIds = new ArrayList<>();
		Assert.assertEquals(2, paymentController
				.getListPaymentSystemLinkInforToInsert(salesForceIds, stockholmIds, sfPaymentSystemLinkInforList)
				.size());
	}

	@Test
	public void test_getListPaymentSystemLinkInforToUpdate() {
		List<PaymentSystemLinkInfor> list = new ArrayList<>();

		List<String> salesForceIds = new ArrayList<>();
		salesForceIds.add("1");
		salesForceIds.add("2");
		salesForceIds.add("3");

		List<String> stockholmIds = new ArrayList<>();
		stockholmIds.add("2");
		stockholmIds.add("3");

		List<PaymentSystemLinkInfor> sfPaymentSystemLinkInforList = new ArrayList<>();
		PaymentSystemLinkInfor systemLinkInfor = new PaymentSystemLinkInfor();
		systemLinkInfor.setAccount("teststring");
		systemLinkInfor.setSfid("1");
		sfPaymentSystemLinkInforList.add(systemLinkInfor);
		systemLinkInfor.setAccount("teststring");
		systemLinkInfor.setSfid("2");
		list.add(systemLinkInfor);
		systemLinkInfor = new PaymentSystemLinkInfor();
		systemLinkInfor.setAccount("teststring");
		systemLinkInfor.setSfid("3");
		sfPaymentSystemLinkInforList.add(systemLinkInfor);
		list.add(systemLinkInfor);

		Assert.assertEquals(list, paymentController.getListPaymentSystemLinkInforToUpdate(salesForceIds, stockholmIds,
				sfPaymentSystemLinkInforList));
	}

	@Test
	public void test_truncatePaymentSystemLinkInfor() {
		linkInforMapper.truncatePaymentSystemLinkInfor();
	}

	@Test
	public void test_coreDateCreatPaymentSystemLinkInfor() {
		ParameterRequest parameterRequest = new ParameterRequest();
		parameterRequest.setStartMode("test");
		parameterRequest.setStartTime("2020-01-0101:01:01");
		BatchStatus batchStatus = BatchControllerTest.getBatchControl();
		paymentController.coreDateCreatPaymentSystemLinkInfor(parameterRequest, batchStatus);
	}
	
}
