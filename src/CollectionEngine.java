import java.awt.event.ActionListener;
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
public class CollectionEngine implements Runnable{
	
	private static Logger log = Logger.getLogger(CollectionEngine.class.getCanonicalName());
	
	private ArrayList<Artist>artists;
	private HashSet<String>artistNames;
	private ProgressListener progress;
	private File root;
	
	public CollectionEngine(){
		clearCollection();
	}
	
	public void clearCollection(){
		artists = new ArrayList<Artist>();
		artistNames = new HashSet<String>();
		File root = new File(System.getProperty("user.dir"));
	}
	
	public void primeForScan(ProgressListener progress,File root){
		this.progress=progress;
		this.root=root;
	}
	
	public void run(){
		populateCollection();
	}
	
	public void populateCollection(){
		progress.setOverallMax(3);
		progress.setOverallProgress(0);
		progress.setTaskName("Looking at metadata from "+root.getPath());
		scanFileCollection(root);
		
		progress.setOverallProgress(1);
		progress.setCurrentMax(artistNames.size());
		progress.setTaskName("Looking for artist information online");
		populateArtists();
		
		progress.setOverallProgress(2);
		progress.setCurrentMax(artists.size());
		progress.setTaskName("Looking for album information online");
		populateAlbums();
		
		progress.setOverallProgress(3);
		progress.setTaskName("Finished :)");

		progress.done();		
	}
	
	public void scanFileCollection(File src){
		if (src.isDirectory()){
			 for (File child: src.listFiles())
				 scanFileCollection(child);
		 }
		 
		 else{
			 try {
				 String artist = MetadataParser.getArtist(src);
				 if(artist!=null){
					 int origSize=artistNames.size();
					 artistNames.add(artist);
					 if(artistNames.size()>origSize)
						 progress.addInfo("Found artist: "+artist);
				 }
			} catch (Exception e) {
				progress.addWarning("Couldn't parse artist for file: "+src.getName());
			}
		 }
	}
	
	public void populateArtists(){
		int i=0;
		for (String name: artistNames){
			Artist art = new Artist(name);
			art.searchForArtistDetails();
			if (art.isFound()){
				artists.add(art);
				progress.addInfo("Got online match for artist: "+art.getName());
			}
			else {
				progress.addWarning("No online match for artist: "+name);
			}
			progress.setCurrentProgress(i++);
		}
	}
	
	public void populateAlbums(){
		int i=0;
		for (Artist art: artists){
			art.populateAlbums();
			progress.setCurrentProgress(i++);
			if (art.getReleases().size()>0){
				for (Album alb: art.getReleases())
					progress.addInfo("Found album: "+alb.getName());
			}
			else
				progress.addWarning("No releases found for artist: "+art.getName());
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
	
	public ArrayList<Artist> getArtists(){
		return artists;
	}
}
