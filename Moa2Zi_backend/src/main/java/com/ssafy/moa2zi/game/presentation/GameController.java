package com.ssafy.moa2zi.game.presentation;

import java.nio.file.AccessDeniedException;
import java.util.List;

import com.ssafy.moa2zi.game.application.GameService;
import com.ssafy.moa2zi.game.domain.Game;
import com.ssafy.moa2zi.game.dto.response.GameGetResponse;

import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<Void> createGame(){
        gameService.createGame();
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<GameGetResponse>> getGame(
            Long loungeId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws AccessDeniedException {

        List<GameGetResponse> gameList = gameService.getGame(loungeId, loginMember);
        return ResponseEntity.ok(gameList);
    }

    @GetMapping("/histories")
    public ResponseEntity<?> getGameHistory(@AuthenticationPrincipal CustomMemberDetails loginMember){
        return ResponseEntity.ok(gameService.getGameHistory(loginMember));
    }

}
