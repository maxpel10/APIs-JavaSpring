package unsl.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import unsl.entities.Balance;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class RestService {

    private static Logger LOGGER = LoggerFactory.getLogger(RestService.class);

    @Retryable( maxAttempts = 4, backoff = @Backoff(1000))
    public Balance getBalance(String url) throws Exception{
        RestTemplate restTemplate = new RestTemplate();
        LOGGER.info(String.format("TransferAPI RETRY request to Cliente %d", LocalDateTime.now().getSecond()));
        Balance balance;
        try{
            balance = restTemplate.getForObject(url, Balance.class);
        }catch (Exception e){
            throw new Exception(buildMessageError(e));
        }
        return balance;
    }

    @Retryable( maxAttempts = 4, backoff = @Backoff(1000))
    public void modifyBalance(String url, double saldo) throws Exception{
        LOGGER.info(String.format("TransferAPI RETRY request to Cliente %d", LocalDateTime.now().getSecond()));
        Map<String, Double> params = new HashMap<>();
        params.put("saldo", saldo);
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.put(url, params);
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