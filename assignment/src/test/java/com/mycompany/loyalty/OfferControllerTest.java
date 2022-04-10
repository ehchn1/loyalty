package com.mycompany.loyalty;

import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.loyalty.offer.Offer;
import com.mycompany.loyalty.offer.OfferRepository;
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
public class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OfferRepository offerRepository;

    @WithMockUser
    @Test
    void whenGetOneOffer_thenOK() throws Exception {

        Mockito.when(offerRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.of(new Offer()));

        mockMvc.perform(MockMvcRequestBuilders.get("/loyalty/api/v1.0/offers/an-offer-id")
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser
    @Test
    void whenGetOneOffer_thenNotFound() throws Exception {

        Mockito.when(offerRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/loyalty/api/v1.0/offers/an-offer-id")
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser
    @Test
    void whenFindOngoingOffers_thenOK() throws Exception {

        Mockito.when(offerRepository.findAllByDate(ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/loyalty/api/v1.0/offers?pageNo=0&pageSize=10&date=2022-01-01")
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser
    @Test
    void whenFindPendingOffers_thenOK() throws Exception {

        Mockito.when(offerRepository.findByEndDateAfter(ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/loyalty/api/v1.0/offers?pageNo=0&pageSize=10&date=2022-01-01&mode=pending")
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser
    @Test
    void whenFindPassOffers_thenOK() throws Exception {

        Mockito.when(offerRepository.findByStartDateBefore(ArgumentMatchers.any(LocalDate.class), ArgumentMatchers.any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/loyalty/api/v1.0/offers?pageNo=0&pageSize=10&date=2022-01-01&mode=pass")
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser
    @Test
    void whenCreateOffer_thenCreated() throws Exception {

        Offer offer = new Offer();
        offer.setName("an-offer-name");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(offer);

        Mockito.when(offerRepository.save(ArgumentMatchers.any(Offer.class))).thenReturn(offer);

        mockMvc.perform(MockMvcRequestBuilders.post("/loyalty/api/v1.0/offers")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)
                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @WithMockUser
    @Test
    void whenUpdateOffer_thenOK() throws Exception {

        Offer offer = new Offer();
        offer.setName("an-offer-name");
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(offer);

        Mockito.when(offerRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.of(offer));
        Mockito.when(offerRepository.save(ArgumentMatchers.any(Offer.class))).thenReturn(offer);

        mockMvc.perform(MockMvcRequestBuilders.put("/loyalty/api/v1.0/offers/an-offer-id")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(json)
                                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser
    @Test
    void whenDeleteOffer_thenOK() throws Exception {

        Offer offer = new Offer();
        offer.setId("an-offer-id");
        offer.setName("an-offer-name");

        Mockito.when(offerRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.of(offer));
        Mockito.doNothing().when(offerRepository).deleteById(ArgumentMatchers.anyString());

        mockMvc.perform(MockMvcRequestBuilders.delete("/loyalty/api/v1.0/offers/an-offer-id")
                                .contextPath("/loyalty"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
