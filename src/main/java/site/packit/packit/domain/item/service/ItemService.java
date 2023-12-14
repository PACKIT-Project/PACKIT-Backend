package site.packit.packit.domain.item.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.category.entity.Category;
import site.packit.packit.domain.category.repository.CategoryRepository;
import site.packit.packit.domain.item.dto.CreateItemReq;
import site.packit.packit.domain.item.entity.Item;
import site.packit.packit.domain.item.repository.ItemRepository;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.repository.MemberRepository;
import site.packit.packit.domain.travel.repository.TravelRepository;
import site.packit.packit.global.exception.ResourceNotFoundException;

import static site.packit.packit.domain.cluster.execption.ClusterErrorCode.CATEGORY_NOT_EDIT;
import static site.packit.packit.domain.item.exception.ItemErrorCode.ITEM_NOT_EDIT;

@Service
@Transactional
public class ItemService {
    private final CategoryRepository categoryRepository;
    private final TravelRepository travelRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    public ItemService(CategoryRepository categoryRepository, TravelRepository travelRepository, ItemRepository itemRepository, MemberRepository memberRepository) {
        this.categoryRepository = categoryRepository;
        this.travelRepository = travelRepository;
        this.itemRepository = itemRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * 할 일 아이템 생성
     */
    public Long createNewItem(Long memberId, CreateItemReq createItemReq){
        Member member = memberRepository.findByIdOrThrow(memberId);
        Category category = categoryRepository.findByIdOrThrow(createItemReq.categoryId());
        if (!category.getCluster().getMember().equals(member)) {
            throw new ResourceNotFoundException(ITEM_NOT_EDIT);
        }
        int maxListOrder = itemRepository.findMaxListOrderByCategory(category)
                .orElse(1);
        Item item = Item.builder()
                .title(createItemReq.title())
                .listOrder(maxListOrder + 1)
                .isChecked(false)
                .category(category)
                .build();

        Item savedItem = itemRepository.save(item);
        category.addItem(savedItem);

        return savedItem.getId();
    }


}
