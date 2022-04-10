package com.mycompany.loyalty;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.loyalty.user.User;
import com.mycompany.loyalty.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @WithMockUser
    @Test
    void whenGetOneUser_thenOK() throws Exception {

        Mockito.when(userRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.of(new User()));

        mockMvc.perform(MockMvcRequestBuilders.get("/loyalty/api/v1.0/users/a-user-id")
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser
    @Test
    void whenGetOneUser_thenNotFound() throws Exception {

        Mockito.when(userRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/loyalty/api/v1.0/users/a-user-id")
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser
    @Test
    void whenFindAllUsers_thenOK() throws Exception {

        Mockito.when(userRepository.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/loyalty/api/v1.0/users?pageNo=0&pageSize=10")
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser
    @Test
    void whenCreateUser_thenCreated() throws Exception {

        User user = new User();
        user.setLoginName("a-login-name");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(user);

        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/loyalty/api/v1.0/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @WithMockUser
    @Test
    void whenUpdateUser_thenOK() throws Exception {

        User user = new User();
        user.setId("a-user-id");
        user.setLoginName("a-login-name");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(user);

        Mockito.when(userRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.put("/loyalty/api/v1.0/users/a-user-id")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json)
                                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser
    @Test
    void whenDeleteUser_thenOK() throws Exception {

        User user = new User();
        user.setId("a-user-id");
        user.setLoginName("a-login-name");

        Mockito.when(userRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userRepository).deleteById(ArgumentMatchers.anyString());

        mockMvc.perform(MockMvcRequestBuilders.delete("/loyalty/api/v1.0/users/a-user-id")
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
