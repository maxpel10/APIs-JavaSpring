package unsl.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unsl.entities.Balance;
import unsl.entities.Transfer;
import unsl.repository.TransferRepository;
import unsl.utils.RestService;

import java.util.List;

@Service
public class TransferService {
    @Autowired
    TransferRepository transferRepository;

    @Autowired
    RestService restService;

    public List<Transfer> getAll() {
        return transferRepository.findAll();
    }

    public Transfer getTransfer(Long transferId) {
        return transferRepository.findById(transferId).orElse(null);
    }

    public Transfer saveTransfer(Transfer transfer) {
        Balance balanceOrigen = getBalance(transfer.getId_cuenta_origen());
        Balance balance = null;
        try {
            restService.modifyBalance("http://localhost:8887/cuentas/" + balanceOrigen.getId(), balanceOrigen.getSaldo() - transfer.getMonto());
        }catch (Exception e){
            return null;
        }
        return transferRepository.save(transfer);
    }

    public Transfer processTransfer(Long transferId){
        Transfer transfer = transferRepository.findById(transferId).orElse(null);

        transfer.setEstado(Transfer.Status.PROCESADA);


        Balance balanceDestino = getBalance(transfer.getId_cuenta_destino());
        try {
            restService.modifyBalance("http://localhost:8887/cuentas/" + balanceDestino.getId(), balanceDestino.getSaldo() + transfer.getMonto());
        }catch(Exception e){
            return null;
        }
        return transferRepository.save(transfer);
    }

    public Transfer cancelTransfer(Long transferId) {
        Transfer transfer = transferRepository.findById(transferId).orElse(null);

        Balance balanceOrigen = getBalance(transfer.getId_cuenta_origen());
        try {
            restService.modifyBalance("http://localhost:8887/cuentas/" + balanceOrigen.getId(), balanceOrigen.getSaldo() + transfer.getMonto());
        }catch(Exception e){
            return null;
        }
        transfer.setEstado(Transfer.Status.CANCELADA);
        return transferRepository.save(transfer);
    }

    public Balance getBalance(Long id){
        Balance balance = null;
        try {
            balance = restService.getBalance("http://localhost:8887/cuentas/"+id);
        } catch (Exception e) {
            balance = new Balance();
            balance.setSaldo(-1);
            return balance;
        }
        return balance;
    }
}
