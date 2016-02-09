package qa.qcri.mm.api.dao;

import qa.qcri.mm.api.entity.ImageConfig;

public interface ImageConfigDao  extends AbstractDao<ImageConfig, Long>  {

	public ImageConfig getByClientAppId(Long clientAppId);

}
