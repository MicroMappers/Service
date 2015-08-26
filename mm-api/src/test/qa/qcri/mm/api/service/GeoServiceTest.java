package qa.qcri.mm.api.service;

import junit.framework.TestCase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by jlucas on 5/13/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml", "classpath:spring/hibernateContext.xml"})
public class GeoServiceTest extends TestCase {

    @Autowired
    GeoService geoService;

    @Autowired
    MicroMapsService microMapsService;

    @Test
    public void testGetAllCrisis() throws Exception {
        //JSONArray a = microMapsService.getAllCrisisJSONP();

        long appID = 260;
        String a = microMapsService.getGeoClickerByClientApp(appID);

        System.out.println(a);

       // long crisisID = 316;
      //  String b = microMapsService.getGeoClickerByCrisis(crisisID);

      //  System.out.println(b);

       // long clientAppID = 260;
       // geoService.getGeoClickerByClientApp(clientAppID);

       // microMapsService.generateTextClickerKML(clientAppID);
    }
}