package com.ssafy.moa2zi.sms.application;

import com.google.gson.Gson;
import com.ssafy.moa2zi.common.util.AESUtil;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.sms.domain.SMS;
import com.ssafy.moa2zi.sms.domain.SMSRedisRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class SMSService {

    @Value("${sms.smsId}") String smsId;
    @Value("${sms.apiKey}") String apiKey;
    @Value("${sms.calling_phone_number}") String CALLING_PHONE_NUMBER;
    @Value("${sms.join.sms_pre_content}") String SMS_PRE_CONTENT;
    @Value("${sms.join.sms_post_content}") String SMS_POST_CONTENT;

    private final MemberRepository memberRepository;
    private final SMSRedisRepository smsRedisRepository;
    private final AESUtil aesUtil;

    public static final String SMS_OAUTH_TOKEN_URL = "https://sms.gabia.com/oauth/token";
    public static final String SMS_SEND_URL = "https://sms.gabia.com/api/send/sms";
    private String accessToken;

    protected String getAccessToken() throws IOException {

        String authValue =
                Base64.getEncoder().encodeToString(String.format("%s:%s", smsId,
                        apiKey).getBytes(StandardCharsets.UTF_8)); // Authorization Header 에 입력할 값입니다.


        // 사용자 인증 API 를 호출합니다.
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("grant_type", "client_credentials")
                .build();

        Request request = new Request.Builder()
                .url(SMS_OAUTH_TOKEN_URL)
                .post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic " + authValue)
                .addHeader("cache-control", "no-cache")
                .build();

        // Response 를 key, value 로 확인하실 수 있습니다.
        Response response = client.newCall(request).execute();
        HashMap<String, String> result = new
                Gson().fromJson(Objects.requireNonNull(response.body()).string(), HashMap.class);

        return result.get("access_token");
    }

    public void sendSMS(String phoneNumber) throws Exception {

        if(!phoneNumber.startsWith("010")){
            throw new BadRequestException("유효한 번호가 아닙니다.");
        }

        String encryptedPhoneNumber = aesUtil.encrypt(phoneNumber);

        Member member = memberRepository.findByPhoneNumber(encryptedPhoneNumber).orElse(null);

        if(member != null){
            throw new DuplicateKeyException("이미 저장된 번호가 존재합니다.");
        }

        //레디스에 정보가 있는 지 확인하기
        SMS beforeSMS = smsRedisRepository.findById(encryptedPhoneNumber).orElse(null);

        if(beforeSMS != null && beforeSMS.getCount() >= 5){
            throw new BadRequestException("하루 보낼 수 있는 문자 제한량을 초과합니다.");
        }


        accessToken = getAccessToken();

        log.info(accessToken);

        String authValue =
                Base64.getEncoder().encodeToString(String.format("%s:%s", smsId,
                        accessToken).getBytes(StandardCharsets.UTF_8)); // Authorization Header 에 입력할 값입니다.

        // SMS 발송 API 를 호출합니다.
        OkHttpClient client = new OkHttpClient();

        int randomNum = (int)(Math.random() * 900000) + 100000;

        String message = SMS_PRE_CONTENT + randomNum+ SMS_POST_CONTENT;

        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("phone", phoneNumber)
                .addFormDataPart("callback", CALLING_PHONE_NUMBER) // 발신번호를 입력해 주세요.
                .addFormDataPart("message", message) // SMS 내용을 입력해 주세요.
                .addFormDataPart("refkey", String.valueOf(randomNum)) // 발송 결과 조회를 위한 임의의 랜덤 키 값을 입력해 주세요.
                .build();

        Request request = new Request.Builder()
                .url(SMS_SEND_URL)
                .post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic " + authValue)
                .addHeader("cache-control", "no-cache")
                .build();

        Response response = client.newCall(request).execute();

        // Response 를 key, value 로 확인하실 수 있습니다.

        log.info(response.toString());

        LocalDateTime limit = LocalDateTime.now().plusMinutes(3);

        if(beforeSMS == null){
            beforeSMS = SMS.builder()
                    .phoneNumber(encryptedPhoneNumber)
                    .count(1)
                    .random(randomNum)
                    .limit(limit)
                    .build();
        }
        else{
            beforeSMS.setCount(beforeSMS.getCount() + 1);
            beforeSMS.setRandom(randomNum);
            beforeSMS.setLimit(limit);
        }

        smsRedisRepository.save(beforeSMS);

    }

    @Transactional
    public void validateSMS(String phoneNumber, Integer randomNum) throws Exception {
        String encryptedPhoneNumber = aesUtil.encrypt(phoneNumber);
        SMS sms = smsRedisRepository.findById(encryptedPhoneNumber)
                .orElseThrow(() -> new NotFoundException("인증 요청이 존재하지 않습니다."));

        if(!Objects.equals(sms.getRandom(), randomNum)){
            throw new AccessDeniedException("인증 번호가 일치하지 않습니다.");
        }

        smsRedisRepository.delete(sms);
    }


}