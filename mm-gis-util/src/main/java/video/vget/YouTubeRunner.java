package video.vget;

import java.io.BufferedReader;
import java.io.FileReader;

public class YouTubeRunner extends AppManagedDownload {
    public static void main(String[] args) {


        try{

            BufferedReader br = null;

            br = new BufferedReader(new FileReader("/Users/jlucas/Downloads/vget-master/video.csv"));

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                System.out.println(sCurrentLine);
                String url = sCurrentLine;
                try{
                    AppManagedDownload.main(new String[]{url, "/Users/jlucas/Downloads/uaviators"});
                }
                catch(Exception e){
                    System.out.println("Exception e : "  + e);
                }

            }


        }
        catch (Exception a){
            System.out.println("Exception : "  + a);
        }

    }
}
