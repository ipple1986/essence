package readinglist;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.LinkedHashMap;

public class JpaRepositories extends LinkedHashMap<String,JpaRepository>  {

    public JpaRepositories(LinkedHashMap<String,JpaRepository> maps){
        maps.forEach((k,v)->put(k,v));
    }
}
