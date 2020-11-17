package main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.security.Role;
import main.security.User;
import main.security.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void accessDeniedTest() throws Exception {
        this.mockMvc
            .perform(get("/journal"))
            .andDo(print())
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void correctLoginTest() throws Exception {
        User user = new User();
        user.setUsername("testUser1");
        user.setPassword("$2y$10$2Eymfny/vXNvAFNIC.pzku98.KfOv02trT.Pn8Z9.tR.tWw0YFluC");
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user.setRoles(roles);
        userRepository.save(user);
        this.mockMvc
            .perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRepository.findByUsername("testUser1")))
                .accept(MediaType.APPLICATION_JSON));
        this.mockMvc
            .perform(formLogin().user("testUser1").password("123"))
            .andExpect(redirectedUrl("/journal"));
    }

    @Test
    public void badCredentials() throws Exception {
        User user = new User();
        user.setUsername("testUser2");
        user.setPassword("$2y$10$2Eymfny/vXNvAFNIC.pzku98.KfOv02trT.Pn8Z9.tR.tWw0YFluC");
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user.setRoles(roles);
        userRepository.save(user);
        this.mockMvc
            .perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRepository.findByUsername("testUser2")))
                .accept(MediaType.APPLICATION_JSON));
        this.mockMvc
            .perform(formLogin().user("testUser2").password("111"))
            .andExpect(redirectedUrl("/login"));
    }

    private static String asJsonString(final Object object) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
