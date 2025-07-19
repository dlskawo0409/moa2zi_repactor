package com.ssafy.moa2zi.term.persentation;


import com.ssafy.moa2zi.term.application.TermService;
import com.ssafy.moa2zi.term.domain.Term;
import com.ssafy.moa2zi.term.dto.response.TermDetailGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/terms")
public class TermController {

    private final TermService termService;

    @GetMapping
    public ResponseEntity<List<Term>> getTerm(){
        return ResponseEntity.ok(termService.getTerms());
    }

    @GetMapping("/{termId}/details")
    public ResponseEntity<TermDetailGetResponse> getTermDetail(@PathVariable Long termId){
        return ResponseEntity.ok(termService.getTermDetails(termId));
    }

}
