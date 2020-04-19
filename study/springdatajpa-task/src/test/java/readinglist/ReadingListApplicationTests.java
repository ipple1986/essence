package readinglist;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReadingListApplication.class)
public class ReadingListApplicationTests {

/*@Autowired
	ReaderRepository readerRepository;
@Autowired
MyJpaRepositories a;*/
@Autowired
	JpaRepositories jpaRepositories;
	@Test
	public void contextLoads() {
		System.out.println(jpaRepositories);
	}

}
