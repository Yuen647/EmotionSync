package org.example.springboottest.AIchat.Controller;

import org.example.springboottest.AIchat.Entity.AIchat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONObject;

@RestController
@CrossOrigin(origins = "http://localhost:5173/")
@RequestMapping("/chat")
public class AIChatController {
    @Autowired
    private AIchatRepository AIchatRepository;

    @Value("${api.Key}")
    private String apikey;

    @Value("${api.Url}")
    private String apiurl;

    private String mood;
    @PostMapping("/getMood")
    public void getMood(@RequestBody Map<String, String> requestData) {
        this.mood = requestData.get("mood");
    }

    @PostMapping("/get")
    public String getAIchat(@RequestBody Map<String, String> requestData) {
        String username = requestData.get("username");
        return AIchatRepository.findById(username)
                .map(AIchat::getChatmessage) // 提取 chatMessage 字段
                .orElse("User not found");  // 如果没有找到记录，返回提示信息
    }

    @PostMapping("/add")
    public ResponseEntity<String> addAIchat(@RequestBody AIchat aichat) {
        if (aichat.getUsername() == null || aichat.getUsername().isEmpty()) {
            return ResponseEntity.badRequest().body("Username cannot be null or empty.");
        }
        Optional<AIchat> optionalAIchat = AIchatRepository.findById(aichat.getUsername());
        if (optionalAIchat.isPresent()) {
            AIchat existingAIchat = optionalAIchat.get();
            existingAIchat.setChatmessage(aichat.getChatmessage()); // 修改 chatMessage
            AIchatRepository.save(existingAIchat); // 保存修改后的实体
            return ResponseEntity.ok().body("Chat message updated successfully.");
        }
        else {
            AIchatRepository.save(aichat); // 保存到数据库
        }
        return ResponseEntity.ok("Data added successfully!");
    }

    @PostMapping("/send")
    public String sendAIchat(@RequestBody List<Map<String, String>> messages) {

        String apiUrl = apiurl;
        String apiKey = apikey;

        try {
            // 创建 URL 对象
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法和头信息
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", apiKey);
            connection.setRequestProperty("User-Agent", "Apifox/1.0.0 (https://apifox.com)");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // 构建 JSON 请求体
            JSONObject payload = new JSONObject();
            payload.put("model", "gpt-3.5-turbo");

            // 转换消息为 JSON 数组
            JSONArray messagesArray = new JSONArray(messages);
            System.out.println(messagesArray);
            System.out.println(mood);
            // 获取数组中的最后一个元素
            JSONObject lastMessage = messagesArray.getJSONObject(messagesArray.length() - 1);
            // 获取最后一项的 content
            String content = lastMessage.getString("content");
            // 在原有的 content 上添加新的内容
            String updatedContent = content + ",我当前的情绪是"+mood;
            // 更新 content 字段
            lastMessage.put("content", updatedContent);
            payload.put("messages", messagesArray);

            // 发送请求体数据
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 获取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 读取响应体
                String response = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                JSONObject responseObject = new JSONObject(response);

                // 返回解析的内容
                return responseObject
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } else {
                // 处理非 200 响应
                return "Error: HTTP " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
