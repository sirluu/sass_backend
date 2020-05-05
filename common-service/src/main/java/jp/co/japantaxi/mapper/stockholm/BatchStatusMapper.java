package jp.co.japantaxi.mapper.stockholm;

import jp.co.japantaxi.model.BatchStatus;

public interface BatchStatusMapper {

  BatchStatus getBatchStatus(String batchCode);

  void insertBatchStatus(BatchStatus batchStatus);

  void updateBatchStatus(BatchStatus batchStatus);

}
