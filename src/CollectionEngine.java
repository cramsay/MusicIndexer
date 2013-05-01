import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Works with an entire music collection
 * 
 * Provides information to an interface about the collection
 * @author Craig
 *
 */
public class CollectionEngine {
	
	private static String MUSIC_DIR="/home/cramsay/Music/Muse"; 
	
	private ArrayList<Artist>artists;
	private HashSet<String>artistNames;
	
	public CollectionEngine(){
		artists = new ArrayList<Artist>();
		artistNames = new HashSet<String>();
	}
	
	public void populateArtistNames(){
		populateArtistNames(new File(MUSIC_DIR));
	}
	
	private void populateArtistNames(File src){
		if (src.isDirectory()){
			 for (File child: src.listFiles())
				 populateArtistNames(child);
		 }
		 
		 else{
			 try {
				 String artist = MetadataParser.getArtist(src);
				 if(artist!=null)
					 artistNames.add(artist);
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
	}
	
	public void searchForArtistDetails(){
		for (String name: artistNames){
			Artist art = new Artist(name);
			art.searchForArtistDetails();
			if (art.isFound()){
				artists.add(art);
			 	 //System.out.println("Found artist: "+art.getDetails());//TODO remove
			}
		}
	}
	
	public void populateAlbums(){
		for (Artist art: artists){
			art.populateAlbums();
			Album alb = art.getLatestAlbum();
			if (alb!=null){
				System.out.println(alb.getDetails());
				//for (Album a: art.getAlbumsSince(0))
				//	System.out.println(a.getDetails());
			}
		}
	}
}
