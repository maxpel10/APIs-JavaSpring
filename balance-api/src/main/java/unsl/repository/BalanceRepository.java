package unsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import unsl.entities.Balance;

import java.util.List;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    List<Balance> findByTitular(Long titular);
}
