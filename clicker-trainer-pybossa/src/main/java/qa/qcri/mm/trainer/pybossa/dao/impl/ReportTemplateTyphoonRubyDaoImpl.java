package qa.qcri.mm.trainer.pybossa.dao.impl;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.trainer.pybossa.dao.ReportTemplateTyphoonRubyDao;
import qa.qcri.mm.trainer.pybossa.entity.ReportTemplateTyphoonRuby;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 10/20/13
 * Time: 1:46 AM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class ReportTemplateTyphoonRubyDaoImpl extends AbstractDaoImpl<ReportTemplateTyphoonRuby, String> implements ReportTemplateTyphoonRubyDao {

    protected ReportTemplateTyphoonRubyDaoImpl(){
        super(ReportTemplateTyphoonRuby.class);
    }
    
	@Override
	public List<ReportTemplateTyphoonRuby> getReportTemplateTyphoonRubyByTweetId(Long tweetId) {
		return  findByCriteria(Restrictions.eq("tweetID", tweetId.toString()));
	}

}
