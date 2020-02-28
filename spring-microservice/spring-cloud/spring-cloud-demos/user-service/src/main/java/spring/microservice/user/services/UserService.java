package spring.microservice.user.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.microservice.user.ServiceConfig;
import spring.microservice.user.model.User;
import spring.microservice.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    ServiceConfig serviceConfig;
    @Autowired
    UserRepository userRepository;
    public List<User> getUsers(){
        List<User> users = new ArrayList<User>();
        Iterator<User> userInterator = userRepository.findAll().iterator();
        while(userInterator.hasNext()){
            users.add(userInterator.next());
        }
        return users;
    }
    public User findByUserId(String userId){
        return userRepository.findById(userId).orElse(null);
    }
    public User saveUser(User user){
        user.setUserId(UUID.randomUUID().toString());
        userRepository.save(user);
        return user;
    }
}
