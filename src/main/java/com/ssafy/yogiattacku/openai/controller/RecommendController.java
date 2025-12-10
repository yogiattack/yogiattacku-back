package com.ssafy.yogiattacku.openai.controller;

import com.ssafy.yogiattacku.openai.dto.request.ChatRequest;
import com.ssafy.yogiattacku.openai.dto.response.RecommendStreamEvent;
import com.ssafy.yogiattacku.openai.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommend")
public class RecommendController {
    private final RecommendService recommendService;

    @PostMapping(
            value = "/stream",
            consumes = APPLICATION_JSON_VALUE,
            produces = TEXT_EVENT_STREAM_VALUE
    )
    public Flux<ServerSentEvent<RecommendStreamEvent>> streamRecommend(@RequestBody ChatRequest request) {
        return Flux.defer(() -> recommendService.recommendStream(request));
    }
}
