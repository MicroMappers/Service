package qa.qcri.mm.api.dao;

import qa.qcri.mm.api.entity.SourceImage;

public interface SourceImageDao extends AbstractDao<SourceImage, Long> {

	public Long persist(SourceImage sourceImage);
}
