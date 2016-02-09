package qa.qcri.mm.api.dao.impl;

import java.util.Date;

import org.springframework.stereotype.Repository;

import qa.qcri.mm.api.dao.SourceImageDao;
import qa.qcri.mm.api.entity.SourceImage;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 3/28/15
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class SourceImageDaoImpl extends AbstractDaoImpl<SourceImage, Long> implements SourceImageDao {

    protected SourceImageDaoImpl(){
        super(SourceImage.class);
    }
    
    @Override
    public Long persist(SourceImage sourceImage){
    	sourceImage.setCreatedAt(new Date());
    	return save(sourceImage);
    }

}
