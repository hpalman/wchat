// 파일 경로: src/main/java/com/example/wchat/controller/ChatRoomController.java
package com.example.wchat.controller;

import com.example.wchat.model.ChatMessage;
import com.example.wchat.model.ChatRoom;
import com.example.wchat.repository.ChatRoomRepository;
import com.example.wchat.service.RedisPublisher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisPublisher redisPublisher;
    private final ObjectMapper objectMapper; // JSON 파싱을 위한 객체

    // 고객 로그인 시 방 생성 (에러 해결을 위해 name 명시)
    @PostMapping("/room")
    public ChatRoom createRoom(@RequestParam(name = "name") String name) {
        ChatRoom room = ChatRoom.create(name);
        chatRoomRepository.saveRoom(room);
        return room;
    }

    // 상담사용: 모든 고객 채팅방 목록 조회
    @GetMapping("/rooms")
    public List<ChatRoom> rooms() {
        return chatRoomRepository.findAllRoom();
    }

    /**
     * [POST] /chat/bot/callback
     * 봇 에뮬레이터로부터 비동기 응답을 받는 API
     * 요구사항: 전체가 JSON이 아닌 여러 줄의 JSON 데이터가 포함된 TEXT를 처리
     */
    @PostMapping("/bot/callback")
    public void botCallback(@RequestBody ChatMessage botResponse) {
        log.info("봇 에뮬레이터로부터 콜백 수신: {}", botResponse.getMessage());

        String rawText = botResponse.getMessage();
        String finalBotMessage = rawText; // 기본값은 전체 텍스트

        try {
            // 1. 정규표현식을 사용하여 텍스트 내부의 JSON 블록 추출 시도
            // 봇 에뮬레이터가 보낸 데이터 중 {"roomId": ...} 형태의 첫 번째 JSON을 찾음
            Pattern pattern = Pattern.compile("\\{.*\\}");
            Matcher matcher = pattern.matcher(rawText);

            if (matcher.find()) {
                String jsonStr = matcher.group();
                // 2. 추출된 JSON 문자열을 파싱하여 실제 message 내용만 추출
                JsonNode node = objectMapper.readTree(jsonStr);
                if (node.has("message")) {
                    finalBotMessage = node.get("message").asText();
                }
            }
        } catch (Exception e) {
            log.error("봇 응답 텍스트 파싱 중 오류 발생 (전체 텍스트를 전송합니다): {}", e.getMessage());
        }

        // 3. 정제된 메시지(또는 전체 텍스트)를 Redis를 통해 고객에게 브로드캐스팅
        ChatMessage chatMessage = ChatMessage.builder()
                .type(ChatMessage.MessageType.TALK) // 일반 대화 타입으로 변경
                .roomId(botResponse.getRoomId())
                .sender("AI_BOT")
                .message(finalBotMessage)
                .build();

        redisPublisher.publish(chatMessage);
    }
}
