package site.packit.packit.domain.cluster.service;

import org.springframework.stereotype.Service;
import site.packit.packit.domain.category.repository.CategoryRepository;
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

import static site.packit.packit.domain.travel.exception.TravelErrorCode.NOT_MEMBER_IN;

@Service
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




    private void validateTravelMemberExists(Travel travel, Member member) {
        if (!travelMemberRepository.existsByTravelAndMember(travel, member)) {
            throw new ResourceNotFoundException(NOT_MEMBER_IN);
        }
    }
}
