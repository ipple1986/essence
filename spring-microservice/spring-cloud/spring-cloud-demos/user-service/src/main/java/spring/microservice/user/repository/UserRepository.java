package spring.microservice.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import spring.microservice.user.model.User;


@Repository
public interface UserRepository extends CrudRepository<User,String> {
}