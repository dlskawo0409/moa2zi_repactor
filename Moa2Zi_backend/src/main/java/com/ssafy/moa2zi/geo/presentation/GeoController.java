package com.ssafy.moa2zi.geo.presentation;

import com.ssafy.moa2zi.geo.application.GeoService;
import com.ssafy.moa2zi.geo.dto.DongResponse;
import com.ssafy.moa2zi.geo.dto.GugunResponse;
import com.ssafy.moa2zi.geo.dto.SidoResponse;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/geo")
public class GeoController {

    private final GeoService geoService;

    @GetMapping("/sido")
    public ResponseEntity<List<SidoResponse>> getSidos() {
        List<SidoResponse> result = geoService.getSidos();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/gugun")
    public ResponseEntity<List<GugunResponse>> getGuguns(
            @RequestParam(name = "sidoCode") int sidoCode
    ) {
        List<GugunResponse> result = geoService.getGugunsBySidoCode(sidoCode);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/dong")
    public ResponseEntity<List<DongResponse>> getDongs(
            @RequestParam(name = "gugunCode") int gugunCode
    ) {
        List<DongResponse> result = geoService.getDongsByGugunCode(gugunCode);
        return ResponseEntity.ok(result);
    }

}
