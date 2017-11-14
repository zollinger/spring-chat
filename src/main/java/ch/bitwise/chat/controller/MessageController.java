package ch.bitwise.chat.controller;

import ch.bitwise.chat.entity.Message;
import ch.bitwise.chat.entity.OutputMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Controller
public class MessageController {

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public OutputMessage message(Message message, Principal user) throws Exception {
        return new OutputMessage(
                message.getBody(),
                user.getName(),
                Instant.now()
        );
    }
}
