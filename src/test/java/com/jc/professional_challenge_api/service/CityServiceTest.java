package com.jc.professional_challenge_api.service;

import com.jc.professional_challenge_api.entities.City;
import com.jc.professional_challenge_api.repository.CityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityService cityService;


    @Test
    void findAll_shouldReturnAllCities() {

        City cit1 = new City();
        cit1.setId(1L);
        cit1.setCity("Bogota");

        City cit2 = new City();
        cit2.setId(2L);
        cit2.setCity("Cali");

        when(cityRepository.findAll()).thenReturn(List.of(cit1,cit2));

        List<City> result = cityService.findAll();

        assertEquals(2, result.size());
        assertEquals("Bogota", result.get(0).getCity());


    }
}