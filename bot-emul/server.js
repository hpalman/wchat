/**
 * 파일명: bot-emulator/server.js
 * 설명: wchat 시스템의 질문을 받아 처리하고 비동기적으로 회신하는 봇 에뮬레이터
 * 주요 기능: 질문 수신(/api/ask), 질문 해석, 특수 형식(JSON 포함 TEXT) 회신
 */

const express = require('express');
const axios = require('axios');
const app = express();

// JSON 본문을 파싱하기 위한 미들웨어
app.use(express.json());

// 회신을 보낼 wchat 서버의 설정 (필요 시 수정)
const WCHAT_SERVER_URL = "http://localhost:8080/chat/bot/callback";

/**
 * [POST] /api/ask
 * 질문을 받는 엔드포인트
 */
app.post('/api/ask', (req, res) => {
    // 1. 요청 데이터 추출 (wchat에서 보낸 ChatMessage 객체 가정)
    const { roomId, sender, message } = req.body;

    console.log(`[수신] 방번호: ${roomId}, 발신자: ${sender}, 질문내용: ${message}`);

    // 2. 즉시 200 OK 응답 (비동기 처리를 위해 접수 확인만 보냄)
    res.status(200).send({ status: "success", detail: "질문이 접수되었습니다." });

    /**
     * 3. 질문 처리 시뮬레이션 (3초 지연 후 비동기 회신)
     * 실제 서비스에서는 여기서 AI 모델을 호출하거나 DB를 조회함
     */
    setTimeout(async () => {
        console.log(`[처리] 질문 '${message}'에 대한 답변 생성 중...`);

        // 질문에 따른 간단한 응답 로직
        let answerText = "기본 답변입니다.";
        if (message.includes("가격")) {
            answerText = "가격은 월 10,000원입니다.";
        } else if (message.includes("안녕")) {
            answerText = "안녕하세요! 봇 에뮬레이터입니다.";
        }

        /**
         * 4. 회신 데이터 생성 
         * 요구사항: 전체가 JSON은 아니고, 여러 줄의 JSON 데이터가 포함된 TEXT
         */
        const responseData = 
`[BOT_SYSTEM_LOG] 처리가 완료되었습니다.
데이터 시작 ---
{"roomId": "${roomId}", "sender": "AI_BOT", "message": "${answerText}"}
{"status": "COMPLETED", "timestamp": "${new Date().toISOString()}"}
데이터 끝 ---
이 메시지는 에뮬레이터에서 자동 생성되었습니다.`;

        try {
            // 5. wchat 서버의 callback API 호출
            // 주의: wchat 서버가 TEXT를 받을 수 있도록 구성되어야 함
            // 만약 wchat 서버가 ChatMessage 객체(JSON)를 기대한다면 형식을 맞춰야 합니다.
            await axios.post(WCHAT_SERVER_URL, {
                roomId: roomId,
                sender: "AI_BOT",
                message: responseData // 요구사항에 따른 텍스트 형식의 데이터 전송
            });
            console.log(`[회신성공] 방번호 ${roomId}로 답변을 보냈습니다.`);
        } catch (error) {
            console.error(`[회신실패] wchat 서버 접속 불가: ${error.message}`);
        }
    }, 3000); // 3초 뒤 실행
});

// 서버 실행
const PORT = 3000;
app.listen(PORT, () => {
    console.log(`=========================================`);
    console.log(` Bot Emulator가 포트 ${PORT}에서 실행 중입니다.`);
    console.log(` POST http://localhost:${PORT}/api/ask`);
    console.log(`=========================================`);
});
