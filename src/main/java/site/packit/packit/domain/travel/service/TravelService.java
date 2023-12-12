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
import site.packit.packit.global.exception.ErrorCode;
import site.packit.packit.global.exception.ResourceNotFoundException;

import java.security.SecureRandom;

import static site.packit.packit.domain.travel.exception.TravelErrorCode.NOT_MEMBER_IN;


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

    /**
     * 현재 동행자 수 & 초대코드 확인
     */
    public TravelInviteRes getInvitationCode(Long memberId, Long travelId){
        Member member = memberRepository.findByIdOrThrow(memberId);
        Travel travel = travelRepository.findByIdOrThrow(travelId);
        validateTravelMemberExists(travel, member);

        String invitationCode = travel.getInvitationCode();
        long peopleNum = travelMemberRepository.countByTravel(travel);
        if(peopleNum>=8){ invitationCode = "최대 인원에 도달하였습니다."; }
        return new TravelInviteRes(peopleNum, invitationCode);
    }

    private void validateTravelMemberExists(Travel travel, Member member) {
        if (!travelMemberRepository.existsByTravelAndMember(travel, member)) {
            throw new ResourceNotFoundException(NOT_MEMBER_IN);
        }
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
