package qa.qcri.mm.api.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qa.qcri.mm.api.entity.FilteredTaskRun;
import qa.qcri.mm.api.entity.NamibiaReport;
import qa.qcri.mm.api.service.NamibiaReportService;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 9/29/14
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping("/namibia")
public class NamibiaReportController {

    protected static Logger logger = Logger.getLogger("NamibiaReportController");

    @Autowired
    NamibiaReportService namibiaReportService;

    @RequestMapping(value = "/reports", method = RequestMethod.GET)
    public List<NamibiaReport> getSummeryReport(){
        return namibiaReportService.getSummerydDataSetForReport();

    }

    @RequestMapping(value = "/jsonp/reports", method = RequestMethod.GET)
    public String getJSONPSummeryReport(){
        return "jsonp(" + namibiaReportService.getJSONSummerydDataSetForReport().toJSONString() + ");";
    }
    
    @RequestMapping(value = "/jsonp/image/source/{source}", method = RequestMethod.GET)
    public String getJSONPSourceImageReport(@PathVariable("source") String source){
        return "jsonp(" + namibiaReportService.getJSONDataSetBySource(source).toJSONString() + ");";
    }

    @RequestMapping(value = "/taskrun/{taskID}", method = RequestMethod.GET)
    public List<FilteredTaskRun> getTaskRunResult(@PathVariable("taskID") Long taskID){
        return namibiaReportService.getFilteredTaskRunByTask(taskID);

    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String getTester(){
        return "{\"status\":\"working\"}";

    }

}
