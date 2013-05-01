import java.io.Serializable;

/**
 * Container for all information regarding a musical release
 * This is holds a reference to the owning artist
 * @author Craig
 *
 */
public class Album extends MusicEntry {

	private int year;
	private Artist artist;
	
	public Album(String name, String id, int year, Artist artist){
		super(name,id);
		this.year = year;
		this.artist = artist;
	}
	
	public int getYear(){
		return year;
	}
	
	@Override
	public String getDetails(){
		return "Title: "+super.getName()+ ", ID: "+super.getID()+", Artist: "+artist.getName()+", Year:"+year;
	}
}
