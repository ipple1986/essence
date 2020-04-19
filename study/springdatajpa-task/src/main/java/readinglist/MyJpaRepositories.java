package readinglist;

import org.springframework.stereotype.Component;

@Component("aa")
public class MyJpaRepositories implements JpaRepositoriesAware {
    public ReaderRepository getReaderRepository() {
        return readerRepository;
    }

    public void setReaderRepository(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    private ReaderRepository readerRepository;

    @Override
    public void setRepositories(JpaRepositories repositories) {
        repositories.forEach((k,v)->{

            System.out.println(k + " "+ v);
        });
    }
}
