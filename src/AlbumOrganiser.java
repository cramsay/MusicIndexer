import java.util.ArrayList;

/**
 * Static utility class for sorting lists of albums
 * @author Craig
 *
 */
public class AlbumOrganiser {

	public static void removeDupilateAlbums(ArrayList<Album> releases){

		for(int i=0;i<releases.size();i++){
			for (int j=i+1;j<releases.size();j++){
				Album a1 = releases.get(i);
				Album a2 = releases.get(j);
				if (a1.getName().trim().toLowerCase().equals(
							a2.getName().trim().toLowerCase())){
					if (a1.getYear()>a2.getYear()){
						releases.remove(i);
						i--;
						break;
					}
					else
						releases.remove(j);
					j--;
					
				}
			}
		}
	}
	
	public static void orderAlbumsByDate(ArrayList<Album> releases){
		//TODO COMPLETE ME
	}
}
