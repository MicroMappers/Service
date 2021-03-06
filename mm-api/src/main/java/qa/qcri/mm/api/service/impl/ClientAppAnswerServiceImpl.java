package qa.qcri.mm.api.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.api.dao.ClientAppAnswerDao;
import qa.qcri.mm.api.entity.ClientAppAnswer;
import qa.qcri.mm.api.service.ClientAppAnswerService;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 6/16/14
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("clientAppAnswerService")
@Transactional
public class ClientAppAnswerServiceImpl implements ClientAppAnswerService{

    protected static Logger logger = Logger.getLogger("clientAppAnswerService");

    @Autowired
    ClientAppAnswerDao clientAppAnswerDao;

    @Override
    public ClientAppAnswer getClientAppAnswer(Long clientAppID) {
        List<ClientAppAnswer> answers = clientAppAnswerDao.getClientAppAnswer(clientAppID);
        if(answers.size() > 0)
            return answers.get(0);
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    
    @Override
    public void update(ClientAppAnswer clientAppAnswer) {
    	clientAppAnswerDao.update(clientAppAnswer);
    }
}
