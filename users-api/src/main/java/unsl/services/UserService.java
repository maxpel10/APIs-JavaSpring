package unsl.services;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User findByDni(Long dni) {
        return userRepository.findByDni(dni);
    }

    public User saveUser(User user) {
        if(user.getDni() == 0 || user.getNombre() == null || user.getApellido() == null )
            return null;
        else
            return userRepository.save(user);
    }

    public User updateUser(Long userId, User updatedUser){
        User user = userRepository.findById(userId).orElse(null);;
        if (user ==  null){
            return null;
        }
        if(updatedUser.getNombre() != null)
            user.setNombre(updatedUser.getNombre());
        if(updatedUser.getApellido() != null)
            user.setApellido(updatedUser.getApellido());
        if(updatedUser.getDni() != 0)
            user.setDni(updatedUser.getDni());
        return userRepository.save(user);
    }

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
}
