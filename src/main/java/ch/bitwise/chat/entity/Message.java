package ch.bitwise.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class Message {
    @NonNull
    private String body;
}
