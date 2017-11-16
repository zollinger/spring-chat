package ch.bitwise.chat.configuration;

import ch.bitwise.chat.security.JwtAuthenticationToken;
import ch.bitwise.chat.security.JwtHelper;
import ch.bitwise.chat.service.CustomUserDetailsService;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.util.List;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration
@EnableWebSocketMessageBroker
@Order(HIGHEST_PRECEDENCE + 50)
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    private JwtHelper jwtHelper;

    private CustomUserDetailsService userDetailsService;

    public WebSocketConfig(JwtHelper jwtHelper,
                           CustomUserDetailsService userDetailsService) {
        this.jwtHelper = jwtHelper;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptorAdapter() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                List tokenList = accessor.getNativeHeader(jwtHelper.TOKEN_HEADER);
                String token;

                if (!StompCommand.CONNECT.equals(accessor.getCommand()) || tokenList == null || tokenList.size() < 1) {
                    return message;
                }

                token = (String) tokenList.get(0);
                String userName = jwtHelper.getUsernameFromToken(token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                if (jwtHelper.validateToken(token, userDetails)) {
                    JwtAuthenticationToken authentication = new JwtAuthenticationToken(userDetails);
                    authentication.setToken(token);

                    accessor.setUser(authentication);
                }

                return message;
            }
        });
    }
}
