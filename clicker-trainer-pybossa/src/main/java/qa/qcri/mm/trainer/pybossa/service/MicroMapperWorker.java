package qa.qcri.mm.trainer.pybossa.service;

import org.json.simple.parser.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 10/19/13
 * Time: 9:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MicroMapperWorker {

    void processTaskPublish() throws Exception;
    void processTaskImport() throws Exception;
    void processTaskExport() throws Exception;
    void processTaskImportOnDemand(String shortName) throws Exception;    
    public void processTyphoonRubyImageClikcer() throws ParseException;
	void processTyphoonRubyTextClikcer() throws ParseException;
}
