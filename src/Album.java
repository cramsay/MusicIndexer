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
	private boolean owned;
	
	public Album(String name, String id, int year, Artist artist){
		super(name,id);
		this.year = year;
		this.artist = artist;
		this.owned = false;
	}
	
	public int getYear(){
		return year;
	}
	
	public String toString(){
		return "Title: "+super.getName()+ ", ID: "+super.getID()+", Artist: "+artist.getName()+", Year:"+year;
	}
	
	public String[] getDetailsArray(){
		return new String[]{artist.getName(), super.getName(), Integer.toString(year),Boolean.toString(owned)};
	}
	
	public void setOwned(boolean o){
		owned=o;
	}
	
	public boolean getOwned(){
		return owned;
	}
}
