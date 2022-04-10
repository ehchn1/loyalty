package com.mycompany.loyalty;

import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.loyalty.merchant.Merchant;
import com.mycompany.loyalty.merchant.MerchantRepository;
import com.mycompany.loyalty.user.User;
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
public class MerchantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MerchantRepository merchantRepository;

    @WithMockUser
    @Test
    void whenGetOneMerchant_thenOK() throws Exception {

        Mockito.when(merchantRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.of(new Merchant()));

        mockMvc.perform(MockMvcRequestBuilders.get("/loyalty/api/v1.0/merchants/a-merchant-id")
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser
    @Test
    void whenGetOneMerchant_thenNotFound() throws Exception {

        Mockito.when(merchantRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/loyalty/api/v1.0/merchants/a-merchant-id")
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser
    @Test
    void whenFindAllMerchants_thenOK() throws Exception {

        Mockito.when(merchantRepository.findAll(ArgumentMatchers.any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/loyalty/api/v1.0/merchants?pageNo=0&pageSize=10")
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser
    @Test
    void whenCreateMerchant_thenCreated() throws Exception {

        Merchant merchant = new Merchant();
        merchant.setName("a-merchant-name");

        User user = new User();
        user.setId("a-user-id");
        merchant.setUsers(Set.of(user));

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(merchant);

        Mockito.when(merchantRepository.save(ArgumentMatchers.any(Merchant.class))).thenReturn(merchant);

        mockMvc.perform(MockMvcRequestBuilders.post("/loyalty/api/v1.0/merchants")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @WithMockUser
    @Test
    void whenUpdateMerchant_thenOK() throws Exception {

        Merchant merchant = new Merchant();
        merchant.setName("a-merchant-name");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(merchant);

        Mockito.when(merchantRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.of(merchant));
        Mockito.when(merchantRepository.save(ArgumentMatchers.any(Merchant.class))).thenReturn(merchant);

        mockMvc.perform(MockMvcRequestBuilders.put("/loyalty/api/v1.0/merchants/a-merchant-id")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json)
                                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser
    @Test
    void whenDeleteMerchant_thenOK() throws Exception {

        Merchant merchant = new Merchant();
        merchant.setId("a-merchant-id");
        merchant.setName("a-merchant-name");

        Mockito.when(merchantRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.of(merchant));
        Mockito.doNothing().when(merchantRepository).deleteById(ArgumentMatchers.anyString());

        mockMvc.perform(MockMvcRequestBuilders.delete("/loyalty/api/v1.0/merchants/a-merchant-id")
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
