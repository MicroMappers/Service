package qa.qcri.mm.api.service;


public interface NewsImageService {
	
	void startFetchingDataFromGdelt(Long clientAppID);
	void stopFetchingDataFromGdelt(Long clientAppID);
	boolean getGdeltPullStatus();
}
