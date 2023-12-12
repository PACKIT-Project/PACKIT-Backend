package site.packit.packit.domain.travel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.category.entity.Category;
import site.packit.packit.domain.category.repository.CategoryRepository;
import site.packit.packit.domain.cluster.entity.Cluster;
import site.packit.packit.domain.cluster.repository.ClusterRepository;
import site.packit.packit.domain.item.entity.Item;
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
import site.packit.packit.global.exception.MaxParticipantsExceededException;
import site.packit.packit.global.exception.ResourceNotFoundException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static site.packit.packit.domain.travel.exception.TravelErrorCode.*;


@Service
@Transactional
public class TravelService {

    private final MemberRepository memberRepository;
    private final TravelMemberRepository travelMemberRepository;
    private final TravelRepository travelRepository;
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ClusterRepository clusterRepository;

    private final DestinationRepository destinationRepository;

    public TravelService(MemberRepository memberRepository, TravelMemberRepository travelMemberRepository, TravelRepository travelRepository, ItemRepository itemRepository, CategoryRepository categoryRepository, ClusterRepository clusterRepository, DestinationRepository destinationRepository) {
        this.memberRepository = memberRepository;
        this.travelMemberRepository = travelMemberRepository;
        this.travelRepository = travelRepository;
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.clusterRepository = clusterRepository;
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
    @Transactional(readOnly = true)
    public TravelInviteRes getInvitationCode(Long memberId, Long travelId){
        Member member = memberRepository.findByIdOrThrow(memberId);
        Travel travel = travelRepository.findByIdOrThrow(travelId);
        validateTravelMemberExists(travel, member);

        String invitationCode = travel.getInvitationCode();
        long peopleNum = travelMemberRepository.countByTravel(travel);
        if(peopleNum>=8){ invitationCode = "최대 인원에 도달하였습니다."; }
        return new TravelInviteRes(peopleNum, invitationCode);
    }

    /**
     * 동행자 추가 (초대코드 입력)
     */
    public Long invitationTravel(Long memberId, String invitationCode){
        Member member = memberRepository.findByIdOrThrow(memberId);
        Travel travel = travelRepository.findByInvitationCode(invitationCode)
                .orElseThrow(() -> new ResourceNotFoundException(INVITATION_NOT_FOUND));
        validateTravelMemberNotExists(travel, member);
        if(travelMemberRepository.countByTravel(travel) >= 8){
            throw new MaxParticipantsExceededException(MAX_PARTICIPANTS_EXCEEDED);
        }
        addMemberToTravel(travel, member);
        return travel.getId();
    }

    /**
     * 동행자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<TravelMemberRes> getTravelMemberList(Long memberId, Long travelId){
        Member member = memberRepository.findByIdOrThrow(memberId);
        Travel travel = travelRepository.findByIdOrThrow(travelId);
        List<TravelMember> travelMembers = travelMemberRepository.findByTravel(travel);

        // 자신의 프로필을 찾아서 me로 표시
        TravelMemberRes myProfile = travelMembers.stream()
                .filter(tm -> tm.getMember().equals(member))
                .findFirst()
                .map(tm -> new TravelMemberRes(
                        tm.getMember().getId(),
                        "me",
                        tm.getMember().getProfileImageUrl(),
                        calculateCheckedNum(tm.getTravel(), tm.getMember()),
                        calculateUncheckedNum(tm.getTravel(), tm.getMember())
                ))
                .orElseThrow(() -> new ResourceNotFoundException(NOT_MEMBER_IN));

        List<TravelMemberRes> otherProfiles = travelMembers.stream()
                .filter(tm -> !tm.getMember().equals(member))
                .map(tm -> new TravelMemberRes(
                        tm.getMember().getId(),
                        tm.getMember().getNickname(),
                        tm.getMember().getProfileImageUrl(),
                        calculateCheckedNum(tm.getTravel(), tm.getMember()),
                        calculateUncheckedNum(tm.getTravel(), tm.getMember())
                ))
                .collect(Collectors.toList());
        otherProfiles.add(0, myProfile);
        return otherProfiles;
    }

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    /**
     * 예정된/지난 여행 목록 조회
     */
    @Transactional(readOnly = true)
    public List<TravelListRes> getMyTravel(Long memberId, boolean upcoming) {
        List<TravelMember> travelMembers = travelMemberRepository.findByMemberId(memberId);

        List<TravelListRes> travelListResList = travelMembers.stream()
                .map(this::mapToTravelListRes)
                .collect(Collectors.toList());

        if (upcoming) {
            travelListResList = filterUpcomingTravels(travelListResList);
            travelListResList.sort(Comparator.comparingInt(travelListRes -> Integer.parseInt(travelListRes.dDay())));
        } else {
            travelListResList = filterPastTravels(travelListResList);
            travelListResList.sort(Comparator.comparingInt(travelListRes -> -Integer.parseInt(travelListRes.dDay())));
        }

        return travelListResList;
    }

    /**
     * 여행 리스트 상세 조회
     */
    @Transactional(readOnly = true)
    public TravelDetailRes getMyToDoList(Long memberId, Long travelId) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        Travel travel = travelRepository.findByIdOrThrow(travelId);
        validateTravelMemberExists(travel, member);

        String formattedStartDate = formatLocalDateTime(travel.getStartDate());
        String formattedEndDate = formatLocalDateTime(travel.getEndDate());
        List<TravelCluster> travelClusters = clusterRepository.findByTravelAndMember(travel, member).stream()
                .map(this::mapToTravelClusterList)
                .sorted(Comparator.comparingInt(TravelCluster::order))
                .collect(Collectors.toList());

        return new TravelDetailRes(
                travel.getId(),
                travel.getTitle(),
                calculateRemainingDays(travel.getEndDate()),
                travel.getDestination().getCity(),
                formattedStartDate,
                formattedEndDate,
                travelMemberRepository.countByTravel(travel),
                travelClusters
        );
    }

