package qa.qcri.mm.api.service;

import qa.qcri.mm.api.entity.NewsImage;


public interface NewsImageService {
	
	void startFetchingDataFromGdelt(Long clientAppID);
	void stopFetchingDataFromGdelt(Long clientAppID);
	boolean getGdeltPullStatus();
	Long save(NewsImage newsImage);
}
