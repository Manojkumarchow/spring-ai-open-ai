package com.spring.ai.controllers;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class FaqController {

    private final OpenAiChatModel openAiChatModel;
    private final VectorStore vectorStore;

    @Value("classpath:/prompts/rag-prompt-template.st")
    private Resource ragPromptTemplate;

    public FaqController(OpenAiChatModel openAiChatModel, VectorStore vectorStore) {
        this.openAiChatModel = openAiChatModel;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/faq")
    public String faq(@RequestParam(value = "message", defaultValue = "How can I buy tickets for the olympic games paris 2024") String message) {
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder().query(message).topK(2).build());
        if (CollectionUtils.isEmpty(documents)) {
            return "No relevant documents found.";
        }
        List<String> contentList = documents.stream().map(Document::getText).toList();
        PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
        Prompt prompt = promptTemplate.create(Map.of("input", message, "documents", String.join("\n", contentList)));
        ChatResponse response = openAiChatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }
}
