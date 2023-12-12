package site.packit.packit.domain.destination.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.packit.packit.domain.destination.dto.DestinationDto;
import site.packit.packit.domain.destination.entity.Destination;
import site.packit.packit.domain.destination.repository.DestinationRepository;
import site.packit.packit.domain.member.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DestinationService {
    private final DestinationRepository destinationRepository;
    private final MemberRepository memberRepository;

    public DestinationService(DestinationRepository destinationRepository, MemberRepository memberRepository) {
        this.destinationRepository = destinationRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public List<DestinationDto> searchDestination(Long memberId, String keyword){
        memberRepository.findByIdOrThrow(memberId);
        List<Destination> findDestinations = destinationRepository.findByCityContainingOrCountryContaining(keyword, keyword);
        return findDestinations.stream()
                .map(destination -> new DestinationDto(destination.getId(), destination.getCountry(), destination.getCity()))
                .collect(Collectors.toList());
    }
}
