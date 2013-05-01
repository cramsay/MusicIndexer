import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	
	private static String MUSIC_DIR="/home/cramsay/Music"; 
	
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
				 if(artist!=null){
					 artistNames.add(artist);
					 System.out.println(artistNames.size());//TODO remove
				 }
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
			}
		}
	}
	
	public void SaveCollectionToDisk(File saveState){
		try {
			FileOutputStream fout = new FileOutputStream(saveState);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(artists);
		
		} catch (IOException e) {
			System.out.println("Couldn't read from the save file");
		}
	}
	
	public void ReadCollectionFromDisk(File saveState){
		
			try {
				ObjectInputStream ois;
				ois = new ObjectInputStream(new FileInputStream(saveState));
				System.out.println("Going to read object in");
				artists = (ArrayList<Artist>) ois.readObject();
				
			} catch (FileNotFoundException e) {
				System.out.println("Save state file not found");
			} catch (IOException e) {
				System.out.println("Couldn't read from the save file");
			} catch (ClassNotFoundException e) {
				System.out.println("Problem with the format of the save file");
			}
		
	}
}
