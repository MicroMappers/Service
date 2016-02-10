package qa.qcri.mm.api.service;


public interface NewsImageService {
	
	public void pull(Long clientAppID);
	public boolean isRunning();
	public void setRunning(boolean running);
}
