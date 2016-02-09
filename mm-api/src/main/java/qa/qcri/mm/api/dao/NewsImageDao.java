package qa.qcri.mm.api.dao;


import java.util.List;

import org.springframework.stereotype.Repository;

import qa.qcri.mm.api.dao.impl.AbstractDaoImpl;
import qa.qcri.mm.api.entity.NewsImage;

@Repository
public class NewsImageDao extends AbstractDaoImpl<NewsImage, String> {

    protected NewsImageDao(){
        super(NewsImage.class);
    }
    
    public void saveAll(List<NewsImage> newsImages) {
    	for (NewsImage newsImage : newsImages) {
    		save(newsImage);
    	}
    }
    
}