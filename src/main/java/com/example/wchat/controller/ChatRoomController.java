package com.example.wchat.controller;

import com.example.wchat.model.ChatMessage;
import com.example.wchat.model.ChatRoom;
import com.example.wchat.repository.ChatRoomRepository;
import com.example.wchat.service.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;
    private final RedisPublisher redisPublisher;

    // 고객 로그인 시 방 생성
    @PostMapping("/room")
    public ChatRoom createRoom(@RequestParam String name) {
        ChatRoom room = ChatRoom.create(name);
        chatRoomRepository.saveRoom(room);
        return room;
    }

    // 상담사용: 대기 중인 고객 목록 조회
    @GetMapping("/rooms")
    public List<ChatRoom> rooms() {
        return chatRoomRepository.findAllRoom();
    }

    // 타 서버 BOT이 비동기적으로 회신을 줄 때 호출하는 API
    @PostMapping("/bot/callback")
    public void botCallback(@RequestBody ChatMessage botMessage) {
        botMessage.setType(ChatMessage.MessageType.TALK);
        botMessage.setSender("AI_BOT");
        redisPublisher.publish(botMessage); // Redis를 통해 채팅방에 전달
    }
}
