package qa.qcri.mm.api.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 3/28/15
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml", "classpath:spring/hibernateContext.xml"})

public class ClientAppSourceServiceTest {

    @Autowired
    ClientAppSourceService clientAppSourceService;

    @Test
    public void testHandleMapBoxGistDataSource() throws Exception {
       // String url = "https://gist.githubusercontent.com/smit1678/508e868cd337f29c6e64/raw/f526aa8e7a96ce1729650f549412bf87e5e484ac/tiles.json";
       // clientAppSourceService.handleMapBoxGistDataSource(url);
    }
}
