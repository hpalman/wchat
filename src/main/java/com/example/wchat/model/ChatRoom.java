package com.example.wchat.model;

import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatRoom implements Serializable {
    private String roomId;      // 채팅방 ID
    private String customerName;// 고객 이름
    private boolean isBotMode;  // 봇 응대 중인지 여부
    private String status;      // WAITING(대기), ON_AIR(상담중)

    public static ChatRoom create(String name) {
        return ChatRoom.builder()
                .roomId(UUID.randomUUID().toString())
                .customerName(name)
                .isBotMode(true)
                .status("WAITING")
                .build();
    }
}