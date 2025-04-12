package com.spring.ai.controllers;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public final class SongsController {

    private final OpenAiChatModel openAiChatModel;

    @Value("classpath:/prompts/songs.st")
    private Resource songsPrompt;

    public SongsController(OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    @GetMapping("/songs")
    public List<String> getSongsByArtist(@RequestParam(value = "artist", defaultValue = "Anne marie") String artist) {
        ListOutputConverter listOutputConverter = new ListOutputConverter(new DefaultConversionService());
        PromptTemplate promptTemplate = new PromptTemplate(songsPrompt);
        Prompt prompt = promptTemplate.create(Map.of("artist", artist, "format", listOutputConverter.getFormat()));
        ChatResponse response = openAiChatModel.call(prompt);
        return listOutputConverter.convert(response.getResult().getOutput().getText());
    }

//    @GetMapping("/songs")
//    public String getSongsByArtist(@RequestParam(value = "artist", defaultValue = "Anne marie") String artist) {
//        PromptTemplate promptTemplate = new PromptTemplate(songsPrompt);
//        Prompt prompt = promptTemplate.create(Map.of("artist", artist));
//        ChatResponse response = openAiChatModel.call(prompt);
//        return response.getResult().getOutput().getText();
//    }
}
