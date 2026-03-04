package com.askrida.web.service.service;

import com.askrida.web.service.model.ChatMessage;
import com.askrida.web.service.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic untuk modul Chatbot.
 */
@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    public List<ChatMessage> getMessagesBySession(String sessionId) {
        return chatRepository.getMessagesBySession(sessionId);
    }

    @Transactional
    public void saveMessage(String sessionId, String sender, String message) throws Exception {
        chatRepository.saveMessage(sessionId, sender, message);
    }

    public String generateBotReply(String userMessage) {
        return chatRepository.generateBotReply(userMessage);
    }

    @Transactional
    public void deleteSessionMessages(String sessionId) throws Exception {
        chatRepository.deleteSessionMessages(sessionId);
    }

    public List<String> getAllSessions() {
        return chatRepository.getAllSessions();
    }
}
