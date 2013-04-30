/**
 * Static utility class for parsing out metadata from audio
 * files.
 * @author Craig
 *
 */
import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class MetadataParser {

	public MetadataParser(){
	}
	
	public static String getArtist(File src){
		
		try{
			return getMetaData(src).getFirst(FieldKey.ARTIST);
		} catch (NullPointerException e){
			System.out.println("Avoided reading artist from null metadata");
		}
		
		return null;
	}
	
public static String getAlbum(File src){
		
		try{
			return getMetaData(src).getFirst(FieldKey.ALBUM);
		} catch (NullPointerException e){
			System.out.println("Avoided reading artist from null metadata");
		}
		
		return null;
	}

	private static Tag getMetaData(File src) throws NullPointerException{
		//TODO try to add in check on extension. Don't check for non-audio
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
