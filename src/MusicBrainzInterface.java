import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;


/*
 * Acts as an interface between the application cor and
 * the "MusicBrainz" web service.
 * 
 * So far it seems quite accurate but it is rate limited
 * to a single request per second.
 */
public class MusicBrainzInterface implements InternetInterface{

	private static final long RATE_LIMIT=1000;
	private static long nextAllowedTime=0;
	
	/**
	 * Limits the rate of requests to the MusicBrainz web server
	 */
	private static void rateLimit(){
		if(nextAllowedTime>System.currentTimeMillis()){
			try {
				Thread.sleep(nextAllowedTime-System.currentTimeMillis());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		nextAllowedTime=System.currentTimeMillis()+RATE_LIMIT;
	}
	
	/**
	 * Querys the MusicBrainz web service for a particular artist
	 * @param name	The name of the artist to search for
	 */
	public Artist getArtist(String name) throws NoSuchArtistException{
		try {
			
			rateLimit();
			
			//Get file
			name=sanatiseQuery(name);
			URL query = new URL("http://www.musicbrainz.org/ws/2/artist?query=\""+name +"\"");
			query.openConnection();
			InputStream xmlData = query.openStream();
			
			//Parse XML fields
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlData);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("artist");
			Element topArtist = (Element) nList.item(0);
			
			if (topArtist!=null){
				String id = topArtist.getAttribute("id");
				int certainty = Integer.parseInt(topArtist.getAttribute("ext:score"));
				String fetchedName = topArtist.getElementsByTagName("name").item(0).getTextContent();
				
				//Create an return record of details
				Artist entry = new Artist(name);
				entry.setCopyOf(new MusicEntry(fetchedName, id, certainty));
				return entry;
				
			}
			
		}catch (Exception e){
			throw new NoSuchArtistException();
		}
		
		return null;
	}
	
	/**
	 * Querys the MusicBrainz web service for a particular artist
	 * @param name	The name of the artist to search for
	 */
	public ArrayList<Album> getReleases(Artist artist) throws NoReleasesException{
		ArrayList<Album> albums = new ArrayList<Album>();
		
		try {
			
			rateLimit();
			
			//Get file
			URL query = new URL("http://www.musicbrainz.org/ws/2/release?artist="
					+artist.getID()+"&type=album|ep");
			query.openConnection();
			InputStream xmlData = query.openStream();
			
			//Parse XML fields
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlData);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("release");
			
			for(int i=0;i<nList.getLength();i++){
				Element curElement = (Element)nList.item(i);
				
				String id = curElement.getAttribute("id");
				String title= curElement.getElementsByTagName("title").item(0).getTextContent();
				NodeList dateElements = curElement.getElementsByTagName("date");
				if(dateElements.getLength()!=0){
					int date = parseDate(dateElements.item(0).getTextContent());	
					albums.add(new Album(title,id,date,artist));
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (albums.size()==0)
			throw new NoReleasesException();
		
		return albums;
		
	}
	
	private static int parseDate(String orig) throws NoDateException{
		int date= Integer.parseInt(orig.substring(0, 4));
		if (date==0)
			throw new NoDateException();
		return date;
	}
	
	private static String sanatiseQuery(String orig){
		return orig.replaceAll(" ", "%20").replaceAll("&", "and");
	}
}
