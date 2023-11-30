package site.packit.packit.domain.notification.service;//package site.packit.packit.domain.notification.service;
//
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import site.packit.packit.domain.email.dto.SendEmailDto;
//import site.packit.packit.domain.email.service.EmailService;
//import site.packit.packit.domain.member.entity.Member;
//import site.packit.packit.domain.travel.entity.Travel;
//import site.packit.packit.domain.travel.repository.TravelRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class TravelNotificationService {
//
//    private final TravelRepository travelRepository;
//    private final EmailService emailService;
//
//    private static final String FROM_EMAIL = "packit0807@gmail.com";
//    private static final String FROM_NAME = "pack-it";
//
//    public TravelNotificationService(
//            TravelRepository travelRepository,
//            EmailService emailService
//    ) {
//        this.travelRepository = travelRepository;
//        this.emailService = emailService;
//    }
//
//    // 매일 오후 6시 remind mail 전송
//    @Scheduled(cron = "0 00 18 * * *")
//    @Transactional
//    public void sendRemindNotificationForUpcomingTrip() {
//        findRemindTargetTrips()
//                .forEach(travel -> {
//                    Member member = travel.getMember();
//                    String mailTitle = createMailTitle(member.getNickname(), travel.getTitle());
//                    String emailContent = createEmailContent("https://remind-travel/" + travel.getTitle());
//
//                    try {
//                        emailService.sendEmail(
//                                SendEmailDto.of(
//                                        mailTitle,
//                                        emailContent,
//                                        member.getEmail(),
//                                        FROM_EMAIL,
//                                        FROM_NAME
//                                )
//                        );
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//    }
//
//    private List<Travel> findRemindTargetTrips() {
//        LocalDateTime checkDate = LocalDateTime
//                .now()
//                .plusDays(1)
//                .withHour(0)
//                .withMinute(0)
//                .withSecond(0)
//                .withNano(0);
//
//        return travelRepository.findAllByStartDateAndMember_IsEmailAuthorized(checkDate, true);
//    }
//
//    private String createMailTitle(
//            String nickname,
//            String travelTitle
//    ) {
//        String emailTitle =
//                "[Pack-IT] " +
//                        "안녕하세요! " + nickname + "님! " +
//                        "오늘은 " + travelTitle + " 하루 전 입니다!";
//
//        return emailTitle;
//    }
//
//    private String createEmailContent(String travelLink) {
//        String emailContent = "";
//        emailContent += "<div style='margin:20px;'>";
//        emailContent += "<h1> 즐거운 여행을 위해 다시 한번 여행 일정을 체크하시면 어떨까요! </h1>";
//        emailContent += "<br>";
//        emailContent += "<p>아래 링크를 클릭해주세요<p>";
//        emailContent += "<br>";
//        emailContent += "<p>감사합니다.<p>";
//        emailContent += "<br>";
//        emailContent += "<div align='center' style='border:1px solid black; font-family:verdana';>";
//        emailContent += "<div style='font-size:130%'>";
//        emailContent += travelLink + "</strong><div><br/> ";
//        emailContent += "</div>";
//
//        return emailContent;
//    }
//}
