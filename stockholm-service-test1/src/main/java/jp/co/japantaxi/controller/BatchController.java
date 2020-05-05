package jp.co.japantaxi.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jp.co.japantaxi.mapper.stockholm.BatchStatusMapper;
import jp.co.japantaxi.model.BatchStatus;
import jp.co.japantaxi.utils.DateTimeUtil;

@RestController
@RequestMapping("/batch")
public class BatchController {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchController.class);

  @Value("${batch.code}")
  private String batchCode;

  @Autowired
  public BatchStatusMapper batchStatusMapper;

  public BatchStatus getBatchStatus() {
    return batchStatusMapper.getBatchStatus(batchCode);
  }

  public BatchStatus error(BatchStatus batchStatus) {
    batchStatus.setStatus("error");
    return batchStatus;
  }

  public BatchStatus increase(BatchStatus batchStatus) {
    if (batchStatus.getSfapicallcount() == null) {
      batchStatus.setSfapicallcount(Integer.parseInt("1"));
      return batchStatus;
    }
    Integer sfapicallcount = batchStatus.getSfapicallcount() + 1;
    batchStatus.setSfapicallcount(sfapicallcount);
    return batchStatus;
  }

  public BatchStatus processing(BatchStatus batchStatus) {
    batchStatus.setStatus("processing");
    batchStatus.setStatusinfo("");
    batchStatus.setProcessstartdate(
        Timestamp.valueOf(LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
    return batchStatus;
  }

  public BatchStatus reset(BatchStatus batchStatus) {
    batchStatus.setStatus(null);
    batchStatus
        .setProcessenddate(Timestamp.valueOf(LocalDateTime.now(DateTimeUtil.TIMEZONE_TOKYO.toZoneId())));
    return batchStatus;
  }

  public void updateBatchStatus(BatchStatus batchStatus, Boolean processing, Boolean reset,
      Boolean error, Boolean increase) {
    if (processing) {
      batchStatusMapper.updateBatchStatus(processing(batchStatus));
    } else if (reset) {
      batchStatusMapper.updateBatchStatus(reset(batchStatus));
      LOGGER.info("Update reset status success !!!");
    } else if (error) {
      batchStatusMapper.updateBatchStatus(error(batchStatus));
      LOGGER.info("Update error status success !!!");
    } else if (increase) {
      batchStatusMapper.updateBatchStatus(increase(batchStatus));
      LOGGER.info("Update increase call SF API success !!!");
    } else {
      batchStatusMapper.updateBatchStatus(batchStatus);
      LOGGER.info("Update new cron fromdatetime success !!!");
    }
  }

}
