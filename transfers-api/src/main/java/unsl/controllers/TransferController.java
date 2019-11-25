package unsl.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unsl.entities.Balance;
import unsl.entities.ResponseError;
import unsl.entities.Transfer;
import unsl.services.TransferService;


import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
public class TransferController {
    @Autowired
    TransferService transferService;


    @GetMapping(value = "/transferencias")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<Transfer> getAll() {
       return transferService.getAll();
    }


    @GetMapping(value = "/transferencias/{id}")
    @ResponseBody
    public Object getTransfer(@PathVariable("id") Long id) {
        Transfer transfer = transferService.getTransfer(id);
        if (transfer == null) {
            return new ResponseEntity(new ResponseError(404, String.format("Transferencia con numero %d no encontrada.", id)), HttpStatus.NOT_FOUND);
        }
        return transfer;
    }



    @PostMapping(value = "/transferencias")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Object createTransfer(@RequestBody Transfer transfer) {

        if(transfer.getId_cuenta_origen() == transfer.getId_cuenta_destino()){
            return new ResponseEntity(new ResponseError(400, String.format("La cuenta de origen debe ser distinta a la cuenta destino.")), HttpStatus.NOT_FOUND);
        }

        if(transfer.getMonto()<=0){
            return new ResponseEntity(new ResponseError(400, String.format("El monto de la transferencia es inválido.")), HttpStatus.NOT_FOUND);
        }

        Balance inBalance = transferService.getBalance(transfer.getId_cuenta_origen());
        Balance outBalance = transferService.getBalance(transfer.getId_cuenta_destino());

        if(inBalance == null){
            return new ResponseEntity(new ResponseError(404, String.format("Cuenta con id %d no encontrada.", transfer.getId_cuenta_origen())), HttpStatus.NOT_FOUND);
        }

        if(outBalance == null){
            return new ResponseEntity(new ResponseError(404, String.format("Cuenta con id %d no encontrada.", transfer.getId_cuenta_destino())), HttpStatus.NOT_FOUND);
        }

        if(inBalance.getSaldo()==-1 || outBalance.getSaldo()==-1){
            return new ResponseEntity(new ResponseError(500, String.format("Fallas en la comunicación con la API cuentas.")), HttpStatus.NOT_FOUND);
        }

        if(inBalance.getTipo_moneda() != outBalance.getTipo_moneda()){
            return new ResponseEntity(new ResponseError(400, String.format("La cuenta de origen debe ser de la misma moneda que la cuenta destino.")), HttpStatus.NOT_FOUND);
        }

        if(inBalance.getSaldo() < transfer.getMonto()){
            return new ResponseEntity(new ResponseError(400, String.format("Saldo insuficiente.")), HttpStatus.NOT_FOUND);
        }

        transfer.setFecha(Date.from(Instant.now()));
        Transfer savedTransfer = transferService.saveTransfer(transfer);

        if(savedTransfer == null){
            return new ResponseEntity(new ResponseError(500, String.format("Fallas en la comunicación con la API cuentas.")), HttpStatus.NOT_FOUND);
        }

        HashMap<String, Long> map = new HashMap<>();
        long value = savedTransfer.getNro_transferencia();
        map.put("nro_transferencia", value);
        return map;
    }

    @PutMapping(value = "/transferencias/{id}")
    @ResponseBody
    public Object processTransfer(@PathVariable("id") Long id){
        Transfer transfer = transferService.getTransfer(id);

        if(transfer == null){
            return new ResponseEntity(new ResponseError(404, String.format("Transferencia con numero %d no encontrada.", id)), HttpStatus.NOT_FOUND);
        }
        else{
            if(transfer.getEstado() == Transfer.Status.PENDIENTE){
                transfer = transferService.processTransfer(id);
                if(transfer == null){
                    return new ResponseEntity(new ResponseError(500, String.format("Fallas en la comunicación con la API cuentas.")), HttpStatus.NOT_FOUND);
                }
            }
            else{
                return new ResponseEntity(new ResponseError(404, String.format("El estado de la transferencia ya ha sido modificado una vez.", id)), HttpStatus.NOT_FOUND);
            }
        }
        return transfer;
    }

    @DeleteMapping(value = "/transferencias/{id}")
    @ResponseBody
    public Object cancelTransfer(@PathVariable("id") Long id) {
        Transfer transfer = transferService.getTransfer(id);

        if(transfer == null){
            return new ResponseEntity(new ResponseError(404, String.format("Transferencia con numero %d no encontrada.", id)), HttpStatus.NOT_FOUND);
        }
        else{
            if(transfer.getEstado() == Transfer.Status.PENDIENTE){
                transfer = transferService.cancelTransfer(id);
                if(transfer == null){
                    return new ResponseEntity(new ResponseError(500, String.format("Fallas en la comunicación con la API cuentas.")), HttpStatus.NOT_FOUND);
                }
            }
            else{
                return new ResponseEntity(new ResponseError(404, String.format("El estado de la transferencia ya ha sido modificado una vez.", id)), HttpStatus.NOT_FOUND);
            }
        }
        return transfer;
    }

}

