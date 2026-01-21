// RedisSubscriber.java
package com.example.wchat.service;

import com.example.wchat.model.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisSubscriber {
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    // Redis에서 메시지가 발행되면 수신하여 WebSocket 구독자들에게 전달
    public void onMessage(String message) {
        try {
            ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
            // 각 채팅방 채널로 전송
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
            
            // 상담원 알림 전 전송 (상담사 전용 알림 채널)
            if (chatMessage.getType() == ChatMessage.MessageType.REQ_COUNSELOR) {
                messagingTemplate.convertAndSend("/sub/counselor/notifications", chatMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
