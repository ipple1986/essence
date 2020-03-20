package abstractFactory;

public class ActionMovieactory implements IMovieFactory {
    @Override
    public ITollywoodMovie tollywoodMovie() {
        return new TollywoodMovie();
    }

    @Override
    public IBollywoodMovie bollywoodMovie() {
        return new BollywoodMovie();
    }
}
