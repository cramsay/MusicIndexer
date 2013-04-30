import java.util.ArrayList;

/**
 * Interface for searching the Last FM api
 * Just like the Music Brainz interface this seems to be rate
 * limited to 1 request per second
 * 
 * @author craig
 *
 */
public class LastFMInterface implements InternetInterface {

	@Override
	public Artist getArtist(String name) throws NoSuchArtistException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Album> getReleases(Artist artist)
			throws NoReleasesException {
		// TODO Auto-generated method stub
		return null;
	}

}
