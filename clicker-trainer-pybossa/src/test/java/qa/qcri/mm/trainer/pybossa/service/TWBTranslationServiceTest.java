package qa.qcri.mm.trainer.pybossa.service;

import org.json.simple.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import qa.qcri.mm.trainer.pybossa.entity.TaskTranslation;
import qa.qcri.mm.trainer.pybossa.format.impl.TranslationRequestModel;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 2/27/15
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml", "classpath:spring/hibernateContext.xml"})

public class TWBTranslationServiceTest {
    @Autowired
    TranslationService translationService;

    @Autowired
    ClientAppService clientAppService;

    @Autowired
    private MicroMapperWorker pybossaWorker;

    private static final long TEST_CLIENT_ID = 3;
    private static final long NEW_CLIENT_APP_ID = 1740;
    private static final long TEST_TWB_PROJECT_ID = 7781;
    private static final String LONG_CODE = "This_is_a_long_answer_code_loooooooong";

   // @Test
    public void testPybossaWorker() throws Exception {
       // pybossaWorker.processTaskRunImport();
    }

    final private static String BASE_URL = "https://twb.translationcenter.org/api/v1";
    final private static String API_KEY = "niptz15xao0w";

   // @Test
    public void testPullAllTranslationResponses() throws Exception {

        translationService.pullAllTranslationResponses(new Long(NEW_CLIENT_APP_ID), TEST_TWB_PROJECT_ID);
        assert(true);

    }


    @Test
    public void testPushAllTranslations() {
       // ClientApp clientApp = clientAppService.getAllClientAppByClientIDAndStatus(new Long(TEST_CLIENT_ID), 1).get(0);
        //ClientApp clientApp = clientAppService.getAllClientAppByClientID(new Long(TEST_CLIENT_ID)).get(0);
       // Long tcProjectId = new Long(TEST_TWB_PROJECT_ID);
/*        if (clientApp.getTcProjectId() != null) {
            tcProjectId = clientApp.getTcProjectId();
        }
*/
        //List translations = generateTestTranslationTasks(NEW_CLIENT_APP_ID, true, 6);
        //Long clientAppId = new Long(NEW_CLIENT_APP_ID);
        //List checkTranslations = translationService.findAllTranslationsByClientAppIdAndStatus(clientAppId, TaskTranslation.STATUS_NEW, 100);

       // assert(checkTranslations.size() > 0);
       // long clientAppId = 1740;
       // Map result = translationService.pushAllTranslations(clientAppId, new Long(TEST_TWB_PROJECT_ID), 172800000, 1000);
   //    // assertNotNull(result);

//        List<TaskTranslation> inProgressTranslations = translationService.findAllTranslationsByClientAppIdAndStatus(clientAppId, TaskTranslation.STATUS_IN_PROGRESS, 1);
        /**
        assert(inProgressTranslations.size() > (checkTranslations.size()-5));
        Iterator<TaskTranslation> itr = inProgressTranslations.iterator();
        while (itr.hasNext()) {
            TaskTranslation translation = itr.next();
            assert(translation.getStatus().equals(TaskTranslation.STATUS_IN_PROGRESS));
        }

        Iterator<TaskTranslation> itr2 = checkTranslations.iterator();
        while (itr2.hasNext()) {
            TaskTranslation translation = itr2.next();
            //translationService.delete(translation);
        }
        **/
    }


  //  @Test
    public void testPushTranslationRequest() {
        TranslationRequestModel model = new TranslationRequestModel();
        model.setContactEmail("test@test.com");
        model.setTitle("Request from Unit Test");
        model.setSourceLanguage("eng");
        String[] targets = {"und","esl"};
        model.setTargetLanguages(targets);
        model.setSourceWordCount(100); //random test
        model.setInstructions("Unit test instructions");
        model.setDeadline(new Date(System.currentTimeMillis()+3600000l));
        model.setUrgency("high");
        model.setProjectId(TEST_TWB_PROJECT_ID);// hard coded for now

        model.setCallbackURL("https://www.example.com/my-callback-url");
        List translations = generateTestTranslationTasks(NEW_CLIENT_APP_ID, false, 2);
        model.setTranslationList(translations);

        Map result = translationService.pushTranslationRequest(model);
        assertNotNull(result);
    }


