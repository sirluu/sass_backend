package jp.co.japantaxi.controller;

import static org.mockito.Mockito.mock;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import jp.co.japantaxi.config.CacheManagerConfig;
import jp.co.japantaxi.config.SalesforceConfig;
import jp.co.japantaxi.mapper.stockholm.WorkerMapper;
import jp.co.japantaxi.model.BatchStatus;
import jp.co.japantaxi.model.ParameterRequest;
import jp.co.japantaxi.model.Worker;
import jp.co.japantaxi.utils.Constant;

@RunWith(SpringRunner.class)
@WebMvcTest(value = WorkerController.class)
public class WorkerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BatchController batchController;

  @Autowired
  private WorkerController workerController;

  @MockBean
  private CacheManagerConfig cacheManagerConfig;

  @MockBean
  private SalesforceConfig salesforceConfig;

  @MockBean
  private WorkerMapper workerMapper;

  @Test
  public void test_valid_startmode_exception() {
    ParameterRequest parameterRequest = new ParameterRequest();
    parameterRequest.setStartMode("test");
    parameterRequest.setStartTime("2020-01-01 01:01:01");
    Assert.assertEquals(400,
        workerController.validParameter(parameterRequest).getStatusCode().value());
  }

  @Test
  public void test_valid_startmode() {
    ParameterRequest parameterRequest = new ParameterRequest();
    workerController.validParameter(parameterRequest);
  }

  @Test
  public void test_valid_starttime_exception() {
    ParameterRequest parameterRequest = new ParameterRequest();
    parameterRequest.setStartMode(Constant.STARTMODE.CRON.value);
    parameterRequest.setProcessMode(Constant.PROCESSMODE.GETSF.value);
    Assert.assertEquals(400,
        workerController.validParameter(parameterRequest).getStatusCode().value());
  }

  @Test
  public void test_validparameter() {
    ParameterRequest parameterRequest = new ParameterRequest();
    parameterRequest.setIds("a04N000000KgpbkIAB");
    parameterRequest.setStartMode(Constant.STARTMODE.MANUAL.value);
    parameterRequest.setProcessMode(Constant.PROCESSMODE.GETSF.value);
    parameterRequest.setStartTime("2020-01-01 01:01:01");
    Assert.assertEquals(200,
        workerController.validParameter(parameterRequest).getStatusCode().value());
  }

  @Test
  public void test_valid_starttime() {
    ParameterRequest parameterRequest = new ParameterRequest();
    parameterRequest.setStartMode(Constant.STARTMODE.CRON.value);
    parameterRequest.setProcessMode(Constant.PROCESSMODE.GETSF.value);
    parameterRequest.setStartTime("2020-01-01 01:01:01");
    Assert.assertEquals(200,
        workerController.validParameter(parameterRequest).getStatusCode().value());
  }

  @Test
  public void test_invalid_starttime() {
    ParameterRequest parameterRequest = new ParameterRequest();
    parameterRequest.setStartMode(Constant.STARTMODE.CRON.value);
    parameterRequest.setProcessMode(Constant.PROCESSMODE.GETSF.value);
    parameterRequest.setStartTime("2020-01-0101:01:01");
    workerController.validParameter(parameterRequest);
  }

  @Test
  public void test_manual_getsf_synchronize() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);  
    cacheManagerConfig.setToken(null);
    cacheManagerConfig.setFromDateTime("2020-01-01 01:01:01");
    cacheManagerConfig.setToDateTime("2020-04-04 01:01:01");
    cacheManagerConfig.setNextPageToken("W3gsCltSwhdQS45wLc");
    ParameterRequest parameterRequest = new ParameterRequest();
    parameterRequest.setStartMode(Constant.STARTMODE.MANUAL.value);
    parameterRequest.setProcessMode(Constant.PROCESSMODE.GETSF.value);
    parameterRequest.setStartTime("2020-01-13 11:30:01");
    BatchStatus batchStatus = BatchControllerTest.getBatchControl();
    Mockito.when(batchController.getBatchStatus()).thenReturn(batchStatus);
    workerController.synchronize(parameterRequest, request);

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("http://localhost:8888/stockholm/synchronize").accept(MediaType.APPLICATION_JSON)
        .content("{\r\n" + " \"startMode\": \"manual\",\r\n" + " \"processMode\": \"getsf\",\r\n"
            + " \"startTime\": \"2020-01-13 11:30:01\"\r\n" + "}")
        .contentType(MediaType.APPLICATION_JSON);
    mockMvc.perform(requestBuilder).andReturn();
  }

  @Test
  public void test_cron_getsf_synchronize() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);  
    cacheManagerConfig.setToken(null);
    cacheManagerConfig.setFromDateTime("2020-01-01 01:01:01");
    cacheManagerConfig.setToDateTime("2020-04-04 01:01:01");
    cacheManagerConfig.setNextPageToken("W3gsCltSwhdQS45wLc");
    ParameterRequest parameterRequest = new ParameterRequest();
    parameterRequest.setStartMode(Constant.STARTMODE.CRON.value);
    parameterRequest.setProcessMode(Constant.PROCESSMODE.GETSF.value);
    parameterRequest.setStartTime("2020-01-13 11:30:01");
    BatchStatus batchStatus = BatchControllerTest.getBatchControl();
    Mockito.when(batchController.getBatchStatus()).thenReturn(batchStatus);
    workerController.synchronize(parameterRequest, request);

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("http://localhost:8888/stockholm/synchronize").accept(MediaType.APPLICATION_JSON)
        .content("{\r\n" + " \"startMode\": \"cron\",\r\n" + " \"processMode\": \"getsf\",\r\n"
            + " \"startTime\": \"2020-01-13 11:30:01\"\r\n" + "}")
        .contentType(MediaType.APPLICATION_JSON);
    mockMvc.perform(requestBuilder).andReturn();
  }

  @Test
  public void test_manual_coredatecreat_synchronize() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);  
    cacheManagerConfig.setToken(null);
    cacheManagerConfig.setFromDateTime("2020-01-01 01:01:01");
    cacheManagerConfig.setToDateTime("2020-04-04 01:01:01");
    cacheManagerConfig.setNextPageToken("W3gsCltSwhdQS45wLc");
    ParameterRequest parameterRequest = new ParameterRequest();
    parameterRequest.setStartMode(Constant.STARTMODE.MANUAL.value);
    parameterRequest.setProcessMode(Constant.PROCESSMODE.COREDATECREAT.value);
    parameterRequest.setStartTime("2020-01-13 11:30:01");
    BatchStatus batchStatus = BatchControllerTest.getBatchControl();
    Mockito.when(batchController.getBatchStatus()).thenReturn(batchStatus);
    workerController.synchronize(parameterRequest, request);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("http://localhost:8888/stockholm/synchronize")
            .accept(MediaType.APPLICATION_JSON)
            .content("{\r\n" + " \"startMode\": \"manual\",\r\n"
                + " \"processMode\": \"coredatecreat\",\r\n"
                + " \"startTime\": \"2020-01-13 11:30:01\"\r\n" + "}")
            .contentType(MediaType.APPLICATION_JSON);
    mockMvc.perform(requestBuilder).andReturn();
  }

  @Test
  public void test_cron_coredatecreat_synchronize() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);  
    cacheManagerConfig.setToken(null);
    cacheManagerConfig.setFromDateTime("2020-01-01 01:01:01");
    cacheManagerConfig.setToDateTime("2020-04-04 01:01:01");
    cacheManagerConfig.setNextPageToken("W3gsCltSwhdQS45wLc");
    ParameterRequest parameterRequest = new ParameterRequest();
    parameterRequest.setStartMode(Constant.STARTMODE.CRON.value);
    parameterRequest.setProcessMode(Constant.PROCESSMODE.COREDATECREAT.value);
    parameterRequest.setStartTime("2020-01-13 11:30:01");
    BatchStatus batchStatus = BatchControllerTest.getBatchControl();
    Mockito.when(batchController.getBatchStatus()).thenReturn(batchStatus);
    workerController.synchronize(parameterRequest, request);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("http://localhost:8888/stockholm/synchronize")
            .accept(MediaType.APPLICATION_JSON)
            .content("{\r\n" + " \"startMode\": \"cron\",\r\n"
                + " \"processMode\": \"coredatecreat\",\r\n"
                + " \"startTime\": \"2020-01-13 11:30:01\"\r\n" + "}")
            .contentType(MediaType.APPLICATION_JSON);
    mockMvc.perform(requestBuilder).andReturn();
  }

  @Test
  public void test_manual_clear_synchronize() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);  
    cacheManagerConfig.setToken(null);
    cacheManagerConfig.setFromDateTime("2020-01-01 01:01:01");
    cacheManagerConfig.setToDateTime("2020-04-04 01:01:01");
    cacheManagerConfig.setNextPageToken("W3gsCltSwhdQS45wLc");
    ParameterRequest parameterRequest = new ParameterRequest();
    parameterRequest.setStartMode(Constant.STARTMODE.MANUAL.value);
    parameterRequest.setProcessMode(Constant.PROCESSMODE.CLEAR.value);
    parameterRequest.setStartTime("2020-01-13 11:30:01");
    BatchStatus batchStatus = BatchControllerTest.getBatchControl();
    Mockito.when(batchController.getBatchStatus()).thenReturn(batchStatus);
    workerController.synchronize(parameterRequest, request);

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("http://localhost:8888/stockholm/synchronize").accept(MediaType.APPLICATION_JSON)
        .content("{\r\n" + " \"startMode\": \"manual\",\r\n" + " \"processMode\": \"clear\",\r\n"
            + " \"startTime\": \"2020-01-13 11:30:01\"\r\n" + "}")
        .contentType(MediaType.APPLICATION_JSON);
    mockMvc.perform(requestBuilder).andReturn();
  }

  @Test
  public void test_manual_null_synchronize() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);  
    cacheManagerConfig.setToken(null);
    cacheManagerConfig.setFromDateTime("2020-01-01 01:01:01");
    cacheManagerConfig.setToDateTime("2020-04-04 01:01:01");
    cacheManagerConfig.setNextPageToken("test");
    ParameterRequest parameterRequest = new ParameterRequest();
    parameterRequest.setStartMode(Constant.STARTMODE.MANUAL.value);
    parameterRequest.setStartTime("2020-01-13 11:30:01");

    BatchStatus batchStatus = BatchControllerTest.getBatchControl();
    Mockito.when(batchController.getBatchStatus()).thenReturn(batchStatus);
    workerController.synchronize(parameterRequest, request);

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("http://localhost:8888/stockholm/synchronize").accept(MediaType.APPLICATION_JSON)
        .content("{\r\n" + " \"startMode\": \"manual\",\r\n"
            + " \"startTime\": \"2020-01-13 11:30:01\"\r\n" + "}")
        .contentType(MediaType.APPLICATION_JSON);
    mockMvc.perform(requestBuilder).andReturn();
  }

  @Test
  public void test_cron_null_synchronize() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);  
    cacheManagerConfig.setToken(null);
    cacheManagerConfig.setFromDateTime("2020-01-01 01:01:01");
    cacheManagerConfig.setToDateTime("2020-04-04 01:01:01");
    cacheManagerConfig.setNextPageToken("test");
    ParameterRequest parameterRequest = new ParameterRequest();
    parameterRequest.setStartMode(Constant.STARTMODE.CRON.value);
    parameterRequest.setProcessMode(Constant.PROCESSMODE.NULL.value);
    parameterRequest.setStartTime("2020-01-13 11:30:01");

    RequestBuilder requestBuilder = MockMvcRequestBuilders
        .post("http://localhost:8888/stockholm/synchronize").accept(MediaType.APPLICATION_JSON)
        .content("{\r\n" + " \"startMode\": \"cron\",\r\n"
            + " \"startTime\": \"2020-01-13 11:30:01\"\r\n" + "}")
        .contentType(MediaType.APPLICATION_JSON);
    mockMvc.perform(requestBuilder).andReturn();
    BatchStatus batchStatus = BatchControllerTest.getBatchControl();
    Mockito.when(batchController.getBatchStatus()).thenReturn(batchStatus);
    workerController.synchronize(parameterRequest, request);
  }

  @Test
  public void test_insertworker() {
    Worker expected = new Worker();
    workerController.setWorker("table");
    expected.setSfid("000000000000000000");
    workerMapper.insertWorker(expected);
    workerController.insertWorker(expected);
  }

  @Test
  public void test_updateWorkerr() {
    Worker expected = new Worker();
    workerController.setWorker("table");
    expected.setSfid("000000000000000000");
    workerMapper.updateWorker(expected);
    workerController.updateWorker(expected);
  }
}
