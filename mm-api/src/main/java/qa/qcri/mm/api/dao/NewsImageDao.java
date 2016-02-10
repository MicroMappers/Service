package qa.qcri.mm.api.dao;

import java.util.List;

import qa.qcri.mm.api.entity.NewsImage;

public interface NewsImageDao extends AbstractDao<NewsImage, Long> {
	public void saveAll(List<NewsImage> newsImages);
}