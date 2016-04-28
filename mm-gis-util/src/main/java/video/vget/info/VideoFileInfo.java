package video.vget.info;

import com.github.axet.wget.info.DownloadInfo;
import com.github.axet.wget.info.ProxyInfo;

import java.io.File;
import java.net.URL;

public class VideoFileInfo extends DownloadInfo {

    public File targetFile;

    public VideoFileInfo(URL source) {
        super(source);
    }

    public VideoFileInfo(URL source, ProxyInfo p) {
        super(source, p);
    }

}
