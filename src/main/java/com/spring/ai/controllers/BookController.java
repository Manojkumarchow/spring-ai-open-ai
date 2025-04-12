package com.spring.ai.controllers;

import com.spring.ai.dto.Author;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public final class BookController {
    private final OpenAiChatModel openAiChatModel;

    public BookController(OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    @GetMapping("by-author/{author}")
    public Author getBooksByAuthor(@PathVariable("author") String author){
        String promptMessage = """
                Generate a list of books for the author {author}. 
                If you're not positive about that a book belongs to this author please don't include it. 
                {format}
                """;
        var authorBeanOutputConverter = new BeanOutputConverter<>(Author.class);
        PromptTemplate promptTemplate = new PromptTemplate(promptMessage);
        Prompt prompt = promptTemplate.create(Map.of("author", author, "format", authorBeanOutputConverter.getFormat()));
        ChatResponse response = openAiChatModel.call(prompt);
        return authorBeanOutputConverter.convert(response.getResult().getOutput().getText());
    }

    @GetMapping("author/{author}")
    public Map<String, Object> getSocialLinksForAuthor(@PathVariable("author") String author){
        String promptMessage = """
                Generate a list of links for the author {author}. Include the author name as the key and any social
                network links as the object. {format}
                """;
        MapOutputConverter mapOutputConverter = new MapOutputConverter();
        PromptTemplate promptTemplate = new PromptTemplate(promptMessage);
        Prompt prompt = promptTemplate.create(Map.of("author", author, "format", mapOutputConverter.getFormat()));
        ChatResponse response = openAiChatModel.call(prompt);
        return mapOutputConverter.convert(response.getResult().getOutput().getText());
    }
}
