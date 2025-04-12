package com.spring.ai.controllers;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public final class OlympicController {

    private final OpenAiChatModel openAiChatModel;

    @Value("classpath:/prompts/olympics.st")
    private Resource olympicResource;

    @Value("classpath:/docs/olympic-sports.txt")
    private Resource olympicSports;

    public OlympicController(OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    @GetMapping("/olympic")
    public String getOlympicSports(
            @RequestParam(value = "message", defaultValue = "What sports are being included in 2028 olympics") String message,
            @RequestParam(value = "stuffit", defaultValue = "false") boolean stuffit
    ) {

        PromptTemplate promptTemplate = new PromptTemplate(olympicResource);
        Map<String, Object> map = new HashMap<>();
        map.put("question", message);
        if (stuffit) {
            map.put("context", olympicSports);
        } else {
            map.put("context", "");
        }
        Prompt prompt = promptTemplate.create(map);
        ChatResponse response = openAiChatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }
}
