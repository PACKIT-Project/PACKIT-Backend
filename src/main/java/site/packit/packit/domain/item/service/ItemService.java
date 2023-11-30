package site.packit.packit.domain.item.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.checkList.entity.CheckList;
import site.packit.packit.domain.checkList.repository.CheckListRepository;
import site.packit.packit.domain.item.dto.CreateItemRequest;
import site.packit.packit.domain.item.dto.UpdateItemRequest;
import site.packit.packit.domain.item.dto.UpdateItemTitleRequest;
import site.packit.packit.domain.item.entity.Item;
import site.packit.packit.domain.item.repository.ItemRepository;
import site.packit.packit.domain.travel.entity.Travel;
import site.packit.packit.domain.travel.repository.TravelRepository;
import site.packit.packit.global.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Objects;

import static site.packit.packit.domain.checkList.excepiton.CheckListErrorCode.CHECKLIST_NOT_FOUND;
import static site.packit.packit.domain.item.exception.ItemErrorCode.*;
import static site.packit.packit.domain.travel.exception.TravelErrorCode.TRAVEL_NOT_FOUND;

@Service
public class ItemService {
    private final CheckListRepository checkListRepository;
    private final TravelRepository travelRepository;
    private final ItemRepository itemRepository;

    public ItemService(CheckListRepository checkListRepository, TravelRepository travelRepository, ItemRepository itemRepository) {
        this.checkListRepository = checkListRepository;
        this.travelRepository = travelRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * 새로운 체크리스트 아이템 생성
     */
    @Transactional
    public Long createCheckList(Long travelId, Long checklistId, CreateItemRequest createItemRequest, Long memberId) {

        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException(TRAVEL_NOT_FOUND));

        CheckList checkList = checkListRepository.findById(checklistId)
                .orElseThrow(() -> new ResourceNotFoundException(CHECKLIST_NOT_FOUND));

        if(!Objects.equals(travel.getMember().getId(), memberId)){
            throw new ResourceNotFoundException(ITEM_NOT_EDIT);
        }

        // 체크리스트에 속한 아이템 중 가장 큰 listOrder 값을 찾기
        Integer maxListOrder = itemRepository.findMaxListOrderByCheckList(checkList);

        if (maxListOrder == null) {
            maxListOrder = 0;
        }

        Item newitem = Item.builder()
                .title(createItemRequest.title())
                .listOrder(maxListOrder + 1)
                .isChecked(false)
                .checkList(checkList)
                .build();

        itemRepository.save(newitem);
        return newitem.getId();

    }


    /**
     * 체크리스트 아이템 순서 수정
     */
    @Transactional
    public void updateItemOrder(Long travelId, Long checklistId, List<UpdateItemRequest> updateItemRequests, Long memberId) {

        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException(TRAVEL_NOT_FOUND));

        CheckList checkList = checkListRepository.findById(checklistId)
                .orElseThrow(() -> new ResourceNotFoundException(CHECKLIST_NOT_FOUND));

        if(!Objects.equals(travel.getMember().getId(), memberId)){
            throw new ResourceNotFoundException(ITEM_NOT_EDIT);
        }

        // checklistId로 해당 여행의 아이템들을 가져오기
        List<Item> items = itemRepository.findByCheckListId(checklistId);

        // 요청으로 받은 순서대로 체크리스트를 업데이트
        for (UpdateItemRequest request : updateItemRequests) {
            for (Item item : items) {
                if (item.getId().equals(request.id())) {
                    item.setListOrder(request.order());
                    break;
                }
            }
        }

        itemRepository.saveAll(items);
    }

    /**
     * 체크리스트 아이템 삭제
     */
    @Transactional
    public void deleteItemAndReorder(Long travelId, Long checklistId, Long itemId, Long memberId){

        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException(TRAVEL_NOT_FOUND));

        CheckList checkList = checkListRepository.findById(checklistId)
                .orElseThrow(() -> new ResourceNotFoundException(CHECKLIST_NOT_FOUND));

        Item deletedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(ITEM_NOT_FOUND));

        if(!Objects.equals(travel.getMember().getId(), memberId)){
            throw new ResourceNotFoundException(ITEM_NOT_DELETE);
        }

        // 삭제할 아이템의 order 가져오기
        int deletedOrder = deletedItem.getListOrder();

        // 삭제할 아이템보다 order가 큰 아이템들 조회
        List<Item> itemsToUpdate = itemRepository.findByCheckListAndListOrderGreaterThan(
                deletedItem.getCheckList(), deletedOrder
        );

        // 조회된 아이템들의 order를 1씩 감소시키고 저장
        for (Item item : itemsToUpdate){
            item.setListOrder(item.getListOrder() - 1);
            itemRepository.save(item);
        }

        // 아이템 삭제
        itemRepository.delete(deletedItem);
    }


    /**
     * 체크리스트 아이템 체크/체크 취소
     */
    @Transactional
    public void checkItem(Long travelId, Long checkListId, Long itemId, Long memberId) {

        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException(TRAVEL_NOT_FOUND));
        CheckList checkList = checkListRepository.findById(checkListId)
                .orElseThrow(() -> new ResourceNotFoundException(CHECKLIST_NOT_FOUND));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(ITEM_NOT_FOUND));

        if(!Objects.equals(travel.getMember().getId(), memberId)){
            throw new ResourceNotFoundException(ITEM_NOT_EDIT);
        }

        // 아이템의 isChecked 속성 변경
        item.toggleChecked();

        itemRepository.save(item);
    }

    /**
     * 체크리스트 아이템 항목 수정
     */
    @Transactional
    public void updateItemTitle(Long travelId, Long checkListId, Long itemId, Long memberId, UpdateItemTitleRequest updateItemTitleRequest) {

        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException(TRAVEL_NOT_FOUND));
        CheckList checkList = checkListRepository.findById(checkListId)
                .orElseThrow(() -> new ResourceNotFoundException(CHECKLIST_NOT_FOUND));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(ITEM_NOT_FOUND));

        if(!Objects.equals(travel.getMember().getId(), memberId)){
            throw new ResourceNotFoundException(ITEM_NOT_EDIT);
        }

        item.updateItemTitle(updateItemTitleRequest.title());



    }
}
