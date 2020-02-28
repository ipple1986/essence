package readinglist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReadingListRepository extends JpaRepository<Book, Long> {//JpaRepository 18个通用接口
        List<Book> findByReader(String reader);
}