package site.packit.packit.domain.travel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.checkList.entity.CheckList;
import site.packit.packit.domain.checkList.repository.CheckListRepository;
import site.packit.packit.domain.item.entity.Item;
import site.packit.packit.domain.item.repository.ItemRepository;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.repository.MemberRepository;
import site.packit.packit.domain.storage.entity.Storage;
import site.packit.packit.domain.storage.repository.StorageRepository;
import site.packit.packit.domain.travel.dto.*;
import site.packit.packit.domain.destination.entity.Destination;
import site.packit.packit.domain.travel.entity.Travel;
import site.packit.packit.domain.destination.repository.DestinationRepository;
import site.packit.packit.domain.travel.repository.TravelRepository;
import site.packit.packit.global.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static site.packit.packit.domain.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;
import static site.packit.packit.domain.travel.exception.TravelErrorCode.TRAVEL_NOT_EDIT;
import static site.packit.packit.domain.travel.exception.TravelErrorCode.TRAVEL_NOT_FOUND;

@Service
public class TravelService {

    private final MemberRepository memberRepository;
    private final TravelRepository travelRepository;
    private final ItemRepository itemRepository;
    private final CheckListRepository checkListRepository;

    private final StorageRepository storageRepository;

    private final DestinationRepository destinationRepository;

    public TravelService(MemberRepository memberRepository, TravelRepository travelRepository, ItemRepository itemRepository, CheckListRepository checkListRepository, StorageRepository storageRepository, DestinationRepository destinationRepository) {
        this.memberRepository = memberRepository;
        this.travelRepository = travelRepository;
        this.itemRepository = itemRepository;
        this.checkListRepository = checkListRepository;
        this.storageRepository = storageRepository;
        this.destinationRepository = destinationRepository;
    }


    /**
     * 새로운 여행 생성
     */
    public Long createTravel(Long memberId, CreateTravelRequest createTravelRequest) {

        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException(MEMBER_NOT_FOUND);
        }

        Member member = memberRepository.findById(memberId).get();
        Destination destination = destinationRepository.findByIdOrThrow(createTravelRequest.destinationId());

        Travel createTravel = Travel.builder()
                .title(createTravelRequest.title())
                .destination(destination)
                .startDate(createTravelRequest.startDate())
                .endDate(createTravelRequest.endDate())
                .member(member)
                .build();

        travelRepository.save(createTravel);

