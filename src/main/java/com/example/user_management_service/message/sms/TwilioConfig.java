package com.example.user_management_service.message.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
//@ConfigurationProperties(prefix = "twilio")
public class TwilioConfig {
//    private String accountSid;
//    private String authToken;
//    private String phoneNumber;
}
