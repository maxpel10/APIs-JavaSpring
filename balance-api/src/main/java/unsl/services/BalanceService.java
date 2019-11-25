package unsl.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unsl.entities.Balance;
import unsl.entities.User;
import unsl.repository.BalanceRepository;
import unsl.utils.RestService;

import java.util.List;

@Service
public class BalanceService {
    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    RestService restService;

    public List<Balance> getAll(){
        return balanceRepository.findAll();
    }

    public Balance getBalance(Long balanceId) {
        return balanceRepository.findById(balanceId).orElse(null);
    }

    public Balance saveBalance(Balance balance) {
        return balanceRepository.save(balance);
    }

    public Balance updateBalance(Long balanceId, Balance updatedBalance){
        Balance balance = balanceRepository.findById(balanceId).orElse(null);;
        if (balance ==  null){
            return null;
        }
        balance.setSaldo(updatedBalance.getSaldo());
        return balanceRepository.save(balance);
    }

    public Balance deleteBalance(Long balanceId) {
        Balance balance = balanceRepository.findById(balanceId).orElse(null);;
        if (balance ==  null){
            return null;
        }
        else{
            balance.setEstado(Balance.Status.BAJA);
            return balanceRepository.save(balance);
        }

    }

    public User getUser(Long id) throws Exception {
        return restService.getUser((String.format("http://localhost:8888/clientes/%d",id)));
    }

    public List<Balance> findByUser(Long idUser) {
        return balanceRepository.findByTitular(idUser);
    }
}
