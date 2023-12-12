package site.packit.packit.domain.destination.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.destination.entity.Destination;
import site.packit.packit.global.exception.ResourceNotFoundException;

import static site.packit.packit.domain.travel.exception.TravelErrorCode.DESTINATION_NOT_FOUND;

public interface DestinationRepository
        extends JpaRepository<Destination, Long> {

    default Destination findByIdOrThrow(Long destinationId){
        return findById(destinationId)
                .orElseThrow(()-> new ResourceNotFoundException(DESTINATION_NOT_FOUND));
    }
}
