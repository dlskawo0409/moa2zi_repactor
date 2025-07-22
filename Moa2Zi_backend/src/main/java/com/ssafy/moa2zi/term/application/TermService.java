package com.ssafy.moa2zi.term.application;

import com.ssafy.moa2zi.term.domain.Term;
import com.ssafy.moa2zi.term.domain.TermDetail;
import com.ssafy.moa2zi.term.domain.TermDetailRepository;
import com.ssafy.moa2zi.term.domain.TermRepository;
import com.ssafy.moa2zi.term.dto.response.TermDetailGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TermService {

    private final TermRepository termRepository;
    private final TermDetailRepository termDetailRepository;


    public List<Term> getTerms(){
        return termRepository.findAll();
    }

    public TermDetailGetResponse getTermDetails(Long termId){
        Term term = termRepository.findById(termId)
                .orElseThrow(()-> new NotFoundException("존재하지않는 약관입니다."));

        List<TermDetail> termDetailList = termDetailRepository.findByTermId(termId);

        return TermDetailGetResponse.builder()
                .title(term.getTitle())
                .subTitle(term.getSubTitle())
                .termDetailList(termDetailList)
                .build();
    }
}
