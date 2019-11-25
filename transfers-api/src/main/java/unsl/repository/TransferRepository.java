package unsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unsl.entities.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

}
