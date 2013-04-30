import java.util.ArrayList;

/**
 * Interface for implementing all web searches for music details
 * @author Craig
 *
 */
public interface InternetInterface {
	public Artist getArtist(String name) throws NoSuchArtistException;
	public ArrayList<Album> getReleases(Artist artist) throws NoReleasesException;
}
