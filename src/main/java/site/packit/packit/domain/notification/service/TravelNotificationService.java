package site.packit.packit.domain.notification.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.travel.entity.Travel;
import site.packit.packit.domain.travel.entity.TravelMember;
import site.packit.packit.domain.travel.repository.TravelMemberRepository;
import site.packit.packit.domain.travel.repository.TravelRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static site.packit.packit.domain.notification.constant.NotificationType.*;

@Service
public class TravelNotificationService {

    private final PushNotificationService pushNotificationService;
    private final TravelRepository travelRepository;
    private final TravelMemberRepository travelMemberRepository;

    public TravelNotificationService(PushNotificationService pushNotificationService, TravelRepository travelRepository, TravelMemberRepository travelMemberRepository) {
        this.pushNotificationService = pushNotificationService;
        this.travelRepository = travelRepository;
        this.travelMemberRepository = travelMemberRepository;
    }

    public void sendTravelMemberInviteNotification(Travel travel, Member newMember) {
        List<Member> notifocationRecivers = findInviteNotificationRecivers(travel);
        String title = newMember.getNickname() + "님이 여행에 참여했어요!";
        String body = "모두 환영해주세요!";

        try {
            pushNotificationService.sendMessageTo(JOIN, notifocationRecivers, title, body);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(cron = "0 00 18 * * *")
    public void sendRemindNotificationForUpcomingTrip() {
        findRemindTargetTrips().forEach(travel -> {
            List<Member> notifocationRecivers = findTravelRemindNotificationRecivers(travel);
            String title = travel.getTitle() + "여행 하루 전입니다!";
            String content = "여행 전까지 준비를 마무리하세요!";

            try {
                pushNotificationService.sendMessageTo(TRAVEL_REMIND, notifocationRecivers, title, content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<Travel> findRemindTargetTrips() {
        LocalDateTime startDay = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0, 0));
        LocalDateTime endDay = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(23, 59, 59));

        return travelRepository.findAllByCreatedAtBetween(startDay, endDay);
    }

    private List<Member> findInviteNotificationRecivers(Travel travel) {
        return travelMemberRepository.findByTravel(travel)
                .stream()
                .map(TravelMember::getMember)
                .filter(member -> pushNotificationService.isPushNotificationSubscriber(ACTIVE, member))
                .toList();
    }

    private List<Member> findTravelRemindNotificationRecivers(Travel travel) {
        return travelMemberRepository.findByTravel(travel)
                .stream()
                .map(TravelMember::getMember)
                .filter(member -> pushNotificationService.isPushNotificationSubscriber(TRAVEL_REMIND, member))
                .toList();
    }
}
