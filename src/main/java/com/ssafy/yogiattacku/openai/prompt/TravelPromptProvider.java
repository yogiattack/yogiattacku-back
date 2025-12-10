package com.ssafy.yogiattacku.openai.prompt;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TravelPromptProvider {
    @Value("${spring.ai.prompt.system}")
    private String systemPrompt;

    @Value("${spring.ai.prompt.user-template}")
    private String userTemplatePrompt;

    public SystemMessage buildSystemMessage() {
        return new SystemMessage(systemPrompt);
    }

    public UserMessage buildUserMessage(String query, String candidatesJson) {
        String formatted = userTemplatePrompt.replace("{query}", query)
                .replace("{candidates}", candidatesJson);
        return new UserMessage(formatted);
    }
}
