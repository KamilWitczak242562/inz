package com.example.auth_service.session;

import com.example.auth_service.model.User;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Data
public class SessionManager {
    private static Map<String, Session> sessions;
    private static int maintain = 60;

    static {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(SessionManager::checkSessions, 0, 1, TimeUnit.MINUTES);
    }

    public static void createSession(User user) {
        Session session = new Session(user);
        sessions.put(user.getEmail(), session);
    }

    public static void removeSession(String email) {
        sessions.remove(email);
    }

    public static boolean isSession(String email) {
        return sessions.containsKey(email);
    }

    public static void checkSessions() {
        LocalDateTime now = LocalDateTime.now();
        Iterator<Map.Entry<String, Session>> iterator = sessions.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Session> entry = iterator.next();
            Session session = entry.getValue();

            Duration duration = Duration.between(session.getLocalTime(), now);

            if (duration.toMinutes() > maintain) {
                iterator.remove();
            }
        }
    }
}
