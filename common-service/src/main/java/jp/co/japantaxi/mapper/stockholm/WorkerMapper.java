package jp.co.japantaxi.mapper.stockholm;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import jp.co.japantaxi.model.Worker;

public interface WorkerMapper {

	@Select("select sfid, tablename, sycapproveflg, syncstatus, deleteflg, syncedtime  FROM worker where sfid=#{sfid}")
	Worker getProgressId(@Param("sfid") String sfid);

	void insertWorker(Worker worker);

	void updateWorker(Worker worker);

}
