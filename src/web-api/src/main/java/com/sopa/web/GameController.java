package com.sopa.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*") // Allow React dev server
public class GameController {

    @Autowired
    private UdpService udpService;

    @PostMapping("/start")
    public BoardResponse start(@RequestBody StartRequest request) {
        // UDP: START:Name
        String response = udpService.sendAndReceive("START:" + request.name());
        // Response: BOARD:CHARS#WORDS
        if (response.startsWith("BOARD:")) {
            String payload = response.substring(6);
            String[] parts = payload.split("#");
            String boardStr = parts[0];
            String wordsStr = parts.length > 1 ? parts[1] : "";
            return new BoardResponse("OK", boardStr, wordsStr);
        }
        return new BoardResponse("ERROR", null, null);
    }

    @PostMapping("/validate")
    public ValidateResponse validate(@RequestBody ValidateRequest request) {
        // UDP: FOUND:Word
        String response = udpService.sendAndReceive("FOUND:" + request.word());
        // Response: VALID:Word or INVALID
        boolean isValid = response.startsWith("VALID");
        return new ValidateResponse(isValid, request.word());
    }

    @PostMapping("/finish")
    public GenericResponse finish(@RequestBody FinishRequest request) {
        // UDP: FINISH:Name,Time
        String response = udpService.sendAndReceive("FINISH:" + request.name() + "," + request.time());
        return new GenericResponse(response);
    }

    // DTOs
    public record StartRequest(String name) {
    }

    public record BoardResponse(String status, String board, String hiddenWords) {
    }

    public record ValidateRequest(String word) {
    }

    public record ValidateResponse(boolean valid, String word) {
    }

    public record FinishRequest(String name, String time) {
    }

    public record GenericResponse(String message) {
    }
}
