package com.example.wchat.controller;

import com.example.wchat.model.ChatRoom;
import com.example.wchat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {
private final ChatRoomRepository chatRoomRepository;

    // name 파라미터 이름을 명시적으로 지정하여 컴파일 옵션 문제를 해결합니다.
    @PostMapping("/room")
    public ChatRoom createRoom(@RequestParam(name = "name") String name) {
        ChatRoom room = ChatRoom.create(name);
        chatRoomRepository.saveRoom(room);
        return room;
    }

    @GetMapping("/rooms")
    public List<ChatRoom> rooms() {
        return chatRoomRepository.findAllRoom();
    }
}
