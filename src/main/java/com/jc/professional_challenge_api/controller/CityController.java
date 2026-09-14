package com.jc.professional_challenge_api.controller;

import com.jc.professional_challenge_api.entities.City;
import com.jc.professional_challenge_api.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("cities")
public class CityController {

    private CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    //ENDPOINTS
    @GetMapping
    public List<City> getAll(){
        return cityService.findAll();
    }

}
