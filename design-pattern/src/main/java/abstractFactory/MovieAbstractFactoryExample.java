package abstractFactory;

public class MovieAbstractFactoryExample {
    public static void main(String[] args) {
        IMovieFactory iMovieFactory = new ActionMovieactory();

        IBollywoodMovie iBollywoodMovie = iMovieFactory.bollywoodMovie();
        ITollywoodMovie iTollywoodMovie = iMovieFactory.tollywoodMovie();

        System.out.println(iBollywoodMovie.movieName());
        System.out.println(iTollywoodMovie.movieName());

    }
}
