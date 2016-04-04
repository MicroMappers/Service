package qa.qcri.mm.api.dao;

import java.util.List;

import qa.qcri.mm.api.entity.ClientAppAnswer;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 6/15/14
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ClientAppAnswerDao extends AbstractDao<ClientAppAnswer, String> {

    List<ClientAppAnswer> getClientAppAnswer(Long clientAppID);

	void update(ClientAppAnswer clientAppAnswer);

}
