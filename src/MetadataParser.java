/**
 * Static utility class for parsing out metadata from audio
 * files.
 * @author Craig
 *
 */
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class MetadataParser {

	private static Logger log = Logger.getLogger(MetadataParser.class.getCanonicalName());
	private static final String[] audioExtensions={"mp3", "mp4" ,"ogg", "flac", "wma", "wav"};
	
	public MetadataParser(){
	}
	
	public static String getArtist(File src){
		
		try{
			return getMetaData(src).getFirst(FieldKey.ARTIST);
		} catch (NullPointerException e){
			log.fine("Avoided reading from null metadata: "+src.getName());
		} catch (NotAudioFileException e) {
			log.fine("Ignoring non audio file: "+src.getName());
		}
		
		return null;
	}
	
public static String getAlbum(File src){
		
		try{
			return getMetaData(src).getFirst(FieldKey.ALBUM);
		} catch (NullPointerException e){
			log.fine("Avoided reading from null metadata: "+src.getName());
		} catch (NotAudioFileException e) {
			log.fine("Ignoring non audio file: "+src.getName());
		}
		
		return null;
	}

	private static Tag getMetaData(File src) throws NullPointerException, NotAudioFileException{
		
		//Perform a check on the file extension
		if (src.isFile()){
			String extension = src.getName().substring(src.getName().lastIndexOf(".")+1);
			boolean isAudioFile =false;
			
			for (String check : audioExtensions){
				if (extension.equals(check)){
					isAudioFile=true;
					break;
				}
			}
			
			if (isAudioFile==false)
				throw new NotAudioFileException();
		}

		AudioFile f = null;
		try {
			f = AudioFileIO.read(src);
		} catch (CannotReadException | IOException | TagException
				| ReadOnlyFileException | InvalidAudioFrameException e) {
			System.out.println("Couldn't parse metadata from " + src.getName());
			throw new NullPointerException();
		}
		return f.getTag();
	}
}
