package qa.qcri.mm.api.dao.impl;

import java.util.Date;

import org.springframework.stereotype.Repository;

import qa.qcri.mm.api.dao.SlicedImageDao;
import qa.qcri.mm.api.entity.SlicedImage;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 3/28/15
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class SlicedImageDaoImpl extends AbstractDaoImpl<SlicedImage, String> implements SlicedImageDao {

    protected SlicedImageDaoImpl(){
        super(SlicedImage.class);
    }

    @Override
    public void saveMapBoxDataTile(SlicedImage slicedImage) {
    	slicedImage.setCreatedAt(new Date());
        save(slicedImage);
    }
}
