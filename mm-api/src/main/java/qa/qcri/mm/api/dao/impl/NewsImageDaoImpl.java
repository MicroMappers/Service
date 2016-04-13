package qa.qcri.mm.api.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.api.dao.NewsImageDao;
import qa.qcri.mm.api.entity.NewsImage;

@Repository
public class NewsImageDaoImpl extends AbstractDaoImpl<NewsImage, Long>
		implements NewsImageDao {

	protected static Logger logger = Logger.getLogger(NewsImageDao.class);
	
	protected NewsImageDaoImpl() {
		super(NewsImage.class);
	}

	@Override
	@Transactional(readOnly = false)
	public void saveAll(List<NewsImage> newsImages) {
		for (NewsImage newsImage : newsImages) {
			try {
				if(StringUtils.isEmpty(newsImage.getCreated())){
					newsImage.setCreated(new Date().toString());
				}
				save(newsImage);
			} catch (Exception e) {
				logger.error("Error in saving gdelt data", e);
			}
		}
	}
}
