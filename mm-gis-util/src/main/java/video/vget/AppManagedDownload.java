package video.vget;

import video.vget.info.VGetParser;
import video.vget.info.VideoFileInfo;
import video.vget.info.VideoInfo;
import video.vget.vhs.VimeoInfo;
import video.vget.vhs.YouTubeInfo;
import com.github.axet.wget.info.DownloadInfo;
import com.github.axet.wget.info.DownloadInfo.Part;
import com.github.axet.wget.info.DownloadInfo.Part.States;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AppManagedDownload {

    VideoInfo videoinfo;
    long last;

    public void run(String url, File path) {
        try {
            AtomicBoolean stop = new AtomicBoolean(false);
            Runnable notify = new Runnable() {
                @Override
                public void run() {
                    VideoInfo videoinfo = AppManagedDownload.this.videoinfo;
                    List<VideoFileInfo> dinfoList = videoinfo.getInfo();

                    // notify app or save download state
                    // you can extract information from DownloadInfo info;
                    switch (videoinfo.getState()) {
                    case EXTRACTING:
                    case EXTRACTING_DONE:
                    case DONE:
                        if (videoinfo instanceof YouTubeInfo) {
                            YouTubeInfo i = (YouTubeInfo) videoinfo;
                            System.out.println(videoinfo.getState() + " " + i.getVideoQuality());
                        } else if (videoinfo instanceof VimeoInfo) {
                            VimeoInfo i = (VimeoInfo) videoinfo;
                            System.out.println(videoinfo.getState() + " " + i.getVideoQuality());
                        } else {
                            System.out.println("downloading unknown quality");
                        }
                        for (VideoFileInfo d : videoinfo.getInfo()) {
                            System.out.println(String.format("file:%d - %s", dinfoList.indexOf(d), d.targetFile));
                        }
                        break;
                    case ERROR:
                        System.out.println(videoinfo.getState() + " " + videoinfo.getDelay());
                        
                        if (dinfoList != null) {
                            for (DownloadInfo dinfo : dinfoList) {
                                System.out.println("file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.getException()
                                        + " delay:" + dinfo.getDelay());
                            }
                        }
                        break;
                    case RETRYING:
                        System.out.println(videoinfo.getState() + " " + videoinfo.getDelay());

                        if (dinfoList != null) {
                            for (DownloadInfo dinfo : dinfoList) {
                                System.out.println("file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.getState() + " "
                                        + dinfo.getException() + " delay:" + dinfo.getDelay());
                            }
                        }
                        break;
                    case DOWNLOADING:
                        long now = System.currentTimeMillis();
                        if (now - 1000 > last) {
                            last = now;

                            String parts = "";

                            for (DownloadInfo dinfo : dinfoList) {
                                List<Part> pp = dinfo.getParts();
                                if (pp != null) {
                                    // multipart download
                                    for (Part p : pp) {
                                        if (p.getState().equals(States.DOWNLOADING)) {
                                            parts += String.format("part#%d(%.2f) ", p.getNumber(),
                                                    p.getCount() / (float) p.getLength());
                                        }
                                    }
                                }
                                System.out.println(String.format("file:%d - %s %.2f %s", dinfoList.indexOf(dinfo),
                                        videoinfo.getState(), dinfo.getCount() / (float) dinfo.getLength(), parts));
                            }
                        }
                        break;
                    default:
                        break;
                    }
                }
            };

            URL web = new URL(url);

            // [OPTIONAL] limit maximum quality, or do not call this function if
            // you wish maximum quality available.
            //
            // if youtube does not have video with requested quality, program
            // will raise en exception.
            VGetParser user = null;

            // create proper html parser depends on url
            user = VGet.parser(web);

            // download limited video quality from youtube
            // user = new YouTubeQParser(YoutubeQuality.p480);

            // download mp4 format only, fail if non exist
            // user = new YouTubeMPGParser();

            // create proper videoinfo to keep specific video information
            videoinfo = user.info(web);

            VGet v = new VGet(videoinfo, path);

            // [OPTIONAL] call v.extract() only if you d like to get video title
            // or download url link before start download. or just skip it.
            v.extract(user, stop, notify);

            System.out.println("Title: " + videoinfo.getTitle());
            for (DownloadInfo d : videoinfo.getInfo()) {
                System.out.println("Download URL: " + d.getSource());
            }

            v.download(user, stop, notify);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        AppManagedDownload e = new AppManagedDownload();
        // ex: http://www.youtube.com/watch?v=Nj6PFaDmp6c
        String url = args[0];
        // ex: /Users/axet/Downloads/
        String path = args[1];
        e.run(url, new File(path));
    }
}