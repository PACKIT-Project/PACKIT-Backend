package site.packit.packit.domain.travel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.category.repository.CategoryRepository;
import site.packit.packit.domain.item.repository.ItemRepository;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.repository.MemberRepository;
import site.packit.packit.domain.travel.dto.*;
import site.packit.packit.domain.destination.entity.Destination;
import site.packit.packit.domain.travel.entity.Travel;
import site.packit.packit.domain.destination.repository.DestinationRepository;
import site.packit.packit.domain.travel.entity.TravelMember;
import site.packit.packit.domain.travel.repository.TravelMemberRepository;
import site.packit.packit.domain.travel.repository.TravelRepository;

import java.security.SecureRandom;


@Service
@Transactional
public class TravelService {

    private final MemberRepository memberRepository;
    private final TravelMemberRepository travelMemberRepository;
    private final TravelRepository travelRepository;
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    private final DestinationRepository destinationRepository;

    public TravelService(MemberRepository memberRepository, TravelMemberRepository travelMemberRepository, TravelRepository travelRepository, ItemRepository itemRepository, CategoryRepository categoryRepository, DestinationRepository destinationRepository) {
        this.memberRepository = memberRepository;
        this.travelMemberRepository = travelMemberRepository;
        this.travelRepository = travelRepository;
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.destinationRepository = destinationRepository;
    }


    /**
     * 새로운 여행 생성
     */
    public Long createNewTravel(Long memberId, CreateTravelReq createTravelRequest) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        Destination destination = destinationRepository.findByIdOrThrow(createTravelRequest.destinationId());
        String invitationCode;
        do {
            invitationCode = generateRandomCode();
        } while (isCodeExists(invitationCode));

        Travel createTravel = Travel.builder()
                .title(createTravelRequest.title())
                .destination(destination)
                .startDate(createTravelRequest.startDate())
                .endDate(createTravelRequest.endDate())
                .owner(member)
                .invitationCode(invitationCode)
                .build();

        travelRepository.save(createTravel);
        addMemberToTravel(createTravel, member);

        // TODO: 기본 체크리스트 생성 코드

        return createTravel.getId();
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder codeBuilder = new StringBuilder(10);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    private boolean isCodeExists(String invitationCode) {
        return travelRepository.existsByInvitationCode(invitationCode);
    }

    public void addMemberToTravel(Travel travel, Member member) {
        TravelMember travelMember = TravelMember.builder()
                .travel(travel)
                .member(member)
                .build();

        travelMemberRepository.save(travelMember);
    }

}
