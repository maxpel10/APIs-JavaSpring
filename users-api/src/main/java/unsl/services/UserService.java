package unsl.services;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import unsl.config.CacheConfig;
import unsl.entities.Balance;
import unsl.entities.User;
import unsl.repository.UserRepository;
import unsl.utils.RestService;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RestService restService;

    @Cacheable(CacheConfig.USERS_CACHE)
    public List<User> getAll() {
        simulateSlowService();
        return userRepository.findAll();
    }

    @Cacheable(CacheConfig.USERS_CACHE)
    public User getUser(Long userId) {
        simulateSlowService();
        return userRepository.findById(userId).orElse(null);
    }

    @Cacheable(CacheConfig.USERS_CACHE)
    public User findByDni(Long dni) {
        simulateSlowService();
        return userRepository.findByDni(dni);
    }

    @CacheEvict(value = CacheConfig.USERS_CACHE,allEntries = true)
    public User saveUser(User user) {
        if(user.getDni() == 0 || user.getNombre() == null || user.getApellido() == null )
            return null;
        else
            return userRepository.save(user);
    }

    @CacheEvict(value = CacheConfig.USERS_CACHE,allEntries = true)
    public User updateUser(Long userId, User updatedUser){
        User user = userRepository.findById(userId).orElse(null);;
        if (user ==  null){
            return null;
        }
        if(updatedUser.getNombre() != null)
            user.setNombre(updatedUser.getNombre());
        if(updatedUser.getApellido() != null)
            user.setApellido(updatedUser.getApellido());
        return userRepository.save(user);
    }

    @CacheEvict(value = CacheConfig.USERS_CACHE,allEntries = true)
    public User logicDeleteUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);;
        if (user ==  null){
            return null;
        }
        user.setEstado(User.Status.BAJA);
        List<Balance> balances;
        try {
            balances = restService.getBalance("http://localhost:8887/cuentas/busqueda?titular="+userId);
            Iterator i = balances.iterator();
            while(i.hasNext()){
                Balance balance = (Balance) i.next();
                restService.logicDeleteBalance("http://localhost:8887/cuentas/"+balance.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return userRepository.save(user);
    }

    private void simulateSlowService() {
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
