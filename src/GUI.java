import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JScrollPane;

/**
 * GUI is the graphical interface between the user and
 * the main engine
 * @author Craig
 *
 */
public class GUI extends JFrame{
	
	private static Logger log = Logger.getLogger(GUI.class.getCanonicalName());
	private static JFileChooser musicChooser = new JFileChooser();
	private static JFileChooser stateChooser = new JFileChooser();
	
	private CollectionEngine engine;
	
	private JTable albumTable;
	private JTree libTree;
	private JButton btnScanMusic;
	private JButton btnSaveState;
	private JButton btnLoadState;
	
	public GUI(){
		
		//Initialise engine (Move this to button call later)
		engine = new CollectionEngine();
		
		//Set up file choosers
		musicChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		stateChooser.setFileFilter(new FileNameExtensionFilter("Save state files (.ser)", "ser"));
		
		//Start SWING stuff
		makeGUI();
		populateLibraryTree();
		populateAlbumTable();
		
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
	
	private void populateAlbumTable(){
		
		TableModel model = new DefaultTableModel(engine.getAlbumDetailsArray(),
				new String[] {"Artist", "Album", "Year", "Owned?"	}
			);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		albumTable.setModel(model);
		albumTable.setRowSorter(sorter);
	}
	
	private void loadState(){
		int res = stateChooser.showOpenDialog(this);

		if (res!=JFileChooser.APPROVE_OPTION){
			log.info("User dismissed file chooser");
			return;
		}
		engine.ReadCollectionFromDisk(stateChooser.getSelectedFile());
		refreshGUI();
	}
	
	private void saveState(){
		int res = stateChooser.showSaveDialog(this);
	
		if (res!=JFileChooser.APPROVE_OPTION){
			log.info("User dismissed file chooser");
			return;
		}
		
		String file = stateChooser.getSelectedFile().toString();
		if (!file.endsWith(".ser"))
			file+=".ser";
		engine.SaveCollectionToDisk(new File(file));
	}
	
	private void scanMusic(){
		int res = musicChooser.showOpenDialog(this);
		if (res!=JFileChooser.APPROVE_OPTION){
			log.info("User dismissed file chooser");
			return;
		}
		engine.clearCollection();
		engine.scanFileCollection(musicChooser.getSelectedFile());
		engine.populateArtists();
		engine.populateAlbums();
		refreshGUI();
	}
	
	private void refreshGUI(){
		populateLibraryTree();
		populateAlbumTable();
	}
	
	private void makeGUI(){
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
		
		btnScanMusic = new JButton("Scan Music");
		panel.add(btnScanMusic);
		btnScanMusic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scanMusic();	}});
		
		btnLoadState = new JButton("Load State");
		panel.add(btnLoadState);
		btnLoadState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadState();	}});
		
		btnSaveState = new JButton("Save State");
		panel.add(btnSaveState);
		btnSaveState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveState();	}});
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		libTree = new JTree();
		
		tabbedPane.addTab("Library", null, libTree, null);
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPane.addTab("Sort", null, scrollPane, null);
		
		TableModel model = new DefaultTableModel(engine.getAlbumDetailsArray(),
				new String[] {"Artist", "Album", "Year", "Owned?"	}
			);
		albumTable = new JTable();
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
		albumTable.setRowSorter(sorter);
		
		scrollPane.setViewportView(albumTable);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
		super.setVisible(true);
		super.pack();
	}
}
