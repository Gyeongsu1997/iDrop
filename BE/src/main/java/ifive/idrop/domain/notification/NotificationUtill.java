package ifive.idrop.domain.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import ifive.idrop.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationUtill {

    static public void createNotification(User user, String title, String message) {
        if (user.getFcmToken() == null) {
            log.error("user = {}, FCM토큰이 없습니다", user.getLoginId());
            return;
        }

        try {
            sendNotification(title, message, user.getFcmToken());
        } catch (ExecutionException e) {
            log.error("user = {}, FCM토큰이 없습니다", user.getLoginId());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static private void sendNotification(String title, String body, String token) throws ExecutionException, InterruptedException {
        Message message = Message.builder()
                .setWebpushConfig(WebpushConfig.builder()
                        .setNotification(WebpushNotification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .build())
                .setToken(token)
                .build();

        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        log.info(">>>>Send message = {}", response);
    }
}
