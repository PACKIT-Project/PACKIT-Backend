package site.packit.packit.domain.category.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.category.dto.CreateCategoryReq;
import site.packit.packit.domain.category.dto.UpdateCategoryReq;
import site.packit.packit.domain.category.entity.Category;
import site.packit.packit.domain.category.repository.CategoryRepository;
import site.packit.packit.domain.cluster.entity.Cluster;
import site.packit.packit.domain.cluster.repository.ClusterRepository;
import site.packit.packit.domain.item.entity.Item;
import site.packit.packit.domain.item.repository.ItemRepository;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.repository.MemberRepository;
import site.packit.packit.global.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static site.packit.packit.domain.cluster.execption.ClusterErrorCode.CATEGORY_NOT_EDIT;
import static site.packit.packit.domain.cluster.execption.ClusterErrorCode.CLUSTER_NOT_FOUND;

@Service
@Transactional
public class CategoryService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ClusterRepository clusterRepository;

    public CategoryService(MemberRepository memberRepository, ItemRepository itemRepository, CategoryRepository categoryRepository, ClusterRepository clusterRepository) {
        this.memberRepository = memberRepository;
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.clusterRepository = clusterRepository;
    }


    /**
     * 할 일 생성
     */
    public Long createNewCategory(Long memberId, CreateCategoryReq createCategoryReq){
        Member member = memberRepository.findByIdOrThrow(memberId);
        Cluster cluster = clusterRepository.findByIdAndMember(createCategoryReq.clusterId(), member)
                .orElseThrow(() -> new ResourceNotFoundException(CLUSTER_NOT_FOUND));
        int maxListOrder = categoryRepository.findMaxListOrderByCluster(cluster)
                .orElse(1);

        Category newCategory = Category.builder()
                .title(createCategoryReq.title())
                .listOrder(maxListOrder + 1)
                .cluster(cluster)
                .build();

        Category savedCategory = categoryRepository.save(newCategory);
        cluster.addCategory(savedCategory);

        return savedCategory.getId();
    }

    /**
     * 할 일 수정
     */
    public void updateCategoryTitle(Long memberId, UpdateCategoryReq updateCategoryReq){
        Member member = memberRepository.findByIdOrThrow(memberId);
        Category category = categoryRepository.findByIdOrThrow(updateCategoryReq.categoryId());
        if (!category.getCluster().getMember().equals(member)) {
            throw new ResourceNotFoundException(CATEGORY_NOT_EDIT);
        }

        category.updateCategoryTitle(updateCategoryReq.title());
        categoryRepository.save(category);
    }

    /**
     * 할 일 삭제
     */
    public void deleteCategory(Long memberId, Long categoryId){
        Member member = memberRepository.findByIdOrThrow(memberId);
        Category category = categoryRepository.findByIdOrThrow(categoryId);
        if (!category.getCluster().getMember().equals(member)) {
            throw new ResourceNotFoundException(CATEGORY_NOT_EDIT);
        }
        List<Item> items = category.getItems();
        if (items != null && !items.isEmpty()) {
            itemRepository.deleteAll(items);
        }
        categoryRepository.delete(category);
    }


}
