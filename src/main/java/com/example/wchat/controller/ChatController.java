package com.example.wchat.controller;

import com.example.wchat.model.ChatMessage;
import com.example.wchat.model.ChatRoom;
import com.example.wchat.repository.ChatRoomRepository;
import com.example.wchat.service.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        ChatRoom room = chatRoomRepository.findRoomById(message.getRoomId());

        // 1. 고객이 상담사 연결 요청 시
        if (ChatMessage.MessageType.REQ_COUNSELOR.equals(message.getType())) {
            room.setStatus("WAITING");
            chatRoomRepository.saveRoom(room);
            message.setMessage(message.getSender() + " 고객님이 상담사를 호출했습니다.");
        }

        // 2. 상담사가 연결 수락 시
        if (ChatMessage.MessageType.ACCEPT.equals(message.getType())) {
            room.setBotMode(false); // 봇 응대 중단
            room.setStatus("ON_AIR");
            chatRoomRepository.saveRoom(room);
            message.setMessage("상담원이 연결되었습니다.");
        }

        // Redis로 메시지 발행 (Subscriber가 받아서 브로드캐스팅함)
        redisPublisher.publish(message);

        // 3. 봇 응대 로직 (봇 모드이면서 일반 대화일 때만 타 서버 봇 호출 시뮬레이션)
        if (room.isBotMode() && ChatMessage.MessageType.TALK.equals(message.getType())) {
            // 외부 서버 Bot API 호출 (RestTemplate/WebClient 사용 지점)
            // 여기서는 비동기 회신을 가정하므로 별도 API 컨트롤러에서 처리
            System.out.println("타 서버 봇에게 요청 전송: " + message.getMessage());
        }
    }
}