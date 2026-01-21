package com.example.wchat.controller;

import com.example.wchat.model.ChatMessage;
import com.example.wchat.model.ChatRoom;
import com.example.wchat.repository.ChatRoomRepository;
import com.example.wchat.service.BotService;
import com.example.wchat.service.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final BotService botService;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        ChatRoom room = chatRoomRepository.findRoomById(message.getRoomId());

        if (ChatMessage.MessageType.REQ_COUNSELOR.equals(message.getType())) {
            room.setStatus("WAITING");
            chatRoomRepository.saveRoom(room);
            message.setMessage(message.getSender() + " 고객님이 상담사를 호출했습니다.");
        }

        if (ChatMessage.MessageType.ACCEPT.equals(message.getType())) {
            room.setBotMode(false);
            room.setStatus("ON_AIR");
            chatRoomRepository.saveRoom(room);
            message.setMessage("상담원이 연결되었습니다.");
        }

        // Redis 발행 (본인 및 구독자들에게 전달)
        redisPublisher.publish(message);

        // 봇 모드일 경우 WebClient로 외부 서버 호출
        if (room.isBotMode() && ChatMessage.MessageType.TALK.equals(message.getType())) {
            botService.askToBotServer(message);
        }
    }
}