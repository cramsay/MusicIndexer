import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Lets other areas of the program probe the Discogs service for data
 * 
 * This is a bit of a jobby. The API returns sound results (mainly dated by the 
 * looks of it) but V2 cannot differentiate albums and singles! Which is particularly
 * crap when that is a major feature of their normal web page!
 * 
 * This uses the API to get albums but then checks the generated list agains the 
 * html of the normal webpage to pick out which ones are legitimate albums
 * 
 * Example: 
 * 	http://api.discogs.com/artist/foo%20fighters?releases=1&f=xml
 * @author craig
 *
 */
public class DiscogsInterface extends InternetInterface{

	private static final long RATE_LIMIT= 1000;
	private static long nextAllowedTime =0;
	private static Logger log = Logger.getLogger(InternetInterface.class.getCanonicalName());
	private HashMap<String, ArrayList<Album>> dataMap;
	
	public DiscogsInterface(){
		dataMap=new HashMap<>();
	}
	
	@Override
	public Artist getArtist(String name) throws NoSuchArtistException {
		try {
			
			nextAllowedTime = rateLimit(RATE_LIMIT, nextAllowedTime);
			
			//Get file
			name=sanatiseQuery(name);
			URL query = new URL("http://api.discogs.com/artist/"+name+"?releases=1&f=xml");
			query.openConnection();
			InputStream xmlData = query.openStream();
			
			//Parse XML fields
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlData);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("artist");
			Element topArtist = (Element) nList.item(0);
			
			if (topArtist!=null){
				String id = topArtist.getElementsByTagName("id").item(0).getTextContent();
				String fetchedName = topArtist.getElementsByTagName("name").item(0).getTextContent();
				
				//Create an return record of details
				Artist entry = new Artist(name);
				entry.setCopyOf(new MusicEntry(fetchedName, id));
				dataMap.put(id, getReleases(entry, doc)); //Store copy of doc for when looking for artist
				return entry;
				
			}
			
		}catch (Exception e){
			throw new NoSuchArtistException();
		}
		
		return null;
	}

	public ArrayList<Album> getReleases(Artist artist){
		return dataMap.get(artist.getID());
	}
	
	public ArrayList<Album> getReleases(Artist artist, Document doc)
			throws NoReleasesException {
		ArrayList<Album> albums = new ArrayList<Album>();
		
		try {
			
			//nextAllowedTime = rateLimit(RATE_LIMIT, nextAllowedTime);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("release");
			
			//Get normal web page for checking release type
			String webpage = getWebDiscogTable(artist.getName());
			
			int loopCount=0;
			while(loopCount<2){
				
				for(int i=0;i<nList.getLength();i++){
					Element curElement = (Element)nList.item(i);
					
					String id = curElement.getAttribute("id");
					String title= curElement.getElementsByTagName("title").item(0).getTextContent();
					NodeList dateElements = curElement.getElementsByTagName("year");
					if(dateElements.getLength()!=0){
						int date = Integer.parseInt(dateElements.item(0).getTextContent());
						if (webpage.contains(title))
							albums.add(new Album(title,id,date,artist));
					}
				}
				
				nList = doc.getElementsByTagName("master");
				loopCount++;
			}
			
		} catch (Exception e) {
			log.warning("Failed to parse some album data for artist: "+artist.getName());
		}
		
		if (albums.size()==0){
			throw new NoReleasesException();
		}
		
		return albums;
		
	}
	
	private String getWebDiscogTable(String name) throws Exception{
		
		//Get file
		URL query = new URL("http://www.discogs.com/artist/"+sanatiseQuery(name)+"/-Releases/-Albums");
		URLConnection con = query.openConnection();
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
		Matcher m = p.matcher(con.getContentType());
		String charset = m.matches() ? m.group(1) : "ISO-8859-1";
		Reader r = new InputStreamReader(con.getInputStream(), charset);
		StringBuilder buf = new StringBuilder();
		while (true) {
		  int ch = r.read();
		  if (ch < 0)
		    break;
		  buf.append((char) ch);
		}
		
		String page = buf.toString();
		int tableStart = page.indexOf("<table class=\"discography\">");
		page = page.substring(tableStart, page.indexOf("</table>", tableStart));
		return page;
		
	}

}
