
package com.ssafy.moa2zi.sms.presentation;

import com.ssafy.moa2zi.sms.application.SMSService;
import com.ssafy.moa2zi.sms.dto.request.SMSSendRequest;

import com.ssafy.moa2zi.sms.dto.request.SMSValidateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
public class SMSController {

    private final SMSService smsService;

    @PostMapping
    public ResponseEntity<Void> sendSMS(@Valid @RequestBody SMSSendRequest smsSendRequest) throws Exception {
        smsService.sendSMS(smsSendRequest.phoneNumber());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<Void> validateSMS(@Valid @RequestBody SMSValidateRequest smsValidateRequest) throws Exception {
        smsService.validateSMS(smsValidateRequest.phoneNumber(), smsValidateRequest.validateNum());
        return ResponseEntity.ok().build();
    }


}