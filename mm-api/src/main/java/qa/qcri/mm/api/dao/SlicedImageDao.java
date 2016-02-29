package qa.qcri.mm.api.dao;

import qa.qcri.mm.api.entity.SlicedImage;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 3/28/15
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SlicedImageDao  extends AbstractDao<SlicedImage, String>  {

    void saveMapBoxDataTile(SlicedImage imageMetaData);

}
