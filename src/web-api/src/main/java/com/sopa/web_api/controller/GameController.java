package com.sopa.web_api.controller;

import com.sopa.web_api.service.UdpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*") // Allow requests from React Frontend
public class GameController {

    @Autowired
    private UdpClientService udpClientService;

    @PostMapping("/start")
    public Map<String, Object> startGame() {
        String response = udpClientService.sendRequest("START_GAME");
        Map<String, Object> result = new HashMap<>();

        if (response.startsWith("ERROR")) {
            result.put("status", "error");
            result.put("message", response);
        } else {
            // Parse Board CSV
            // Format: ROW1,ROW2...;WORD1,WORD2...
            try {
                String[] parts = response.split(";");
                String[] rows = parts[0].split(",");
                String[] words = (parts.length > 1) ? parts[1].split(",") : new String[0];

                result.put("status", "ok");
                result.put("board", rows);
                result.put("words", words);
            } catch (Exception e) {
                result.put("status", "error");
                result.put("message", "Failed to parse board data");
            }
        }
        return result;
    }

    @PostMapping("/validate")
    public Map<String, String> validateWord(@RequestBody Map<String, String> payload) {
        String word = payload.get("word");
        String response = udpClientService.sendRequest("VALIDATE_WORD:" + word);
        Map<String, String> result = new HashMap<>();
        result.put("status", response.equals("VALID") ? "valid" : "invalid");
        return result;
    }
}
