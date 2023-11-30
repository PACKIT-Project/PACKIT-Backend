package site.packit.packit.domain.checkList.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.checkList.dto.CreateCheckListRequest;
import site.packit.packit.domain.checkList.dto.UpdateCheckListRequest;
import site.packit.packit.domain.checkList.dto.UpdateListTitleRequest;
import site.packit.packit.domain.checkList.entity.CheckList;
import site.packit.packit.domain.checkList.repository.CheckListRepository;
import site.packit.packit.domain.item.entity.Item;
import site.packit.packit.domain.item.repository.ItemRepository;
import site.packit.packit.domain.travel.entity.Travel;
import site.packit.packit.domain.travel.repository.TravelRepository;
import site.packit.packit.global.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Objects;

import static site.packit.packit.domain.checkList.excepiton.CheckListErrorCode.*;
import static site.packit.packit.domain.travel.exception.TravelErrorCode.TRAVEL_NOT_FOUND;

@Service
public class CheckListService {

    private final CheckListRepository checkListRepository;
    private final TravelRepository travelRepository;

    private final ItemRepository itemRepository;

    public CheckListService(CheckListRepository checkListRepository, TravelRepository travelRepository, ItemRepository itemRepository) {
        this.checkListRepository = checkListRepository;
        this.travelRepository = travelRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * 새로운 체크리스트 생성
     */
    @Transactional
    public Long createCheckList(Long travelId, CreateCheckListRequest createCheckListRequest, Long memberId){

        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException(TRAVEL_NOT_FOUND));

        if(!Objects.equals(travel.getMember().getId(), memberId)){
            throw new ResourceNotFoundException(CHECKLIST_NOT_EDIT);
        }

        // travel에 속한 체크리스트 중 가장 큰 listOrder 값을 찾기
        Integer maxListOrder = checkListRepository.findMaxListOrderByTravel(travel);

        if (maxListOrder == null) {
            maxListOrder = 0;
        }

        CheckList newCheckList = CheckList.builder()
                .title(createCheckListRequest.title())
                .listOrder(maxListOrder+1)
                .travel(travel)
                .essential(false)
                .build();

        checkListRepository.save(newCheckList);

        return newCheckList.getId();
    }


    /**
     * 체크리스트 순서 수정
     */
    @Transactional
    public void updateCheckListOrder(Long travelId, List<UpdateCheckListRequest> updateCheckListRequests, Long memberId){

        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException(TRAVEL_NOT_FOUND));

        if(!Objects.equals(travel.getMember().getId(), memberId)){
            throw new ResourceNotFoundException(CHECKLIST_NOT_EDIT);
        }

        // travelId로 해당 여행의 체크리스트들을 가져오기
        List<CheckList> checkLists = checkListRepository.findByTravelId(travelId);

        // 요청으로 받은 순서대로 체크리스트를 업데이트
        for (UpdateCheckListRequest request : updateCheckListRequests) {
            for (CheckList checkList : checkLists) {
                if (checkList.getId().equals(request.id())) {
                    checkList.setListOrder(request.order());
                    break;
                }
            }
        }

        checkListRepository.saveAll(checkLists);
    }


    /**
     * 체크리스트 삭제
     */
    @Transactional
    public void deleteCheckListAndReorder(Long travelId, Long checklistId, Long memberId) {

        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException(TRAVEL_NOT_FOUND));

        CheckList deletedCheckList = checkListRepository.findById(checklistId)
                .orElseThrow(() -> new ResourceNotFoundException(CHECKLIST_NOT_FOUND));

        if(!Objects.equals(travel.getMember().getId(), memberId)){
            throw new ResourceNotFoundException(CHECKLIST_NOT_DELETE);
        }

        // 삭제할 체크리스트의 order 가져오기
        int deletedOrder = deletedCheckList.getListOrder();

        // 삭제할 체크리스트보다 order가 큰 체크리스트들 조회
        List<CheckList> checkListsToUpdate = checkListRepository.findByTravelAndListOrderGreaterThan(
                deletedCheckList.getTravel(), deletedOrder);

        // 조회된 체크리스트들의 order를 1씩 감소시키고 저장
        for (CheckList checkList : checkListsToUpdate){
            checkList.setListOrder(checkList.getListOrder() - 1);
            checkListRepository.save(checkList);
        }

        // 삭제할 체크리스트에 딸린 아이템들 삭제
        List<Item> itemsToDelete = itemRepository.findByCheckListId(checklistId);
        itemRepository.deleteAll(itemsToDelete);

        // 체크리스트 삭제
        checkListRepository.delete(deletedCheckList);

        // 체크리스트 삭제
        checkListRepository.delete(deletedCheckList);
    }

    /**
     * 체크리스트 항목 수정
     */
    @Transactional
    public void updateCheckListTitle(Long travelId, Long checkListId, UpdateListTitleRequest updateListTitleRequest, Long memberId){

        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException(TRAVEL_NOT_FOUND));

        CheckList checkList = checkListRepository.findById(checkListId)
                .orElseThrow(()-> new ResourceNotFoundException(CHECKLIST_NOT_FOUND));

        if(!Objects.equals(travel.getMember().getId(), memberId)){
            throw new ResourceNotFoundException(CHECKLIST_NOT_EDIT);
        }

        checkList.updateListTitle(updateListTitleRequest.title());

    }
}
