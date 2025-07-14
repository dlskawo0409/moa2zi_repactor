package com.ssafy.moa2zi.sms.domain;

import com.ssafy.moa2zi.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface SMSRedisRepository extends CrudRepository<SMS, String> {
}
