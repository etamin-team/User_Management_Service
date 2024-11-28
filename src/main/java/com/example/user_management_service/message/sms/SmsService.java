package com.example.user_management_service.message.sms;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SmsService {

    private static final String SERVER_URL = "https://smsapp.uz/new/services/send.php";
    private static final String API_KEY = "146b92a7f92291c5b4957e9a121e6bd64530b9e1";

    public static void sendSMS(String number, String message) {
        RestTemplate restTemplate = new RestTemplate();
        String smsMessage = "Your confirmation code is: " + message + ". Please don't share it with anyone!";

        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        formParams.add("number", number);
        formParams.add("message", smsMessage);
        formParams.add("key", API_KEY);
        formParams.add("devices", "0");
        formParams.add("type", "sms");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formParams, headers);

        ResponseEntity<String> response = restTemplate.exchange(SERVER_URL, HttpMethod.POST, entity, String.class);

        System.out.println(response.getBody());
    }

}

