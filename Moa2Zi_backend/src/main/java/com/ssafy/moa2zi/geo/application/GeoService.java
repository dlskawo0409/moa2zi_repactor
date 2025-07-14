package com.ssafy.moa2zi.geo.application;

import com.ssafy.moa2zi.geo.domain.*;
import com.ssafy.moa2zi.geo.dto.DongResponse;
import com.ssafy.moa2zi.geo.dto.GugunResponse;
import com.ssafy.moa2zi.geo.dto.SidoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeoService {

    private final SidoRepository sidoRepository;
    private final GugunRepository gugunRepository;
    private final DongRepository dongRepository;

    public List<SidoResponse> getSidos() {
        List<Sido> sidoList = sidoRepository.findAll();
        return sidoList.stream().map(SidoResponse::of).toList();
    }

    public List<GugunResponse> getGugunsBySidoCode(int sidoCode) {
        List<Gugun> gugunList = gugunRepository.findBySidoCode(sidoCode);
        return gugunList.stream().map(GugunResponse::of).toList();
    }

    public List<DongResponse> getDongsByGugunCode(int gugunCode) {
        List<Dong> dongList = dongRepository.findByGugunCode(gugunCode);
        return dongList.stream().map(DongResponse::of).toList();
    }

}