 //   @Test
    public void testPushDocumentForRequest() {
        TranslationRequestModel model = new TranslationRequestModel();
        model.setContactEmail("test@test.com");
        model.setTitle("Request from Unit Test");
        model.setSourceLanguage("eng");
        String[] targets = {"fra","esl"};
        model.setTargetLanguages(targets);
        long[] documentIds = {125549};
        model.setSourceDocumentIds(documentIds);
        model.setSourceWordCount(100); //random test
        model.setInstructions("Unit test instructions");
        model.setDeadline(new Date(System.currentTimeMillis()+3600000l));
        model.setUrgency("high");
        model.setProjectId(TEST_TWB_PROJECT_ID);// hard coded for now

        model.setCallbackURL("https://www.example.com/my-callback-url");

        List translations = generateTestTranslationTasks(NEW_CLIENT_APP_ID, false, 2);
        model.setTranslationList(translations);

        Map result = translationService.pushDocumentForRequest(model);
        assertNotNull(result.get("document_id"));
    }


    private List generateTestTranslationTasks(long clientAppId, boolean persist, int number) {
        List<TaskTranslation> list = new ArrayList<TaskTranslation>();
        int loops = number/2;

        long id = 10;

        if (loops == 0) {loops = 1;}

        for (int i=0; i<loops; i++) {
            TaskTranslation translation = new TaskTranslation(id++, clientAppId, "63636", null, null, null, null, id,"\n" + "क्षति शहर धेरै छ", TaskTranslation.STATUS_NEW);
            if (persist) {
                translationService.createTranslation(translation);
            }
            TaskTranslation translation2 = new TaskTranslation(id++, clientAppId, "63636", "Fred Jones", "22.22", "33.33", "http://google.com", id, "被害のダウン,タウンの\"多くがあります", TaskTranslation.STATUS_NEW);
            if (persist) {
                translationService.createTranslation(translation2);
            }

            list.add(translation);
            list.add(translation2);
        }
        return list;

    }

    //@Test
    public void testCreateWithCharset() throws Exception {

        TaskTranslation translation3 = new TaskTranslation(100l, 1000l, "63636", null, null, null, null, 100l, "被害のダウンタウンの多くがあります", TaskTranslation.STATUS_NEW);
        translationService.createTranslation(translation3);
        TaskTranslation retrieve = translationService.findById(translation3.getTranslationId());
        assertNotNull(retrieve);
        assert(translation3.getOriginalText().equals(retrieve.getOriginalText()));
        assertNotNull(retrieve.getAuthor());
        assertNotNull(retrieve.getOriginalText());

        String newVal = "TEST";
        retrieve.setStatus(newVal);
        retrieve.setAnswerCode(LONG_CODE);
        translationService.updateTranslation(retrieve);

        TaskTranslation retrieve2 = translationService.findById(translation3.getTranslationId());
        assert(retrieve2.getOriginalText().equals(retrieve.getOriginalText()));

    }

   // @Test
    public void testCreateAndUpdateTranslation() throws Exception {
        int initialSize = translationService.findAllTranslations().size();
        TaskTranslation translation2 = new TaskTranslation(100l, 1000l, "63636", null, null, null, null, 100l, "Je m'appelle Jacques", TaskTranslation.STATUS_NEW);
        translationService.createTranslation(translation2);
        assertNotNull(translation2.getAuthor());
        assertNotNull(translation2.getUrl());
        translation2 = translationService.findById(translation2.getTranslationId());

    	String newVal = "TEST";
        translation2.setStatus(newVal);
        translation2.setAnswerCode(LONG_CODE);
    	translationService.updateTranslation(translation2);
        translation2 = translationService.findById(translation2.getTranslationId());
    	// we would really need to flush and clear the hibernate session for this next validation
    	assertEquals(newVal, translation2.getStatus());
    	translationService.delete(translation2);
    	assertEquals(initialSize, translationService.findAllTranslations().size());
    }

