package com.vanillasky.imageuploader.service;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class OpenseaService {
    private final RestTemplate restTemplate;
    private final String baseUrl = "http://opensea.com/api/v1/";
    public OpenseaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAssets(String ownerAddress){
//        String url = UriComponentsBuilder.fromUriString(baseUrl)
//                .queryParam("", "")
//                .build()
//                .toUriString();
        return "hello in getAssets";
    }
}

