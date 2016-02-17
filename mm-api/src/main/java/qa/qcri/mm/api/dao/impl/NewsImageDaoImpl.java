package qa.qcri.mm.api.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import qa.qcri.mm.api.dao.NewsImageDao;
import qa.qcri.mm.api.entity.NewsImage;

@Repository
public class NewsImageDaoImpl extends AbstractDaoImpl<NewsImage, Long>
		implements NewsImageDao {

	protected NewsImageDaoImpl() {
		super(NewsImage.class);
	}

	@Override
	public void saveAll(List<NewsImage> newsImages) {
		for (NewsImage newsImage : newsImages) {
			Long id = null;
			try {
				id = save(newsImage);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("News Image Id: "+ id);
		}
	}
}
