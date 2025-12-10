package com.ssafy.yogiattacku.openai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.yogiattacku.attraction.dto.response.SimilarAttraction;
import com.ssafy.yogiattacku.attraction.dto.response.Spot;
import com.ssafy.yogiattacku.attraction.repository.AttractionDescriptionJdbcRepository;
import com.ssafy.yogiattacku.global.exception.ErrorCode;
import com.ssafy.yogiattacku.global.exception.GlobalException;
import com.ssafy.yogiattacku.openai.dto.request.ChatRequest;
import com.ssafy.yogiattacku.openai.dto.response.RecommendStreamEvent;
import com.ssafy.yogiattacku.openai.embedding.EmbeddingSentence;
import com.ssafy.yogiattacku.openai.prompt.TravelPromptProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.ssafy.yogiattacku.openai.dto.response.RecommendEventType.*;

@Service
@RequiredArgsConstructor
public class RecommendService {
    private final AttractionDescriptionJdbcRepository attractionDescriptionJdbcRepository;
    private final OpenAiChatModel openAiChatModel;
    private final ObjectMapper objectMapper;
    private final EmbeddingSentence embeddingSentence;
    private final TravelPromptProvider travelPromptProvider;

    @Value("${spring.ai.openai.chat.options.model}")
    private String modelName;

    private static final int LIMIT = 5;
    private static final double TEMPERATURE = 0.7;

    public Flux<ServerSentEvent<RecommendStreamEvent>> recommendStream(ChatRequest request) {
        try {
            float[] queryVector = embeddingSentence
                    .requestEmbeddingFromOpenAI(List.of(request.query()))
                    .getFirst();

            List<SimilarAttraction> candidates =
                    attractionDescriptionJdbcRepository.findSimilarAttractions(queryVector, LIMIT);

            List<Spot> spotResponses = candidates.stream()
                    .map(c -> new Spot(
                            c.id(),
                            c.attractionName(),
                            c.sidoName(),
                            c.gugunName(),
                            c.latitude(),
                            c.longitude()
                    ))
                    .toList();

            RecommendStreamEvent spotsPayload = RecommendStreamEvent.builder()
                    .type(SPOTS)
                    .spots(spotResponses)
                    .build();

            Flux<ServerSentEvent<RecommendStreamEvent>> spotsEvent = Flux.just(
                    ServerSentEvent.<RecommendStreamEvent>builder()
                            .event(SPOTS.eventName())
                            .data(spotsPayload)
                            .build()
            );

            String candidatesJson = objectMapper.writeValueAsString(spotResponses);

            SystemMessage systemMessage = travelPromptProvider.buildSystemMessage();
            UserMessage userMessage = travelPromptProvider.buildUserMessage(request.query(), candidatesJson);

            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .model(modelName)
                    .temperature(TEMPERATURE)
                    .build();

            Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);

            Flux<String> descriptionStream = openAiChatModel.stream(prompt)
                    .mapNotNull(res -> res.getResult().getOutput().getText());

            Flux<ServerSentEvent<RecommendStreamEvent>> descriptionEvents =
                    descriptionStream.map(chunk -> {
                        RecommendStreamEvent payload = RecommendStreamEvent.builder()
                                .type(DESCRIPTION)
                                .descriptionChunk(chunk)
                                .build();

                        return ServerSentEvent.<RecommendStreamEvent>builder()
                                .event(DESCRIPTION.eventName()) // "description"
                                .data(payload)
                                .build();
                    });

            Flux<ServerSentEvent<RecommendStreamEvent>> doneEvent =
                    Flux.just(
                            ServerSentEvent.<RecommendStreamEvent>builder()
                                    .event(DONE.eventName()) // "done"
                                    .data(RecommendStreamEvent.builder()
                                            .type(DONE)
                                            .build())
                                    .build()
                    );

            return Flux.concat(spotsEvent, descriptionEvents, doneEvent);
        } catch (Exception e) {
            if (e instanceof GlobalException ge) {
                throw ge;
            }
            throw new GlobalException(ErrorCode.RECOMMENDATION_GENERATION_FAILED);
        }
    }
}
