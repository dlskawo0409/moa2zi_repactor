package com.ssafy.moa2zi.common.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

	@PostConstruct
	public void init() throws IOException {
		InputStream serviceAccount =
				getClass().getClassLoader().getResourceAsStream("moa2zi-firebase-adminsdk-fbsvc-c7b19b7f0a.json");

		if (serviceAccount == null) {
			throw new IOException("Firebase service account file not found in classpath");
		}

		FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();

		if (FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options);
		}
	}
}
