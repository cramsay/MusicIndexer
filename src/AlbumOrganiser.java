import java.util.ArrayList;
import java.util.HashSet;

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
		boolean swapped = true;
		while (swapped){
			swapped=false;
			
			for (int i=0;i<releases.size()-1;i++){
				Album a1 = releases.get(i);
				Album a2 = releases.get(i+1);
				
				if (a1.getYear()<a2.getYear()){
					swapped=true;
					releases.set(i, a2);
					releases.set(i+1, a1);//Possible loss of a1?
				}
				else if(a1.getYear()==a2.getYear()){
					if (a1.getName().compareTo(a2.getName())>0){
						swapped=true;
						releases.set(i, a2);
						releases.set(i+1, a1); //Same here, boss
					}
				}
			}
		}
	}
	
	public static void setOwnedState(ArrayList<Album> releases,HashSet<String> ownedNames){
		//For each owned album
		for (String albumName: ownedNames){
			//Find in releases and set owned to true
			for (Album alb: releases){
				if (alb.getName().trim().toLowerCase().equals(albumName.trim().toLowerCase())){
					alb.setOwned(true);
					break;
				}
			}
		}
	}
}
