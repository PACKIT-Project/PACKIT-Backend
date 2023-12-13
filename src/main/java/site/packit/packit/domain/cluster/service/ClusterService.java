package site.packit.packit.domain.cluster.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.category.repository.CategoryRepository;
import site.packit.packit.domain.cluster.dto.ClusterOrderReq;
import site.packit.packit.domain.cluster.dto.CreateClusterReq;
import site.packit.packit.domain.cluster.entity.Cluster;
import site.packit.packit.domain.cluster.repository.ClusterRepository;
import site.packit.packit.domain.item.repository.ItemRepository;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.repository.MemberRepository;
import site.packit.packit.domain.travel.entity.Travel;
import site.packit.packit.domain.travel.repository.TravelMemberRepository;
import site.packit.packit.domain.travel.repository.TravelRepository;
import site.packit.packit.global.exception.ResourceNotFoundException;

import java.util.List;

import static site.packit.packit.domain.cluster.execption.ClusterErrorCode.CLUSTER_NOT_FOUND;
import static site.packit.packit.domain.travel.exception.TravelErrorCode.NOT_MEMBER_IN;

@Service
@Transactional
public class ClusterService {

    private final MemberRepository memberRepository;
    private final TravelMemberRepository travelMemberRepository;
    private final TravelRepository travelRepository;
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ClusterRepository clusterRepository;

    public ClusterService(MemberRepository memberRepository, TravelMemberRepository travelMemberRepository, TravelRepository travelRepository, ItemRepository itemRepository, CategoryRepository categoryRepository, ClusterRepository clusterRepository) {
        this.memberRepository = memberRepository;
        this.travelMemberRepository = travelMemberRepository;
        this.travelRepository = travelRepository;
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.clusterRepository = clusterRepository;
    }

    /**
     * 할 일 그룹 생성
     */
    public Long createNewCluster(Long memberId, CreateClusterReq createClusterReq){
        Member member = memberRepository.findByIdOrThrow(memberId);
        Travel travel = travelRepository.findByIdOrThrow(createClusterReq.travelId());
        validateTravelMemberExists(travel, member);

        int maxListOrder = clusterRepository.findMaxListOrderByTravelAndMember(travel, member)
                .orElse(1); // Default to 1
        Cluster newCluster = Cluster.builder()
                .title(createClusterReq.title())
                .listOrder(maxListOrder + 1)
                .travel(travel)
                .member(member)
                .build();

        Cluster savedCluster = clusterRepository.save(newCluster);
        return savedCluster.getId();
    }

    /**
     * 할 일 그룹 순서 변경
     */
    public void updateClusterOrder(Long memberId, ClusterOrderReq clusterOrderReq){
        Member member = memberRepository.findByIdOrThrow(memberId);
        Cluster clusterToUpdate = clusterRepository.findByIdAndMember(clusterOrderReq.clusterId(), member)
                .orElseThrow(() -> new ResourceNotFoundException(CLUSTER_NOT_FOUND));

        int newOrder = clusterOrderReq.newOrder();
        int currentOrder = clusterToUpdate.getListOrder();
        if (newOrder == currentOrder) {return;}

        List<Cluster> clusters = clusterRepository.findByTravelAndMember(clusterToUpdate.getTravel(), member);
        clusterToUpdate.setListOrder(newOrder);
        if (newOrder < currentOrder) {
            clusters.stream()
                    .filter(c -> c != clusterToUpdate && c.getListOrder() >= newOrder && c.getListOrder() < currentOrder)
                    .forEach(c -> c.setListOrder(c.getListOrder() + 1));
        } else {
            clusters.stream()
                    .filter(c -> c != clusterToUpdate && c.getListOrder() <= newOrder && c.getListOrder() > currentOrder)
                    .forEach(c -> c.setListOrder(c.getListOrder() - 1));
        }
    }



    private void validateTravelMemberExists(Travel travel, Member member) {
        if (!travelMemberRepository.existsByTravelAndMember(travel, member)) {
            throw new ResourceNotFoundException(NOT_MEMBER_IN);
        }
    }
}
