package com.ssafy.yogiattacku.openai.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ssafy.yogiattacku.global.exception.ErrorCode;
import com.ssafy.yogiattacku.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EmbeddingSentence {
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    @Value("${spring.ai.openai.embedding.options.model}")
    private String embeddingModel;
    @Value("https://gms.ssafy.io/gmsapi/api.openai.com/v1/embeddings")
    private String apiUrl;
    @Value("${spring.ai.vectorstore.pgvector.dimensions}")
    private Integer dimensions;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final ObjectMapper mapper;

    public List<float[]> requestEmbeddingFromOpenAI(List<String> input) {
        try {
            ObjectNode root = mapper.createObjectNode();

            for (String sentence : input) {
                root.withArray("input").add(sentence);
            }

            root.put("model", embeddingModel);
            root.put("dimensions", dimensions);

            String json = mapper.writeValueAsString(root);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response
                    = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new GlobalException(ErrorCode.EMBEDDING_REQUEST_FAILED);
            }

            if (response.body() == null || response.body().isBlank()) {
                throw new GlobalException(ErrorCode.EMBEDDING_REQUEST_FAILED);
            }

            List<float[]> result = new ArrayList<>();
            for (JsonNode jsonNode : mapper.readTree(response.body()).get("data")) {
                float[] embedding = new float[jsonNode.get("embedding").size()];
                int index = 0;
                for (JsonNode embeddingNode : jsonNode.get("embedding")) {
                    embedding[index++] = (float) embeddingNode.asDouble();
                }
                result.add(embedding);
            }
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GlobalException(ErrorCode.EMBEDDING_REQUEST_FAILED);
        } catch (IOException e) {
            throw new GlobalException(ErrorCode.EMBEDDING_REQUEST_FAILED);
        }
    }
}
