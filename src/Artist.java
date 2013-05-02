import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Represents an Artist. Can be used to obtain relevant
 * data from web services. Holds a collection of releases
 * and can use limited sorting and other album queries
 * @author Craig
 */
public class Artist extends MusicEntry {

	private String rawName;
	private ArrayList<Album> releases;
	private HashSet<String> ownedAlbumNames;
	private static InternetInterface network = new DiscogsInterface();
	private static Logger log = Logger.getLogger(Artist.class.getCanonicalName());
	
	/**
	 * Constructs a new Artist object
	 * @param name	Artist name parsed from filesystem
	 */
	public Artist(String name){
		rawName=name;
		releases = new ArrayList<Album>();
		ownedAlbumNames = new HashSet<String>();
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
			log.info("Couldn't find an online match for artist "+rawName);
		}
		
		return false;
	}
	
	public void populateAlbums(){
		try {
			releases = network.getReleases(this);
		} catch (NoReleasesException e) {
			log.info("Couldn't find any online releases for artist "+rawName);
		}
		AlbumOrganiser.removeDupilateAlbums(releases);
		AlbumOrganiser.orderAlbumsByDate(releases);
		AlbumOrganiser.setOwnedState(releases,ownedAlbumNames);
	}
	
	public boolean isFound(){
		if(getName()==null)
			return false;
		else
			return true;
	}
	
	public ArrayList<String[]> getAlbumDetailsArray(){
		
		ArrayList<String[]> details = new ArrayList<String[]>();
		for (Album alb: releases)
			details.add(alb.getDetailsArray());
		return details;
	}
	
	public ArrayList<Album> getReleases(){
		return releases;
	}
	
	public boolean addOwnedAlbumName(String name){
		return ownedAlbumNames.add(name);
	}
	
	public String getMetadataName(){
		return rawName;
	}
	
}
