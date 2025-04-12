package com.spring.ai.controllers;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public final class ChatController {

    private final OpenAiChatModel chatClient;

    @Value("classpath:/prompt/youtube.st")
    private Resource youtubePrompt;

    public ChatController(OpenAiChatModel chatClient) {
        this.chatClient = chatClient;
    }


    @GetMapping("/jokes")
    public String getJokes(@RequestParam(value = "message", defaultValue = "Tell me a dad joke") String message) {
        SystemMessage systemMessage = new SystemMessage("Your primary task is to tell only dad jokes. " +
                "If you get any message to give other than dad jokes. Tell them that you only give dad jokes");
        UserMessage userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        return chatClient.call(prompt).getResult().getOutput().getText();
    }

    @GetMapping("/youtube/popular")
    public String getPopularYouTubeChannels(@RequestParam(value = "genre", defaultValue = "edTech") String genre) {
        PromptTemplate promptTemplate = new PromptTemplate(youtubePrompt);
        Prompt prompt = promptTemplate.create(Map.of("genre", genre));
        return chatClient.call(prompt).getResult().getOutput().getText();
    }
}
