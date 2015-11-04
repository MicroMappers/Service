package qa.qcri.mm.trainer.pybossa.dao;

import java.util.List;

import qa.qcri.mm.trainer.pybossa.entity.ReportTemplateTyphoonRuby;

/**
 * 
 * @author Aman
 *
 */
public interface ReportTemplateTyphoonRubyDao extends AbstractDao<ReportTemplateTyphoonRuby, String> {

	List<ReportTemplateTyphoonRuby> getReportTemplateTyphoonRubyByTweetId(Long tweetId);
	
}
