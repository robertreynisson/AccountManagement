package com.robertreynisson.accountmanager;

import com.robertreynisson.accountmanager.controllers.AccountController;
import com.robertreynisson.accountmanager.controllers.domain.Role;
import com.robertreynisson.accountmanager.controllers.domain.UserAccountAccountCreate;
import com.robertreynisson.accountmanager.service.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
public class AuthorizationTests {

    MockMvc mvc;
    UserAccountAccountCreate userAccountCreate;

    @Autowired
    public WebApplicationContext context;

    @MockBean
    private AccountService accountService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        userAccountCreate = new UserAccountAccountCreate();
        userAccountCreate.setUserName("RobertReynisson");
        userAccountCreate.setFirstName("Robert");
        userAccountCreate.setLastName("Reynisson");
        userAccountCreate.setEmail("robert@robert.com");
        userAccountCreate.setPhone("+234234234234");
        userAccountCreate.setRole(Role.ADMIN);
        userAccountCreate.setPassword("Testing12345");

    }

    @Test
    public void assertUnauthenticatedUsersBlocked() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/account/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        mvc.perform(MockMvcRequestBuilders.get("/account/").param("id", "1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());


    }

    @Test
    @WithMockUser(username = "test", roles = {"XXX"})
    public void assertAuthorizationBlocks() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/account/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test", roles = {"USER"})
    public void assertUserRoleCanOnlyRead() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/account/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.post("/account/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
