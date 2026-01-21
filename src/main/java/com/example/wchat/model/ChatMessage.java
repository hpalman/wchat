package com.example.wchat.model;

import lombok.*;
import java.io.Serializable;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage implements Serializable {
    public enum MessageType {
        ENTER, TALK, REQ_COUNSELOR, ACCEPT, BOT_REPLY
    }

    private MessageType type;   // 메시지 타입
    private String roomId;      // 방 고유 ID
    private String sender;      // 발신자 이름
    private String message;     // 내용
}
