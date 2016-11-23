package qa.qcri.mm.trainer.pybossa.controller;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import qa.qcri.mm.trainer.pybossa.service.ClientAppCreateWorker;
import qa.qcri.mm.trainer.pybossa.service.MicroMapperWorker;
import qa.qcri.mm.trainer.pybossa.service.Worker;
import qa.qcri.mm.trainer.pybossa.store.LookupCode;


/**
 * A synchronous worker
 */
@Component("syncWorker")
public class SyncWorker implements Worker {

	protected static Logger logger = Logger.getLogger("SyncWorker");

    @Autowired
    private MicroMapperWorker microMapperWorker;

    @Autowired
    private ClientAppCreateWorker clientAppWorker;

	@Override
	public void work() {
		String threadName = Thread.currentThread().getName();
		logger.info("   " + threadName + " has began working.(SyncWorker - run ClientApps)");
        Calendar cal = Calendar.getInstance();
        try {
            microMapperWorker.processTaskPublish();
            microMapperWorker.processTaskImport();
            microMapperWorker.processTaskExport();

            clientAppWorker.doCreateApp();
            int hour = cal.get(Calendar.HOUR_OF_DAY) ;
            if(hour == LookupCode.CLIENT_APP_DELETION_TIME){
                clientAppWorker.doAppDelete();
            }
           // Thread.sleep(180000);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("Exception in Sync Worker thread: ", e);
        } catch (Exception e) {
            logger.info("Exception in Sync Worker : ", e);
        }
    }
	
}