        // TODO: 과거 버전의 해외&국내 기본 체크리스트 생성 코드
//        if (createTravelRequest.destinationType() == DestinationType.DOMESTIC) {
//            createDomesticDefaultChecklistsAndItems(createTravel);
//        } else if (createTravelRequest.destinationType() == DestinationType.OVERSEAS) {
//            createOverseasDefaultChecklistsAndItems(createTravel);
//        }
        return createTravel.getId();
    }



    /**
     * 여행 수정
     */
    @Transactional
    public void updateTravel(Long memberId, Long travelId, UpdateTravelRequest updateTravelRequest) {

        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException(TRAVEL_NOT_FOUND));

        if(!Objects.equals(travel.getMember().getId(), memberId)){
            throw new ResourceNotFoundException(TRAVEL_NOT_EDIT);
        }

        travel.updateTravel(updateTravelRequest.title(), updateTravelRequest.startDate(), updateTravelRequest.endDate());
    }

    /**
     * 여행 삭제
     */
    @Transactional
    public void deleteTravel(Long memberId, Long travelId) {

        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException(TRAVEL_NOT_FOUND));

        if(!Objects.equals(travel.getMember().getId(), memberId)){
            throw new ResourceNotFoundException(TRAVEL_NOT_EDIT);
        }

        List<CheckList> checkListsToDelete = checkListRepository.findByTravelId(travelId);
        for (CheckList checkList : checkListsToDelete) {
            // 해당 체크리스트에 딸린 아이템들 삭제
            List<Item> itemsToDelete = itemRepository.findByCheckListId(checkList.getId());
            itemRepository.deleteAll(itemsToDelete);
        }
        checkListRepository.deleteAll(checkListsToDelete);

        if(isAddedToStorage(travel.getId(), memberId)){
            Storage storage = storageRepository.findByMemberIdAndTravelId(memberId,travelId);
            storageRepository.delete(storage);
        }

        travelRepository.delete(travel);
    }

    /**
     * 예정된 여행 조회
     */
    @Transactional(readOnly = true)
    public List<TravelListDto> getUpcomingTravel(Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        List<Travel> upcomingTravels = travelRepository.findByStartDateAfterAndMemberIdOrderByStartDateAsc(now, memberId);

        return upcomingTravels.stream()
                .map(travel -> convertToDto(travel, isAddedToStorage(travel.getId(), memberId)))
                .collect(Collectors.toList());

    }

    // 디데이 계산
    private String calculateDDay(LocalDateTime startDate) {
        LocalDate today = LocalDate.now();
        LocalDate travelDate = startDate.toLocalDate();
        long daysUntilTravel = ChronoUnit.DAYS.between(today, travelDate);
        return daysUntilTravel >= 0 ? "D-" + daysUntilTravel : "D+" + Math.abs(daysUntilTravel);
    }

    /**
     * 지난 여행 조회
     */
    @Transactional(readOnly = true)
    public List<TravelListDto> getPastTravel(Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        List<Travel> pastTravels = travelRepository.findByStartDateBeforeAndMemberIdOrderByStartDateDesc(now, memberId);
        return pastTravels.stream()
                .map(travel -> convertToDto(travel, isAddedToStorage(travel.getId(), memberId)))
                .collect(Collectors.toList());
    }

    private TravelListDto convertToDto(Travel travel, boolean isAddedToStorage) {
        List<CheckList> checkLists = checkListRepository.findByTravelIdOrderByListOrderAsc(travel.getId());
        int unfinishedItemCount = 0;
        int finishedItemCount = 0;

        for (CheckList checkList : checkLists) {
            List<Item> items = itemRepository.findByCheckListId(checkList.getId());
            for (Item item : items) {
                if (item.getIsChecked()) {
                    finishedItemCount++;
                } else {
                    unfinishedItemCount++;
                }
            }
        }

        return new TravelListDto(
                travel.getId(),
                travel.getTitle(),
                calculateDDay(travel.getStartDate()),
                travel.getDestination().getCity(),
                travel.getStartDate(),
                travel.getEndDate(),
                isAddedToStorage,
                unfinishedItemCount,
                finishedItemCount
        );
    }

    // 보관함 유무 여부 판단
    private boolean isAddedToStorage(Long travelId, Long memberId) {
        return storageRepository.existsByMemberIdAndTravelId(memberId, travelId);
    }

    /**
     * 여행 상세 조회
     */
    public TravelDetailDto getDetailTravel(Long memberId, Long travelId) {
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException(TRAVEL_NOT_FOUND));

        List<CheckList> checkLists = checkListRepository.findByTravelIdOrderByListOrderAsc(travelId);

        List<CheckListDto> checkListDtoList = new ArrayList<>();
        for (CheckList checkList : checkLists) {
            List<ItemDto> itemDtoList = itemRepository.findByCheckListIdOrderByListOrderAsc(checkList.getId())
                    .stream()
                    .map(item -> new ItemDto(item.getId(), item.getTitle(), item.getListOrder(), item.getIsChecked()))
                    .collect(Collectors.toList());

            CheckListDto checkListDto = new CheckListDto(
                    checkList.getId(),
                    checkList.getTitle(),
                    checkList.getListOrder(),
                    checkList.getEssential(),
                    itemDtoList
            );

            checkListDtoList.add(checkListDto);
        }

        Boolean isInStorage = isAddedToStorage(travelId, memberId);

        return new TravelDetailDto(
                travel.getId(),
                travel.getTitle(),
                calculateDDay(travel.getStartDate()),
                travel.getDestination().getCity(),
                travel.getStartDate(),
                travel.getEndDate(),
                checkListDtoList,
                isInStorage
        );
    }

    /**
     * 여행 불러오기
     */
    public Long createBringTravel(Long travelId, BringTravelRequest bringTravelRequest, Long memberId) {
        Travel originalTravel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException(TRAVEL_NOT_FOUND));

        // 복사해서 새로운 여행 생성
        Travel newTravel = Travel.builder()
                .title(bringTravelRequest.title())
                .destination(originalTravel.getDestination())
                .startDate(bringTravelRequest.startDate())
                .endDate(bringTravelRequest.endDate())
                .member(originalTravel.getMember())
                .build();
        travelRepository.save(newTravel);

        // 체크리스트 및 아이템 복사
        List<CheckList> originalCheckLists = checkListRepository.findByTravelIdOrderByListOrderAsc(travelId);
        for (CheckList originalCheckList : originalCheckLists) {
            CheckList newCheckList = CheckList.builder()
                    .title(originalCheckList.getTitle())
                    .listOrder(originalCheckList.getListOrder())
                    .travel(newTravel)
                    .essential(originalCheckList.getEssential())
                    .build();
            checkListRepository.save(newCheckList);

            List<Item> originalItems = itemRepository.findByCheckListIdOrderByListOrderAsc(originalCheckList.getId());
            for (Item originalItem : originalItems) {
                Item newItem = Item.builder()
                        .title(originalItem.getTitle())
                        .listOrder(originalItem.getListOrder())
                        .isChecked(false) // 체크는 안 되어있게
                        .checkList(newCheckList)
                        .build();
                itemRepository.save(newItem);
            }
        }

        return newTravel.getId();
    }

    /**
     * 특정 사용자의 여행 개수 조회
     */
    public Integer getTravelCount(
            CustomUserPrincipal principal
    ) {
        return travelRepository.countAllByMember_Id(principal.getMemberId());
    }

    /**
     * 여행 하루 전 리마인드
     */
    public List<CheckListDto> getRemindCheckLists(
            Long travelId
    ){
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException(TRAVEL_NOT_FOUND));

        List<CheckList> checkLists = checkListRepository.findByTravelIdOrderByListOrderAsc(travelId);

        List<CheckListDto> checkListDtoList = new ArrayList<>();
        for (CheckList checkList : checkLists) {
            List<ItemDto> itemDtoList = itemRepository.findByCheckListIdOrderByListOrderAsc(checkList.getId())
                    .stream()
                    .map(item -> new ItemDto(item.getId(), item.getTitle(), item.getListOrder(), false))
                    .collect(Collectors.toList());

            CheckListDto checkListDto = new CheckListDto(
                    checkList.getId(),
                    checkList.getTitle(),
                    checkList.getListOrder(),
                    checkList.getEssential(),
                    itemDtoList
            );

            checkListDtoList.add(checkListDto);
        }

        return checkListDtoList;
    }


    /**
     * 새로운 여행 생성 - 기본 템플릿 생성
     */
    private void createDomesticDefaultChecklistsAndItems(Travel travel){
        CheckList essentialPacking = CheckList.builder()
                .title("필수 짐싸기")
                .listOrder(1)
                .travel(travel)
                .essential(true)
                .build();
        checkListRepository.save(essentialPacking);

        Item itemPacking1 = Item.builder()
                .title("카드 및 지갑")
                .listOrder(1)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking1);

        Item itemPacking2 = Item.builder()
                .title("충전기 및 보조배터리")
                .listOrder(2)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking2);

        Item itemPacking3 = Item.builder()
                .title("상의, 하의")
                .listOrder(3)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking3);

        Item itemPacking4 = Item.builder()
                .title("속옷 및 양말")
                .listOrder(4)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking4);

        Item itemPacking5 = Item.builder()
                .title("우천 용품")
                .listOrder(5)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking5);

        Item itemPacking6 = Item.builder()
                .title("세안 용품")
                .listOrder(6)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking6);

        Item itemPacking7 = Item.builder()
                .title("개인 위생 용품")
                .listOrder(7)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking7);

        CheckList essentialTodo = CheckList.builder()
                .title("필수 할 일")
                .listOrder(2)
                .travel(travel)
                .essential(true)
                .build();
        checkListRepository.save(essentialTodo);

        Item itemTodo1 = Item.builder()
                .title("이동 수단 예매")
                .listOrder(1)
                .isChecked(false)
                .checkList(essentialTodo)
                .build();
        itemRepository.save(itemTodo1);

        Item itemTodo2 = Item.builder()
                .title("숙소 예약")
                .listOrder(2)
                .isChecked(false)
                .checkList(essentialTodo)
                .build();
        itemRepository.save(itemTodo2);

        Item itemTodo3 = Item.builder()
                .title("숙소 비품 확인")
                .listOrder(3)
                .isChecked(false)
                .checkList(essentialTodo)
                .build();
        itemRepository.save(itemTodo3);

        Item itemTodo4 = Item.builder()
                .title("여행 경로 확인")
                .listOrder(4)
                .isChecked(false)
                .checkList(essentialTodo)
                .build();
        itemRepository.save(itemTodo4);

        Item itemTodo5 = Item.builder()
                .title("방문 장소 예약")
                .listOrder(5)
                .isChecked(false)
                .checkList(essentialTodo)
                .build();
        itemRepository.save(itemTodo5);

        Item itemTodo6 = Item.builder()
                .title("여행지 날씨 확인")
                .listOrder(6)
                .isChecked(false)
                .checkList(essentialTodo)
                .build();
        itemRepository.save(itemTodo6);

    }

    private void createOverseasDefaultChecklistsAndItems(Travel travel){

        CheckList essentialPacking = CheckList.builder()
                .title("필수 짐싸기")
                .listOrder(1)
                .travel(travel)
                .essential(true)
                .build();
        checkListRepository.save(essentialPacking);

        Item itemPacking1 = Item.builder()
                .title("여권, 신분증, 티켓")
                .listOrder(1)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking1);

        Item itemPacking2 = Item.builder()
                .title("현지 화폐, 카드 및 지갑")
                .listOrder(2)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking2);

        Item itemPacking3 = Item.builder()
                .title("구비 서류")
                .listOrder(3)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking3);

        Item itemPacking4 = Item.builder()
                .title("충전기 및 보조배터리")
                .listOrder(4)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking4);

        Item itemPacking5 = Item.builder()
                .title("상의, 하의")
                .listOrder(5)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking5);

        Item itemPacking6 = Item.builder()
                .title("상비약, 기타 의약품")
                .listOrder(6)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking6);

        Item itemPacking7 = Item.builder()
                .title("변압기")
                .listOrder(7)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking7);

        Item itemPacking8 = Item.builder()
                .title("우천 용품")
                .listOrder(8)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking8);

        Item itemPacking9 = Item.builder()
                .title("세안 용품")
                .listOrder(9)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking9);

        Item itemPacking10 = Item.builder()
                .title("개인 위생 용품")
                .listOrder(10)
                .isChecked(false)
                .checkList(essentialPacking)
                .build();
        itemRepository.save(itemPacking10);

        CheckList essentialTodo = CheckList.builder()
                .title("필수 할 일")
                .listOrder(2)
                .travel(travel)
                .essential(true)
                .build();
        checkListRepository.save(essentialTodo);

        Item itemTodo1 = Item.builder()
                .title("여권 일자 확인 및 갱신")
                .listOrder(1)
                .isChecked(false)
                .checkList(essentialTodo)
                .build();
        itemRepository.save(itemTodo1);

        Item itemTodo2 = Item.builder()
                .title("비행기 티켓 예매")
                .listOrder(2)
                .isChecked(false)
                .checkList(essentialTodo)
                .build();
        itemRepository.save(itemTodo2);

        Item itemTodo3 = Item.builder()
                .title("비행기 반입 수화물 확인")
                .listOrder(3)
                .isChecked(false)
                .checkList(essentialTodo)
                .build();
        itemRepository.save(itemTodo3);

        Item itemTodo4 = Item.builder()
                .title("숙소 예약")
                .listOrder(4)
                .isChecked(false)
                .checkList(essentialTodo)
                .build();
        itemRepository.save(itemTodo4);

        Item itemTodo5 = Item.builder()
                .title("숙소 비품 확인")
                .listOrder(5)
                .isChecked(false)
                .checkList(essentialTodo)
                .build();
        itemRepository.save(itemTodo5);

        Item itemTodo6 = Item.builder()
                .title("환전")
                .listOrder(6)
                .isChecked(false)
                .checkList(essentialTodo)
                .build();
        itemRepository.save(itemTodo6);

        Item itemTodo7 = Item.builder()
                .title("여행지 날씨 확인")
                .listOrder(7)
                .isChecked(false)
                .checkList(essentialTodo)
                .build();
        itemRepository.save(itemTodo7);

        Item itemTodo8 = Item.builder()
                .title("비자 및 구비서류 확인")
                .listOrder(8)
                .isChecked(false)
                .checkList(essentialTodo)
                .build();
        itemRepository.save(itemTodo8);

    }

}
