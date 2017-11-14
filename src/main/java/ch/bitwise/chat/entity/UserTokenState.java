package ch.bitwise.chat.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserTokenState {
    private String accessToken;
    private int expiresIn;
}