   // @Test
    public void testFindByTaskId() throws Exception {
        TaskTranslation translation = new TaskTranslation();
        translation.setTaskId(new Long(898));
        translationService.createTranslation(translation);

        TaskTranslation found = translationService.findByTaskId(new Long(898));
        assertNotNull(found);
        translationService.delete(found);


    }

 //   @Test
    public void testFindByClientAppIdAndStatus () throws Exception {
        //ClientApp clientApp = clientAppService.getAllClientAppByClientID(new Long(TEST_CLIENT_ID)).get(0);
        String testStatus = "TESTING324";
        List<TaskTranslation> translations = generateTestTranslationTasks(NEW_CLIENT_APP_ID, true, 4);
        TaskTranslation taskTranslation = translations.get(0);
//        taskTranslation.setStatus(testStatus);
        translationService.updateTranslation(taskTranslation);

        List testList = translationService.findAllTranslationsByClientAppIdAndStatus(new Long(NEW_CLIENT_APP_ID), testStatus, 1000);
        assert(testList.size() == 1);

        long newClientAppId = 976;
        taskTranslation = translations.get(1);
        taskTranslation.setClientAppId(newClientAppId);
        translationService.updateTranslation(taskTranslation);


        testList = translationService.findAllTranslationsByClientAppIdAndStatus(new Long(newClientAppId), TaskTranslation.STATUS_NEW, 1000);
        assert(testList.size() == 1);
        Iterator<TaskTranslation> itr = translations.iterator();
        while (itr.hasNext()) {
            TaskTranslation translation = itr.next();
            //translationService.delete(translation);
        }


    }


   // @Test
    public void testCharset() throws Exception {
        JSONObject info = new JSONObject();
        info.put("tweet", "à¤­à¥à¤¯à¤¾à¤•à¥à¤¤à¥‡ à¤°à¥‹à¤—");
        String tweet = (String)info.get("tweet");// maintain encoding - new String(info.getString("tweet").getBytes("ISO-8859-1"), "UTF-8");
        String encodedTweet = new String (tweet.getBytes("ISO-8859-1"), "UTF-8");
        assert encodedTweet != null;
    }

  //  @Test
    public void testSendEncodedDocument() throws Exception {
        String filename = "TWB_Source_"+System.currentTimeMillis()+".csv";

        //decide whether its better to send file or content
        String content = "被害のダウ";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        final String url=BASE_URL+"/documents";
        HttpHeaders requestHeaders=new HttpHeaders();
        requestHeaders.add("X-Proz-API-Key", API_KEY);
        requestHeaders.setContentType(new MediaType("multipart","form-data"));
        RestTemplate restTemplate=new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("document", bytes);
        map.add("name", "translation_source.csv");

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new    HttpEntity<LinkedMultiValueMap<String, Object>>(
                map, requestHeaders);

        ResponseEntity<Map> result = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        Map resultMap = result.getBody();
        String download_Link = (String)resultMap.get("download_link");
        String returnedDocumentContent = getTranslationDocumentContent(download_Link) ;
        assert (returnedDocumentContent.equals(content));

    }

    public String getTranslationDocumentContent(String download_link) throws Exception {
        final String url=download_link;
        HttpHeaders requestHeaders=new HttpHeaders();
        requestHeaders.add("X-Proz-API-Key", API_KEY);
        requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
        RestTemplate restTemplate=new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<String>(requestHeaders), String.class);
        return response.getBody();
    }



}
