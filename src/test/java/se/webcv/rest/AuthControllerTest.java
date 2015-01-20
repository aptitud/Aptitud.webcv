package se.webcv.rest;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer;
import org.springframework.web.context.WebApplicationContext;
import se.webcv.UnitTest;
import se.webcv.auth.UnitTokenVerifier;
import se.webcv.config.UnitTestConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTest extends UnitTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired
    UnitTokenVerifier unitTokenVerifier;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @After
    public void tearDown() throws Exception {
        unitTokenVerifier.authorized();
    }

    @Test
    public void invalidTokenShouldReturnForbidden() throws Exception {
        unitTokenVerifier.notAuthorized();
        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accessToken\":\"1234\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void validTokenShouldReturnOkAndUserData() throws Exception {
        unitTokenVerifier.authorized();
        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accessToken\":\"1234\"}"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"token\":\"token\", \"displayName\":\"Unit test\"}"));
    }

    @Test
    public void logoutShouldInvalidateToken() throws Exception {
        validTokenShouldReturnOkAndUserData();
        // validation result cached, this should be ok
        unitTokenVerifier.notAuthorized();
        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accessToken\":\"1234\"}"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"token\":\"token\", \"displayName\":\"Unit test\"}"));

        mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accessToken\":\"1234\"}"))
                .andExpect(status().isOk());

        // validation result removed from cache, this shoudl fail
        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accessToken\":\"1234\"}"))
                .andExpect(status().isUnauthorized());
    }

}