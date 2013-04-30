/**
 * Serves as the basis for any entry. E.G. Albums and Artists
 * @author Craig
 *
 */
public class MusicEntry {
	
	private String name;
	private String id;
	private int certainty;
	
	/**
	 * Constructs a new entry with the given details
	 * @param name	Name of Artist of title of release
	 * @param id	ID of entry
	 */
	public MusicEntry(String name, String id, int certainty){
		this.name = name;
		this.id = id;
		this.certainty = certainty;
	}
	
	/**
	 * Constructs a new MusicEntry when no details are immediately available
	 */
	public MusicEntry(){
		
	}
	
	/**
	 * Sets the state of the MusicEntry object to a copy of another
	 * @param copy	The MusicEntry to be copied from
	 */
	public void setCopyOf(MusicEntry copy){
		setName(copy.getName());
		setID(copy.getID());
		setCertainty(copy.getCertainty());
	}
	
	public String getName(){
		return name;
	}
	
	public String getID(){
		return id;
	}
	
	public int getCertainty(){
		return certainty;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setID(String id){
		this.id = id;
	}
	
	public void setCertainty(int certainty){
		this.certainty = certainty;
	}
	
	public String getDetails(){
		return "Name: "+name+", ID: "+id;
	}
}
