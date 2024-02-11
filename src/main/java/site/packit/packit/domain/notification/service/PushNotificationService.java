package site.packit.packit.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.notification.constant.NotificationType;
import site.packit.packit.domain.notification.dto.FcmMessage;
import site.packit.packit.domain.notification.dto.NotificationConfigStatus;
import site.packit.packit.domain.notification.dto.NotificationHistoryDto;
import site.packit.packit.domain.notification.entity.FcmToken;
import site.packit.packit.domain.notification.entity.NotificationHistory;
import site.packit.packit.domain.notification.entity.PushNotificationSubscriber;
import site.packit.packit.domain.notification.exception.NotificationException;
import site.packit.packit.domain.notification.repository.FcmTokenRepository;
import site.packit.packit.domain.notification.repository.NotificationHistoryRepository;
import site.packit.packit.domain.notification.repository.PushNotificationSubscriberRepository;

import java.io.IOException;
import java.util.List;

import static site.packit.packit.domain.notification.exception.NotificationErrorCode.PUSH_NOTIFICATION_SUBSCRIBER_ALREADY_EXIST;
import static site.packit.packit.domain.notification.exception.NotificationErrorCode.PUSH_NOTIFICATION_SUBSCRIBER_NOT_FOUND;

@Transactional
@Service
public class PushNotificationService {

    private final PushNotificationSubscriberRepository pushNotificationSubscriberRepository;
    private final NotificationHistoryRepository notificationHistoryRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.fcm.api-url}")
    private String FCM_API_URL;

    public PushNotificationService(
            PushNotificationSubscriberRepository pushNotificationSubscriberRepository,
            NotificationHistoryRepository notificationHistoryRepository,
            FcmTokenRepository fcmTokenRepository,
            ObjectMapper objectMapper
    ) {
        this.pushNotificationSubscriberRepository = pushNotificationSubscriberRepository;
        this.notificationHistoryRepository = notificationHistoryRepository;
        this.fcmTokenRepository = fcmTokenRepository;
        this.objectMapper = objectMapper;
    }

    public void createSubscriber(NotificationType notificationType, Member member) {
        checkSubscriberExist(notificationType, member);
        pushNotificationSubscriberRepository.save(new PushNotificationSubscriber(notificationType, member));
    }

    private void checkSubscriberExist(NotificationType notificationType, Member member) {
        boolean isExist = isPushNotificationSubscriber(notificationType, member);

        if (isExist) {
            throw new NotificationException(PUSH_NOTIFICATION_SUBSCRIBER_ALREADY_EXIST);
        }
    }

    public void deleteSubscriber(NotificationType notificationType, Member member) {
        PushNotificationSubscriber pushNotificationSubscriber = pushNotificationSubscriberRepository.findByNotificationTypeAndMember(notificationType, member)
                .orElseThrow(() -> new NotificationException(PUSH_NOTIFICATION_SUBSCRIBER_NOT_FOUND));

        pushNotificationSubscriberRepository.delete(pushNotificationSubscriber);
    }

    @Transactional(readOnly = true)
    public NotificationConfigStatus getAllNotificationConfigStatus(Member member) {
        List<PushNotificationSubscriber> notificationSubscribers = pushNotificationSubscriberRepository.findAllByMember(member);

        if (notificationSubscribers.isEmpty()) {
            return NotificationConfigStatus.notNotificationConfig();
        }

        return NotificationConfigStatus.of(notificationSubscribers);
    }

    @Transactional(readOnly = true)
    public boolean isPushNotificationSubscriber(NotificationType notificationType, Member member) {
        return pushNotificationSubscriberRepository.existsByNotificationTypeAndMember(notificationType, member);
    }

    public void saveFcmToken(Member member, String tokenValue) {
        fcmTokenRepository.findFcmTokenByMember(member)
                .ifPresentOrElse(
                        fcmToken -> fcmToken.updateValue(tokenValue),
                        () -> fcmTokenRepository.save(new FcmToken(tokenValue, member))
                );
    }

    public void sendMessageTo(NotificationType notificationType, List<Member> recivers, String title, String body) throws IOException {
        recivers
                .forEach(reciver -> {
                    String fcmToken = parseFcmToken(reciver);
                    try {
                        sendMessage(fcmToken, title, body);
                        saveNotificationHistory(notificationType, reciver, title, body);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void sendMessage(String fcmToken, String title, String body) throws IOException {
        String message = makeMessage(fcmToken, title, body);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(FCM_API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        assert response.body() != null;
        System.out.println(response.body().string());
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        )
                        .build()
                )
                .validateOnly(false)
                .build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "/firebase/serviceAccountKey.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    private String parseFcmToken(Member reciver) {
        return fcmTokenRepository.findFcmTokenByMember(reciver)
                .map(FcmToken::getValue)
                .orElseThrow(() -> new NotificationException(PUSH_NOTIFICATION_SUBSCRIBER_NOT_FOUND));
    }

    private void saveNotificationHistory(NotificationType notificationType, Member member, String notificationTitle, String notificationContent) {
        NotificationHistory notificationHistory = new NotificationHistory(notificationTitle, notificationContent, notificationType, member);
        notificationHistoryRepository.save(notificationHistory);
    }

    public List<NotificationHistoryDto> getNotificationHistories(Member member) {
        return notificationHistoryRepository.findAllByMember(member)
                .stream()
                .map(history -> new NotificationHistoryDto(
                        history.getNotificationTitle(),
                        history.getNotificationContent(),
                        history.getNotificationType(),
                        history.getCreatedAt()
                ))
                .toList();
    }
}
