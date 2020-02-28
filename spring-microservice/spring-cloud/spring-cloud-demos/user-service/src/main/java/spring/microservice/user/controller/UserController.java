package spring.microservice.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.microservice.user.ServiceConfig;
import spring.microservice.user.model.User;
import spring.microservice.user.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/v1/departments/{departmentId}/users")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    ServiceConfig serviceConfig;

    @GetMapping("/{userId}")
    public User getUser(@PathVariable("departmentId") String firstName, @PathVariable("userId") String lastName){
        System.out.println(serviceConfig.getSpringDatasourcePassword());
        User user =  new User("Jim","zhao",10,"ipple1986@gmail.com");
         userService.saveUser(user);
        return user;
    }
    @GetMapping
    public List<User> getAllUser(){
        return userService.getUsers();
    }
}
