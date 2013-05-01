import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents an Artist. Can be used to obtain relevant
 * data from web services. Holds a collection of releases
 * and can use limited sorting and other album queries
 * @author Craig
 */
public class Artist extends MusicEntry {

	private String rawName;
	private ArrayList<Album> releases;
	private static InternetInterface network = new DiscogsInterface();
	
	/**
	 * Constructs a new Artist object
	 * @param name	Artist name parsed from filesystem
	 */
	public Artist(String name){
		rawName=name;
		releases = new ArrayList<Album>();
	}
	
	public boolean searchForArtistDetails(){
		Artist result;
		try {
			result = network.getArtist(rawName);

			if (result==null)
				return false;
			super.setCopyOf(result);
			return true;
			
		} catch (NoSuchArtistException e) {
			System.out.println("Couldn't find an online match for artist "+rawName);
		}
		
		return false;
	}
	
	public void populateAlbums(){
		try {
			releases = network.getReleases(this);
		} catch (NoReleasesException e) {
			System.out.println("Couldn't find any online releases for artist "+rawName);
		}
	}
	
	public Album getLatestAlbum(){
		//Find max date
		if (releases.size()==0)
			return null;

		int maxI=0;
		for (int i=1;i<releases.size();i++){
			if (releases.get(i).getYear()>releases.get(maxI).getYear())
				maxI=i;
		}
		
		//Return corresponding release
		return releases.get(maxI);
		
	}
	
	public ArrayList<Album> getAlbumsSince(int year){
		ArrayList<Album> result = new ArrayList<Album>();
		
		for (Album alb : releases){
			if (alb.getYear()>=year)
				result.add(alb);
		}
		
		return result;
	}
	
	public boolean isFound(){
		if(getName()==null)
			return false;
		else
			return true;
	}
	
}
