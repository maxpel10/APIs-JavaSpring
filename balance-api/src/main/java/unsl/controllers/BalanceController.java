package unsl.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unsl.entities.Balance;
import unsl.entities.ResponseError;
import unsl.services.BalanceService;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@RestController
public class BalanceController {
    @Autowired
    BalanceService balanceService;

    @PostMapping(value = "/cuentas")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Object createBalance(@RequestBody Balance balance) {
        //Control de que todos los campos sean enviados
        if(balance.getTipo_moneda() == null || balance.getTitular() == 0){
            return new ResponseEntity(new ResponseError(400, String.format("Id del titular y tipo de moneda de la cuenta requeridos.")), HttpStatus.NOT_FOUND);
        }

        //Control de que el cliente existe
        try {
            balanceService.getUser(balance.getTitular());
        } catch (Exception e) {
            return new ResponseEntity(new ResponseError(404, String.format("Cliente con id %d no encontrado.", balance.getTitular())), HttpStatus.NOT_FOUND);
        }

        //Control de que no tiene una cuenta del tipo que quiere crear
        List<Balance> userBalances = balanceService.findByUser(balance.getTitular());
        Iterator i = userBalances.iterator();
        while(i.hasNext()){
            Balance userBalance = (Balance) i.next();
            if(userBalance.getTipo_moneda() == balance.getTipo_moneda())
                return new ResponseEntity(new ResponseError(400, String.format("El titular ya tiene una cuenta con tipo de moneda "+balance.getTipo_moneda()+".")), HttpStatus.NOT_FOUND);
        }

        //Opcional: Cuando es la primera cuenta de tipo peso la cuenta inicia con $500
        if(balance.getTipo_moneda() == Balance.Money.PESO_AR)
            balance.setSaldo(500.0);

        Balance savedBalance = balanceService.saveBalance(balance);

        HashMap<String, Long> map = new HashMap<>();
        long value = savedBalance.getId();
        map.put("id", value);
        return map;
    }

    @GetMapping(value = "/cuentas")
    @ResponseBody
    public Object getAll() {
        return balanceService.getAll();
    }

    @GetMapping(value = "/cuentas/{id}")
    @ResponseBody
    public Object getBalance(@PathVariable("id") Long id) {
        Balance balance = balanceService.getBalance(id);
        if ( balance == null) {
            return new ResponseEntity(new ResponseError(404, String.format("Cuenta con id %d no encontrado.", id)), HttpStatus.NOT_FOUND);
        }
        return balance;
    }

    @GetMapping(value = "/cuentas/busqueda")
    @ResponseBody
    public Object searchByUser(@RequestParam("titular") Long titular) {
        List<Balance> balance = balanceService.findByUser(titular);
        if ( balance == null) {
            return new ResponseEntity(new ResponseError(404, String.format("Cliente con id %d no existe.", titular)), HttpStatus.NOT_FOUND);
        }

        if(balance.isEmpty()){
            return new ResponseEntity(new ResponseError(404, String.format("Cliente con id %d no tiene cuentas asociadas.", titular)), HttpStatus.NOT_FOUND);
        }
        return balance;
    }

    @PutMapping(value = "/cuentas/{id}")
    @ResponseBody
    public Object updateBalance(@PathVariable("id") Long id, @RequestBody Balance Balance) {
        if(Balance.getSaldo()<0){
            return new ResponseEntity(new ResponseError(400, String.format("Saldo inválido.", id)), HttpStatus.NOT_FOUND);
        }

        Balance balance = balanceService.updateBalance(id,Balance);
        if ( balance == null) {
            return new ResponseEntity(new ResponseError(404, String.format("Cuenta con id %d no encontrado.", id)), HttpStatus.NOT_FOUND);
        }
        return balance;
    }

    @DeleteMapping(value = "/cuentas/{id}")
    @ResponseBody
    public Object updateBalance(@PathVariable("id") Long id) {
        Balance balance = balanceService.deleteBalance(id);
        if ( balance == null) {
            return new ResponseEntity(new ResponseError(404, String.format("Cuenta con id %d no encontrado.", id)), HttpStatus.NOT_FOUND);
        }
        return balance;
    }

}

