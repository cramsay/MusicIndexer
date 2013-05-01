import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.JMenuBar;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JTable;
import javax.swing.event.TreeModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.JScrollPane;

/**
 * GUI is the graphical interface between the user and
 * the main engine
 * @author Craig
 *
 */
public class GUI extends JFrame{
	
	private static Logger log = Logger.getLogger(GUI.class.getCanonicalName());
	
	private CollectionEngine engine;
	private JTable table;
	private JTree libTree;
	
	public GUI(){
		
		engine = new CollectionEngine();
		engine.populateArtistNames();
		engine.searchForArtistDetails();
		engine.populateAlbums();
		engine.SaveCollectionToDisk(new File("collection.ser"));
		//engine.ReadCollectionFromDisk(new File("collection.ser"));
		
		//Start SWING stuff
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			log.warning("Couldn't set native 'look and feel'");
		}
		
		setTitle("CRamsay's Music Indexer");
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnLoadState = new JButton("Load State");
		panel.add(btnLoadState);
		
		JButton btnSaveState = new JButton("Save State");
		panel.add(btnSaveState);
		
		JButton btnScanMusic = new JButton("Scan Music");
		panel.add(btnScanMusic);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		libTree = new JTree();
		
		tabbedPane.addTab("Library", null, libTree, null);
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Sort", null, scrollPane, null);
		
		TableModel model = new DefaultTableModel(engine.getAlbumDetailsArray(),
					new String[] {"Artist", "Album", "Year", "Owned?"	}
				);
		table = new JTable();
		table.setModel(model);
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(sorter);
		
		scrollPane.setViewportView(table);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		super.setVisible(true);
		super.pack();
		
		populateLibraryTree();
		makeGUI();
	}
	
	private void populateLibraryTree(){
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Music", true);
		for (Artist art : engine.getArtists()){
			DefaultMutableTreeNode curArt = new DefaultMutableTreeNode(art.getName());
			for (Album alb : art.getReleases()){
				DefaultMutableTreeNode curAlb = new DefaultMutableTreeNode(alb.getName()+ 
													" ("+alb.getYear()+")");
				curArt.add(curAlb);
			}
			root.add(curArt);
		}
		DefaultTreeModel model = new DefaultTreeModel(root);
		libTree.setModel(model);
	}
	
	private void makeGUI(){
		
	}
}
