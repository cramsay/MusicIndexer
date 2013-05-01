import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Works with an entire music collection
 * 
 * Provides information to an interface about the collection
 * @author Craig
 *
 */
public class CollectionEngine {
	
	private static String MUSIC_DIR="/home/cramsay/Music"; 
	private static Logger log = Logger.getLogger(CollectionEngine.class.getCanonicalName());
	
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
			}
		}
	}
	
	public void populateAlbums(){
		for (Artist art: artists){
			art.populateAlbums();
		}
	}
	
	public void SaveCollectionToDisk(File saveState){
		try {
			FileOutputStream fout = new FileOutputStream(saveState);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(artists);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			log.warning("Couldn't write to the save state file");
		}
	}
	
	public void ReadCollectionFromDisk(File saveState){
		
			try {
				ObjectInputStream ois;
				ois = new ObjectInputStream(new FileInputStream(saveState));
				artists = (ArrayList<Artist>) ois.readObject();
				ois.close();
			} catch (FileNotFoundException e) {
				log.warning("Save state file not found");
			} catch (IOException e) {
				log.warning("Couldn't read from the save file");
			} catch (ClassNotFoundException e) {
				log.warning("Problem with the format of the save file");
			}
	}
	
	public String[][] getAlbumDetailsArray(){	
		ArrayList<String[]> details = new ArrayList<String[]>();
		for (Artist art: artists)
			details.addAll(art.getAlbumDetailsArray());

		final String[][] raw = new String[details.size()][];
		int i = 0;
		for (String[] line : details) 
		  raw[i++] = line;
		
		return raw;
	}
}
