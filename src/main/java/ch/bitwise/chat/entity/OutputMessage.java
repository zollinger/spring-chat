package ch.bitwise.chat.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class OutputMessage {
    @NonNull
    private String body;

    @NonNull
    private String sender;

    @NonNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC")
    private Instant timestamp;
}
