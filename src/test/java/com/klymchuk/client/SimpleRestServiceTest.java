package com.klymchuk.client;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.klymchuk.service.SimpleRestService;

//Simple Rest Service Functional Test
@ContextConfiguration(locations = {"classpath:/applicationContext-test.xml"})
public class SimpleRestServiceTest extends AbstractJUnit4SpringContextTests{

	@Autowired
    private SimpleRestService simpleRestService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;
    
    private static String CURRENT_URL = "http://localhost:8080/address";

    @Before
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }
    
    @Test
    public void testGetMessage() {
    	
        mockServer.expect(requestTo(CURRENT_URL)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("OK RESPONCE", MediaType.TEXT_PLAIN));

        String result = simpleRestService.getMessage();
        System.out.println(result);

        mockServer.verify();
        assertThat(result, allOf(containsString("SUCCESS"), containsString("OK RESPONCE")));
    }

    @Test
    public void testGetMessage_500() {
        mockServer.expect(requestTo(CURRENT_URL)).andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        String result = simpleRestService.getMessage();
        System.out.println("500 "+result);

        mockServer.verify();
        assertThat(result, allOf(containsString("FAILED"), containsString("500")));
    }

    @Test
    public void testGetMessage_404() {
        mockServer.expect(requestTo(CURRENT_URL)).andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        String result = simpleRestService.getMessage();
        System.out.println("404 "+result);

        mockServer.verify();
        assertThat(result, allOf(containsString("FAILED"), containsString("404")));
    }
}
