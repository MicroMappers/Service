package qa.qcri.mm.trainer.pybossa.service.impl;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import qa.qcri.mm.trainer.pybossa.service.Worker;

/**
 * Scheduler for handling jobs
 */
@Service
public class SchedulerService {

	protected static Logger logger = Logger.getLogger("service");

	@Autowired
	@Qualifier("syncWorker")
	private Worker syncWorker;

	//@Scheduled(cron="0/3 * * * * ?")
	//@Scheduled(cron="0 0/3 * * * ?")
	@Scheduled(fixedDelay = 10000)
	public void doSchedule() {
		logger.debug("Start schedule");
        syncWorker.work();
        logger.debug("End schedule");
	}
	

}
