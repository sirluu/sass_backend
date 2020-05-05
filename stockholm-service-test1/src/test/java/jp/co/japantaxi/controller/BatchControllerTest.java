package jp.co.japantaxi.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import jp.co.japantaxi.mapper.stockholm.BatchStatusMapper;
import jp.co.japantaxi.model.BatchStatus;
import jp.co.japantaxi.utils.DateTimeUtil;
import jp.co.japantaxi.utils.Utility;

@RunWith(SpringRunner.class)
@WebMvcTest(value = BatchController.class)
public class BatchControllerTest {

	@MockBean
	public BatchStatusMapper batchStatusMapper;

	@Autowired
	public BatchController batchController;

	@Test
	public void test_getBatchControl() {
		BatchStatus batchStatus = new BatchStatus();
		batchStatus.setBatchcode("SBAT-001");
		batchStatus.setBatchname("teststring");
		batchStatus.setProcessenddate(
				DateTimeUtil.getTimestampFromString("2020-01-01 17:50:01", DateTimeUtil.DATE_TIME_FM));
		batchStatus.setProcessstartdate(
				DateTimeUtil.getTimestampFromString("2020-01-15 17:50:01", DateTimeUtil.DATE_TIME_FM));
		batchStatus.setSfapicallcount(Utility.parseInt("123"));
		batchStatus.setStartupmode("teststring");
		batchStatus.setStatus("teststring");
		batchStatus.setStatusinfo("teststring");
		Mockito.when(batchStatusMapper.getBatchStatus("SBAT-001")).thenReturn(batchStatus);
		Assert.assertEquals(batchStatus, batchController.getBatchStatus());
	}

	@Test
	public void test_updateBatchStatus() {
		BatchStatus batchStatus = new BatchStatus();
		batchStatus.setBatchcode("SBAT-001");
		batchStatus.setBatchname("teststring");
		batchStatus.setProcessenddate(
				DateTimeUtil.getTimestampFromString("2020-01-01 17:50:01", DateTimeUtil.DATE_TIME_FM));
		batchStatus.setProcessstartdate(
				DateTimeUtil.getTimestampFromString("2020-01-15 17:50:01", DateTimeUtil.DATE_TIME_FM));
		batchStatus.setSfapicallcount(null);
		batchStatus.setStartupmode("teststring");
		batchStatus.setStatus("teststring");
		batchStatus.setStatusinfo("teststring");
		Mockito.when(batchStatusMapper.getBatchStatus("SBAT-001")).thenReturn(batchStatus);
		Assert.assertEquals(batchStatus, batchController.increase(batchStatus));
		batchController.updateBatchStatus(batchStatus, false, false, false, true);
		batchController.updateBatchStatus(batchStatus, false, false, true, false);
		batchController.updateBatchStatus(batchStatus, false, true, false, false);
		batchController.updateBatchStatus(batchStatus, true, false, false, false);
		batchStatus.setFromdatetime(DateTimeUtil.getTimestampFromString("2020-01-15 17:50:01", DateTimeUtil.DATE_TIME_FM));
		batchController.updateBatchStatus(batchStatus, false, false, false, false);
	}

	public static BatchStatus getBatchControl() {
		BatchStatus batchStatus = new BatchStatus();
		batchStatus.setBatchcode("SBAT-001");
		batchStatus.setBatchname("teststring");
		batchStatus.setProcessenddate(
				DateTimeUtil.getTimestampFromString("2020-01-01 17:50:01", DateTimeUtil.DATE_TIME_FM));
		batchStatus.setProcessstartdate(
				DateTimeUtil.getTimestampFromString("2020-01-15 17:50:01", DateTimeUtil.DATE_TIME_FM));
		batchStatus.setSfapicallcount(Utility.parseInt("123"));
		batchStatus.setStartupmode("teststring");
		batchStatus.setStatus(null);
		batchStatus.setStatusinfo("teststring");
		batchStatus.setFromdatetime(DateTimeUtil.getTimestampFromString("2020-01-01 01:01:01", DateTimeUtil.DATE_TIME_FM));
		return batchStatus;
	}
}
