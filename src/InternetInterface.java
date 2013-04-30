import java.util.ArrayList;

/**
 * Interface for implementing all web searches for music details
 * @author Craig
 *
 */
public abstract class InternetInterface {
	public abstract Artist getArtist(String name) throws NoSuchArtistException;
	public abstract ArrayList<Album> getReleases(Artist artist) throws NoReleasesException;
	
	protected long rateLimit(long rateLimit, long nextAllowedTime){
		
		if(nextAllowedTime>System.currentTimeMillis()){
			try {
				Thread.sleep(nextAllowedTime-System.currentTimeMillis());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return System.currentTimeMillis()+rateLimit;
	}
	
	protected static int parseDate(String orig) throws NoDateException{
		int date= Integer.parseInt(orig.substring(0, 4));
		if (date==0)
			throw new NoDateException();
		return date;
	}
	
	protected static String sanatiseQuery(String orig){
		return orig.replaceAll(" ", "%20").replaceAll("&", "and");
	}
}
