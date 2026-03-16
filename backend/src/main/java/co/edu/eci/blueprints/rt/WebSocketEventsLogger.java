package co.edu.eci.blueprints.rt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class WebSocketEventsLogger {

    private static final Logger log = LoggerFactory.getLogger(WebSocketEventsLogger.class);

    @EventListener
    public void onConnect(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        log.info("WS CONNECT attempt session={} user={}", sha.getSessionId(), sha.getUser() != null ? sha.getUser().getName() : "anonymous");
    }

    @EventListener
    public void onConnected(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        log.info("WS CONNECTED session={} user={}", sha.getSessionId(), sha.getUser() != null ? sha.getUser().getName() : "anonymous");
    }

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        log.info("WS SUBSCRIBE session={} destination={}", sha.getSessionId(), sha.getDestination());
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        log.info("WS DISCONNECT session={} status={}", event.getSessionId(), event.getCloseStatus());
    }
}
