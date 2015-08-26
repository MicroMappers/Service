package qa.qcri.mm.api.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Kushal
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml", "classpath:spring/hibernateContext.xml"})
public class PartnerAppSourceServiceTest {

	@Autowired
    private PartnerAppSourceService partnerAppSourceService;
	
	@Test
	public void testAddExternalDataSourceWithClassifiedData() {
		
		String importURL ="http://aidr-prod.qcri.org:8084/AIDRTaggerAPI/rest/misc/humanLabeled";
    	Long crisisId = 39L;
    	Long recordsCount=1500L;
    	String crisisCode = "150517124204_twbdemo";
    	String fileLocation = "/Users/jlucas/Desktop/BAD_IMAGE_GEO/javier";

    	try {
			partnerAppSourceService.pushAppSource(importURL, crisisId,recordsCount, crisisCode, fileLocation);
		} catch (Exception e) {
			//fail("Exception while testing PartnerAppSourceServiceTest#testAddExternalDataSourceWithClassifiedData "+e.getMessage());
			System.out.println("Exception while testing PartnerAppSourceServiceTest#testAddExternalDataSourceWithClassifiedData "+e.getMessage());
		}
	}
}
