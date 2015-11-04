package qa.qcri.mm.trainer.pybossa.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 10/19/13
 * Time: 9:34 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml", "classpath:spring/hibernateContext.xml"})

public class MicroMapperWorkerTest {

    @Autowired
    private MicroMapperWorker microMapperWorker;

    @Test
    public void testProcessTaskPublish() throws Exception {
    	long startTime = System.nanoTime();
      //  List<TaskQueueResponse> responseList =  clientAppResponseService.getTaskQueueResponseByContent();
     // microMapperWorker.processTaskPublish();
     //
      //microMapperWorker.processTaskImport();
     // microMapperWorker.processTaskExport();
      // microMapperWorker.processTaskImportOnDemand();
      long endTime = System.nanoTime();
      double duration = (endTime - startTime) / 1000000000.0;
      System.out.println("\n\n\nTotal time elapsed: "+ duration + " seconds\n\n\n");
    }
    
    @Test
    public void processTyphoonRubyImageClikcer() throws Exception{
    	//microMapperWorker.processTyphoonRubyImageClikcer();
    }
    
    @Test
    public void processTyphoonRubyTextClikcer() throws Exception{
    	microMapperWorker.processTyphoonRubyTextClikcer();
    }

}
