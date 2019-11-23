package unsl.utils;

import org.hibernate.validator.constraints.URL;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import unsl.entities.Balance;
import unsl.entities.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestService {

    public class BalanceList {
        private List<Balance> balance;

        public BalanceList() {
            balance = new ArrayList<>();
        }

        public List<Balance> getBalance() {
            return balance;
        }

        public void setBalance(List<Balance> balance) {
            this.balance = balance;
        }

    }


    public List<Balance> getBalance(String url) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Balance>> balanceResponse;
        List<Balance> balance = null;
        try {
            balanceResponse = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Balance>>() {});
            if (balanceResponse != null && balanceResponse.hasBody()) {
                balance = balanceResponse.getBody();
            }
        } catch (Exception e) {
            throw new Exception(buildMessageError(e));
        }
        return balance;
    }

    public void logicDeleteBalance(String url) throws Exception{
        RestTemplate restTemplate = new RestTemplate();
        try{
            restTemplate.delete(url);
        }catch (Exception e){
            throw new Exception(buildMessageError(e));
        }
    }

    private String buildMessageError(Exception e) {
        String msg = e.getMessage();
        if (e instanceof HttpClientErrorException) {
            msg = ((HttpClientErrorException) e).getResponseBodyAsString();
        } else if (e instanceof HttpServerErrorException) {
            msg =  ((HttpServerErrorException) e).getResponseBodyAsString();
        }
        return msg;
    }

}