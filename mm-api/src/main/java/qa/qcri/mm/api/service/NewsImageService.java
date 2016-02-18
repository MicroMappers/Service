package qa.qcri.mm.api.service;


public interface NewsImageService {
	
	public boolean isRunning();
	public void setRunning(boolean running);
	void startFetchingDataFromGdelt(Long clientAppID);
	void stopFetchingDataFromGdelt(Long clientAppID);
}
