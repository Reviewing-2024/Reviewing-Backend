package com.reviewing.review.recommend.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatGPTExample {

    // OpenAI API 키를 여기에 입력하세요
    private static final String API_KEY = "";
    private static final int BUFFER_SIZE = 4096;
    private static final int MAX_TOKENS = 4096; // 최대 토큰 수 설정

    @GetMapping("/gpt")
    public void test() {
        // 사용할 모델을 선택하세요
        String model = "gpt-3.5-turbo";

        try {
            // API URL 설정
            String url = "https://api.openai.com/v1/chat/completions";

            // 멀티라인 입력 프롬프트
            String prompt = "백엔드 개발자가 되는 법 알려줘";

            // JSON 요청 본문 생성
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            requestBody.put("max_tokens", MAX_TOKENS); // 최대 토큰 수 설정
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.put(message);
            requestBody.put("messages", messages);

            // HTTP 연결 설정
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY.trim());
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // 요청 본문 전송
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 응답 받기
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            // 응답 파싱 및 출력
            parseAndPrintResponse(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseAndPrintResponse(String responseBody) {
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONArray choices = jsonObject.getJSONArray("choices");

        for (int i = 0; i < choices.length(); i++) {
            String text = choices.getJSONObject(i).getJSONObject("message").getString("content");
            printInChunks(text, BUFFER_SIZE);
        }
    }

    private static void printInChunks(String text, int bufferSize) {
        int length = text.length();
        int start = 0;

        while (start < length) {
            int end = Math.min(length, start + bufferSize);
            System.out.println(text.substring(start, end));
            start = end;
        }
    }
}
