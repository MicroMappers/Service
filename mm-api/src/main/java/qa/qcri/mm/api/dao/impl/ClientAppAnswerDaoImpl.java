package qa.qcri.mm.api.dao.impl;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.api.dao.ClientAppAnswerDao;
import qa.qcri.mm.api.entity.ClientAppAnswer;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 6/15/14
 * Time: 1:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class ClientAppAnswerDaoImpl extends AbstractDaoImpl<ClientAppAnswer, String> implements ClientAppAnswerDao {

    protected ClientAppAnswerDaoImpl(){
        super(ClientAppAnswer.class);
    }

    @Override
    public List<ClientAppAnswer> getClientAppAnswer(Long clientAppID) {
         return findByCriteria(Restrictions.eq("clientAppID", clientAppID));
    }
    
    @Override
    public void update(ClientAppAnswer clientAppAnswer) {
        saveOrUpdate(clientAppAnswer);
    }

}