    @Transactional(readOnly = true)
    public TravelDetailRes getToDoList(Long myId, Long memberId, Long travelId) {
        Member me = memberRepository.findByIdOrThrow(myId);
        Member member = memberRepository.findByIdOrThrow(memberId);
        Travel travel = travelRepository.findByIdOrThrow(travelId);
        validateTravelMemberExists(travel, me);
        validateTravelMemberExists(travel, member);

        String formattedStartDate = formatLocalDateTime(travel.getStartDate());
        String formattedEndDate = formatLocalDateTime(travel.getEndDate());
        List<TravelCluster> travelClusters = clusterRepository.findByTravelAndMember(travel, member).stream()
                .map(this::mapToTravelClusterList)
                .sorted(Comparator.comparingInt(TravelCluster::order))
                .collect(Collectors.toList());

        return new TravelDetailRes(
                travel.getId(),
                travel.getTitle(),
                calculateRemainingDays(travel.getEndDate()),
                travel.getDestination().getCity(),
                formattedStartDate,
                formattedEndDate,
                travelMemberRepository.countByTravel(travel),
                travelClusters
        );
    }

    private TravelCluster mapToTravelClusterList(Cluster cluster) {
        List<TravelCategory> travelCategories = categoryRepository.findByCluster(cluster).stream()
                .map(this::mapToTravelCategoryList)
                .sorted(Comparator.comparingInt(TravelCategory::order))
                .collect(Collectors.toList());

        return new TravelCluster(
                cluster.getId(),
                cluster.getTitle(),
                cluster.getListOrder(),
                travelCategories
        );
    }

    private TravelCategory mapToTravelCategoryList(Category category) {
        List<TravelItem> travelItems = itemRepository.findByCategory(category).stream()
                .map(this::mapToTravelItemList)
                .sorted(Comparator.comparingInt(TravelItem::order))
                .collect(Collectors.toList());

        return new TravelCategory(
                category.getId(),
                category.getTitle(),
                category.getListOrder(),
                travelItems
        );
    }

    private TravelItem mapToTravelItemList(Item item) {
        return new TravelItem(
                item.getId(),
                item.getTitle(),
                item.getListOrder(),
                item.isChecked()
        );
    }


    private List<TravelListRes> filterUpcomingTravels(List<TravelListRes> travelListResList) {
        return travelListResList.stream()
                .filter(travelListRes -> Integer.parseInt(travelListRes.dDay()) > 0)
                .collect(Collectors.toList());
    }

    private List<TravelListRes> filterPastTravels(List<TravelListRes> travelListResList) {
        return travelListResList.stream()
                .filter(travelListRes -> Integer.parseInt(travelListRes.dDay()) <= 0)
                .collect(Collectors.toList());
    }

    private TravelListRes mapToTravelListRes(TravelMember travelMember) {
        Travel travel = travelMember.getTravel();
        long memberNum = travelMemberRepository.countByTravel(travel);
        int remainingDays = calculateRemainingDays(travel.getStartDate());

        String formattedStartDate = formatLocalDateTime(travel.getStartDate());
        String formattedEndDate = formatLocalDateTime(travel.getEndDate());

        return new TravelListRes(
                travel.getId(),
                travel.getTitle(),
                travel.getDestination().getCity(),
                formattedStartDate,
                formattedEndDate,
                String.valueOf(remainingDays),
                memberNum
        );
    }

    private String formatLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(dateFormatter);
    }

    private int calculateRemainingDays(LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        return (int) ChronoUnit.DAYS.between(now, endDate);
    }

    private int calculateCheckedNum(Travel travel, Member member) {
        int checkedNum = 0;
        for (Cluster cluster : clusterRepository.findByTravelAndMember(travel, member)) {
            for (Category category : categoryRepository.findByCluster(cluster)) {
                for (Item item : itemRepository.findByCategory(category)) {
                    if (item.isChecked()) {
                        checkedNum++;
                    }
                }
            }
        }
        return checkedNum;
    }

    private int calculateUncheckedNum(Travel travel, Member member) {
        int uncheckedNum = 0;
        for (Cluster cluster : clusterRepository.findByTravelAndMember(travel, member)) {
            for (Category category : categoryRepository.findByCluster(cluster)) {
                for (Item item : itemRepository.findByCategory(category)) {
                    if (!item.isChecked()) {
                        uncheckedNum++;
                    }
                }
            }
        }
        return uncheckedNum;
    }


    private void validateTravelMemberExists(Travel travel, Member member) {
        if (!travelMemberRepository.existsByTravelAndMember(travel, member)) {
            throw new ResourceNotFoundException(NOT_MEMBER_IN);
        }
    }

    private void validateTravelMemberNotExists(Travel travel, Member member) {
        if (travelMemberRepository.existsByTravelAndMember(travel, member)) {
            throw new ResourceNotFoundException(EXISTS_MEMBER_IN);
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
