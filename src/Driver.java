/**
 * Includes the main method for the package.
 * It will dispatch execution to either the CLI or GUI
 * interface depending on the given parameters.
 * @author Craig
 *
 */
public class Driver {

	 public static void main(String[] args) {
		 CollectionEngine col = new CollectionEngine();
		 col.populateArtistNames();
		 col.searchForArtistDetails();
		 col.populateAlbums();
		 
	 }
}
